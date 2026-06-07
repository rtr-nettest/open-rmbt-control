# Test-server QoS (reachability & latency monitoring)

How the RMBT Control Server continuously measures the **reachability and latency of the measurement
servers** and exposes the result. This is an operational/monitoring feature: it tells you which test
servers are up, over IPv4 and IPv6, and how fast they answer — without running a full speed test.

There are three parts:

1. A scheduled **prober** (`TestServerQualityService`) that PINGs every active server.
2. A **history table** (`test_server_quality`) that stores one sample per server / IP family / run.
3. A read **endpoint** (`/testServerStatus`) that returns the latest status + 24-hour aggregates.

---

## 1. What is measured

For every **active** test server whose `server_type` is **`RMBThttp`**, **`RMBTudp`** or **`QoS`**,
every run performs one **PING** over **both** IP families and records:

| Field | Meaning |
|---|---|
| `server_uuid` | the `test_server.uuid` |
| `protocol` | `4` (IPv4, via `web_address_ipv4`) or `6` (IPv6, via `web_address_ipv6`) |
| `reachable` | did we get a valid protocol response? |
| `latency_ms` | client-measured round-trip of the PING (`NULL` when unreachable) |
| `timestamp` | when the sample was taken |

Servers are checked **sequentially** (there are only a few). A family with no address is skipped; a
server with no `key` is skipped (no token can be built). Any failure is recorded as
`reachable = false` and never breaks the loop or the scheduler thread.

---

## 2. The schedule

`TestServerQualityService.measureAll()` is a Spring `@Scheduled` job (scheduling is enabled by
`@EnableScheduling` on `RTRApplication`):

```yaml
test-server-quality:
  cron: "0 */5 * * * *"   # Spring 6-field cron — every 5 minutes. Set to "-" to disable.
  zone: ""               # empty = JVM default time zone
  public-ipv4: ""        # optional: this control server's public IPv4 (see §3.2)
  public-ipv6: ""        # optional: this control server's public IPv6 (see §3.2)
```

Both keys are overridable per deployment (env / `context.xml`); the defaults live in `application.yml`.

---

## 3. The protocols

The control server here does the one thing it otherwise never does — it **connects to a measurement
server**. It still transfers **no payload**: it only performs a single authenticated PING.

### 3.1 RMBThttp — RMBT protocol over WebSocket/TLS (`RmbtWebSocketPinger`)

Mirrors the reference client `open-rmbt-client-cli` (`RmbtConn`) and the `open-rmbt-server`:

```
TLS connect to web_address_ipv{4,6} : port_ssl   (server cert NOT verified — self-signed is common)
  → HTTP  "GET /rmbt"  WebSocket upgrade (Upgrade: websocket, Sec-WebSocket-Key, …)
  → RMBT lines are exchanged as WebSocket TEXT frames (client frames masked):
       S: RMBTv…            S: ACCEPT TOKEN QUIT
       C: TOKEN <token>     S: OK    S: CHUNKSIZE …
       S: ACCEPT … PING …   C: PING  S: PONG   C: OK   S: TIME <ns>
```

`latency_ms` = the **client-measured** `PING`→`PONG` round-trip.

**Token** (`RmbtTokenFactory`): `<uuid>_<unixSeconds>_<Base64(HmacSHA1(uuid_unixSeconds, key))>` —
the same scheme the control server hands to real clients (`TestSettingsFacade`) and that the server
verifies (`open-rmbt-server/token.rs`). `uuid` is freshly generated and `unixSeconds` is "now" so the
token falls inside the server's accept window.

### 3.2 RMBTudp — open-rmbt-udp-ping (`RmbtUdpPinger`)

Implements the `open-rmbt-udp-ping` protocol (UDP port from `port_ssl`):

```
Request  (24 bytes):  "RP01" ‖ sequence(4) ‖ time(4) ‖ HMAC256(seed,time)[0..8] ‖ HMAC256(seed, time‖ip)[0..4]
Response (8 bytes):   "RR01" ‖ sequence    (IP HMAC matched)
                      "RE01" ‖ sequence    (IP HMAC mismatch — origin unverified)
```

The server answers **only** when the **time** HMAC is valid and within its window; if the **IP** HMAC
does not match the packet's real source address it replies `RE01` instead of `RR01`.

The IP HMAC is computed over the control server's **public source IP** as seen by the UDP server.
There are two modes, depending on whether that IP is configured:

- **Unverified (default).** When `test-server-quality.public-ipv4` / `public-ipv6` are **empty**, the
  control server does not reliably know its own public source IP (NAT / multi-homing), so it signs the
  time HMAC correctly (seed = the server's `key`, the **existing** token mechanism — see
  `SignalServiceImpl.generatePingToken`) but uses a **placeholder** source IP. The server then replies
  `RE01`, which is **accepted as reachable** — it still proves the server is up and gives the RTT.
- **Verified (optional).** When `public-ipv4` / `public-ipv6` are **set** (per IP family), the token's
  IP HMAC is built with that correct address, so a healthy server replies `RR01`; in this mode only
  `RR01` is accepted (an `RE01` is treated as **not** reachable). This gives a stricter, source-verified
  check when the deployment's public IPs are known and stable.

`latency_ms` = client RTT either way.

**Token** (`RmbtUdpTokenFactory`): the 16-byte `time ‖ HMAC256(seed,time)[0..8] ‖ HMAC256(seed, time‖ip)[0..4]`
(matching `makeToken.py` and the Rust server), wrapped in the 24-byte `RP01` request packet.

### 3.3 QoS — TLS handshake + greeting (`QosTlsPinger`)

QoS test servers (`open-rmbt-qos`) speak a **TLS line protocol** (typically port 443): right after the
TLS handshake the server sends a greeting `QoSSP<version>` followed by `ACCEPT [TOKEN string]`.
`QosTlsPinger` connects to `web_address_ipv{4,6}` on `port_ssl`, completes the TLS
handshake (cert not verified), and reads that greeting to confirm it really is a QoS server. The
**TCP-connect round-trip** is reported as `latency_ms`; a recognised greeting = reachable. No token is
needed. (A bare TCP connect is not enough — it would pass for any open port and not confirm the
service.)

---

## 4. Storage & migrations

`test_server_quality` is created by the standalone `open-rmbt-database` migration set:

- **`V103__test_server_drop_web_address.sql`** — drops the obsolete `test_server.web_address` column
  (superseded by `web_address_ipv4` / `web_address_ipv6`).
- **`V104__create_test_server_quality.sql`** — creates the table, indexes on `server_uuid` and
  `timestamp`, and grants `INSERT/SELECT/UPDATE` (+ sequence usage) to `rmbt_control`.

```sql
CREATE TABLE public.test_server_quality (
    uid         bigserial   PRIMARY KEY,
    server_uuid uuid        NOT NULL REFERENCES public.test_server (uuid),
    "timestamp" timestamptz NOT NULL DEFAULT now(),
    protocol    int4        NOT NULL,   -- 4 = IPv4, 6 = IPv6
    reachable   bool        NOT NULL,
    latency_ms  float8      NULL
);
```

The JPA entity is `model/TestServerQuality`; the repository is `repository/TestServerQualityRepository`.

---

## 5. The `/testServerStatus` endpoint

`GET /testServerStatus` (public; `permitAll`) returns, **per server and IP protocol**, the latest
sample plus 24-hour aggregates. Optional query parameters: **`server_uuid`** (a `test_server.uuid`;
surrounding single/double quotes are tolerated) and **`protocol`** (`4` or `6`). Combining both
narrows the result to a single row.

```
GET /testServerStatus                                  # all servers, both protocols
GET /testServerStatus?server_uuid=<uuid>               # one server (its v4 + v6 rows)
GET /testServerStatus?protocol=4                       # all servers, IPv4 only
GET /testServerStatus?server_uuid=<uuid>&protocol=4    # exactly one row
```

Response (JSON array; mirrors the `test_server_qos_view` columns):

```json
[
  {
    "server_uuid": "11111111-2222-3333-4444-555555555555",
    "name": "RMBT TCP/TLS",
    "server_type": "RMBThttp",
    "protocol": 4,
    "reachable": true,
    "latency_ms": 12.5,
    "max_latency_ms": 30.1,
    "min_latency_ms": 8.7,
    "reachability_pct": 99.5
  }
]
```

| Field | Source |
|---|---|
| `server_uuid` | `test_server.uuid` |
| `name` | `test_server.name` |
| `server_type` | `test_server.server_type` (`RMBThttp` / `RMBTudp` / `QoS`) |
| `protocol` | `4` / `6` |
| `reachable`, `latency_ms` | the **latest** sample for that server+protocol |
| `max_latency_ms`, `min_latency_ms`, `reachability_pct` | aggregated over the **last 24 h** |

Rows are ordered `reachable DESC, latency_ms` (reachable + fastest first).

### Implementation

The production database does **not** have the `test_server_qos_view` view; the same logic is
implemented as a native query in `TestServerQualityRepository.findStatus(...)`:

```sql
WITH latest_entries AS (                      -- newest row per (server, protocol)
    SELECT DISTINCT ON (server_uuid, protocol)
        server_uuid, protocol, reachable, latency_ms
    FROM test_server_quality
    ORDER BY server_uuid, protocol, timestamp DESC
), stats_24h AS (                             -- aggregates over the last 24h
    SELECT server_uuid, protocol,
        max(latency_ms) AS max_latency_ms,
        min(latency_ms) AS min_latency_ms,
        round(100.0 * count(*) FILTER (WHERE reachable)::numeric / NULLIF(count(*),0)::numeric, 2) AS reachability_pct
    FROM test_server_quality
    WHERE timestamp > (now() - interval '24 hours')
    GROUP BY server_uuid, protocol
)
SELECT latest.server_uuid, ts.name, ts.server_type, latest.protocol, latest.reachable, latest.latency_ms,
       stats.max_latency_ms, stats.min_latency_ms, stats.reachability_pct
FROM latest_entries latest
    JOIN test_server ts ON ts.uuid = latest.server_uuid
    LEFT JOIN stats_24h stats ON stats.server_uuid = latest.server_uuid AND stats.protocol = latest.protocol
WHERE ts.active                                                          -- only currently-active servers
  AND (CAST(:testServer AS uuid) IS NULL OR ts.uuid = CAST(:testServer AS uuid))
  AND (CAST(:protocol AS integer) IS NULL OR latest.protocol = CAST(:protocol AS integer))
ORDER BY latest.reachable DESC, latest.latency_ms;
```

Only **active** servers are returned: when a server is deactivated (`test_server.active = false`) it
disappears from the endpoint immediately, even though its historical samples remain in the table.

Rows are mapped to `TestServerStatusResponse` by `TestServerStatusResponse.fromRow(...)`
(`controller/TestServerStatusController` → `service/TestServerStatusService`).

---

## 6. Components

| Class | Role |
|---|---|
| `service/impl/TestServerQualityService` | scheduled orchestration: load servers, dispatch by type, persist |
| `service/quality/RmbtPinger` + `RmbtWebSocketPinger` | RMBThttp WebSocket/TLS PING |
| `service/quality/RmbtUdpPinger` | RMBTudp UDP PING |
| `service/quality/QosTlsPinger` | QoS TLS handshake + greeting PING |
| `service/quality/PingOutcome` | `{reachable, latencyMs}` result |
| `utils/RmbtTokenFactory` | RMBThttp HMAC-SHA1 token |
| `utils/RmbtUdpTokenFactory` | RMBTudp HMAC-SHA256 token + request packet |
| `model/TestServerQuality` + `repository/TestServerQualityRepository` | persistence + status query |
| `service/TestServerStatusService` + `controller/TestServerStatusController` | `/testServerStatus` read side |
| `response/TestServerStatusResponse` | endpoint DTO + row mapper |

The orchestration is split from the network code behind the pinger types, so it is unit-tested with
mocked pingers; the token factories and frame/packet codecs have their own unit tests. The live socket
paths (`RmbtWebSocketPinger.ping`, `RmbtUdpPinger.ping`) need a real server and are covered by
manual/integration testing.

---

## 7. Reference implementations

- RMBThttp protocol & server: `open-rmbt-server` (Rust), `rmbt-server` (C); client: `open-rmbt-client-cli`.
- RMBTudp protocol, server & clients, and `makeToken.py`: `open-rmbt-udp-ping`.
