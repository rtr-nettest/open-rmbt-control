# Migrating the measurement-server token (SHA1 → SHA256, IP + relative time)

## Current token

Built in `TestSettingsFacade` (around line 257–263) and `RmbtTokenFactory.createToken(...)`:

```
<openTestUuid>_<testSlot>_<base64( HmacSHA1( "<openTestUuid>_<testSlot>", testServer.key ) )>
```

Example:
```
8723358c-2037-4029-a70c-91e5d9d35cf3_1781790421_FD43VXeOuRfM5yy/M1U5odJ00IQ=
```

- `testSlot` = scheduled start time in **unix seconds** — the "when"; the measurement server enforces it via an early/late accept window.
- `testServer.key` = per-server shared secret (`test_server.key`); the HMAC is server-specific.
- The base64 third field is 20 bytes → SHA1.
- Verification (measurement server `open-rmbt-server`, Rust `token.rs`): recompute `HmacSHA1("uuid_slot", key)`, compare, and check the slot is within the accept window.

### Facts that make migration tractable
1. **The control server already knows which measurement server a token is for** (it selects `testServer` and uses its `key`), so it can emit different formats per server.
2. `HelperFunctions` already provides `calculateSha256HMAC(secret, data)` (and a two-part variant) — the same `testServer.key` shared secret is reused; no new key distribution.
3. **Gap:** `TestServer` has no capability/version field today; one must be added for Strategy A.

## Target (new) token requirements

- HMAC-**SHA256** based.
- Encodes a **relative time** (periodicity of 2^32 is acceptable → 32-bit time field).
- Encodes a **hash of the source IP** (32 bits acceptable).
- New measurement servers identify the new token and validate **source IP + time**.
- **Old measurement servers must keep interpreting the old token unchanged.**

This is a cross-repo change: control server (this repo) issues the token; `open-rmbt-server` (Rust) validates it.

---

## Option A — Per-server capability flag (recommended)

Add `test_server.token_version` (or `min_protocol`), default `1`. Control server emits:
- `token_version = 1` → today's SHA1 token (untouched).
- `token_version = 2` → the new SHA256 token.

A measurement server advertises v2 support **at registration**, so the flag self-populates as servers are upgraded; existing servers stay on v1 by default. Each token is a single clean format, and a v2 token is never sent to a server that can't parse it.

**Pros:** clean single-format tokens; small; no SHA1 material in v2; self-maintaining via registration; least ambiguity.
**Cons:** needs a schema column + registration wiring; control server must track capability (handled by registration).

---

## Option B — One backward-compatible hybrid token

Keep the v1 triplet intact and **append** the new fields:

```
<uuid>_<slot>_<b64 sha1hmac>_<t32>_<ipHash32>_<b64 sha256hmac>
```

- Old server: splits, uses fields `[0..2]`, validates SHA1 as today, ignores the rest.
- New server: detects the extra fields, validates the SHA256 part + IP + time.

**Pros:** no capability tracking; one token works everywhere during transition.
**Cons:** larger; keeps SHA1 material around; **depends on the old Rust parser tolerating extra `_`-separated fields** — unverified here (it lives in `open-rmbt-server`); if the old parser is strict about field count, this breaks old servers. Use only if capability tracking is impossible.

---

## Option C — Stopgap: bump nothing, document only

Leave SHA1 in place. Not a migration; listed only for completeness. Rejected — does not meet the SHA256 / IP-binding goal.

---

## Recommended design (Option A) — concrete token format

Make v2 unmistakably distinct so a new server identifies it and an old server simply rejects it
(which is safe because A only sends v2 to v2-capable servers):

```
v2.<uuid>.<t32>.<ipHash32>.<base64url( HmacSHA256( key, "<uuid>.<t32>.<ipHash32>" ) )>
```

Use a `v2.` prefix and a separator that cannot be confused with a v1 (UUID-first, `_`-separated) token.

### Field definitions
- **`t32` (relative time, 32-bit):** `unixSeconds mod 2^32`. ~136-year periodicity, so effectively the slot stored in 32 bits. The server maps it back and checks it against its accept window with **wrap-aware** comparison at the boundary. Satisfies "periodicity of 2^32 is acceptable."
- **`ipHash32` (32-bit IP hash):** `truncate32( HmacSHA256( key, canonicalize(sourceIp) ) )`. Keyed (not a plain hash → not precomputable) and bound to the same shared secret. **Canonicalization is mandatory** — normalize IPv4 / IPv6 / IPv4-mapped-IPv6 to one agreed form on both control and measurement sides, or it will mismatch.
- **Signature:** `HmacSHA256(key, "<uuid>.<t32>.<ipHash32>")`, base64url-encoded. Because IP and time are inside the HMAC, tampering with either invalidates the signature.

### New-server validation
1. Recompute `ipHash32` from the **connecting** source IP; require equality (binds the token to the client IP).
2. Recompute the SHA256 HMAC over the canonical payload; require equality (integrity / authenticity).
3. Check `t32` falls within the accept window (wrap-aware).

---

## Open questions to resolve before implementing

- **Source-IP authority (make-or-break):** the control server sees the *client's* IP at `/testRequest`; the measurement server sees the *measurement connection's* source IP. If NAT/proxy/CDN differs between client→control and client→measurement, the hashed IP won't match. The IP binding only works if both ends observe the same address. Verify against the real topology before committing to IP binding (or make IP binding optional/advisory).
- **Cross-repo ownership:** control-server side (this repo) = migration + `token_version` + v2 factory + tests. The Rust `open-rmbt-server` validation is a separate repo (not present here) and must land **first** (accept v2 while still accepting v1).
- **UDP token:** there is a parallel `RmbtUdpTokenFactory`; decide whether it needs the same v2 treatment.

## Rollout sequence (cross-repo)

1. **`open-rmbt-server` (Rust):** recognize/validate the `v2.` token (SHA256 + IP + time) while still accepting v1. Deploy to the servers to be upgraded.
2. **Control server:** Flyway migration in `open-rmbt-database` adding `test_server.token_version` (default 1); populate it from registration capability; add `RmbtTokenFactory.createTokenV2(...)` using `calculateSha256HMAC`; branch token creation in `TestSettingsFacade`.
3. Flip individual servers to v2 once both ends are deployed. v1 servers remain untouched throughout.

## Implemented on this branch ✅ (additive, backward compatible — reuses the open-rmbt-udp-ping schema)

> **Status:** done and build-verified (`mvn test` on JDK 25 — `TestSettingsFacadeTest`,
> `RegistrationControllerTest`, `RmbtUdpTokenFactoryTest`, `RmbtTokenFactoryTest` all green).
> This is the path taken; "Option A" below (per-server `v2.` token) is the alternative that was **not**
> implemented and is retained only as a future option.
>
> **Files changed:** `TestSettingsResponse` (new `ping_token` field), `TestSettingsFacade` (generate +
> wire the token), `RegistrationControllerTest` (constructor arg). The `ping_token` byte layout reuses
> the existing, already-tested `RmbtUdpTokenFactory`.

Rather than invent a new v2 format, the new token reuses the **open-rmbt-udp-ping** schema
(`makeToken.py` / `RmbtUdpTokenFactory`, already byte-for-byte verified by `RmbtUdpTokenFactoryTest`),
so the same token can authenticate the UDP ping test:

```
ping_token = base64( time(4 BE)  ‖  HMAC-SHA256(key, time)[0..8]  ‖  HMAC-SHA256(key, time ‖ ipv6_16)[0..4] )
```
- `key` = `test_server.key` (the existing shared secret).
- `time` = the test slot (unix seconds, low 32 bits) — same time the legacy `test_token` binds.
- `ipv6_16` = the client source IP as IPv4-mapped IPv6 (16 bytes).

Delivery is **additive**: `TestSettingsFacade` still emits the legacy `test_token` unchanged and now
also sets a new `ping_token` field on `TestSettingsResponse` (mirroring the signal/coverage
`ping_token`). It is only emitted when the client source IP is known (the token binds it). Nothing
existing breaks; old measurement servers keep using `test_token`.

Caveat unchanged from below: the IP binding only validates if the address the control server sees
equals the source IP the UDP server observes.

## Suggested control-server work items (Option A — alternative, NOT implemented)

Retained only as a future option if a versioned `v2.` measurement-server token is preferred over the
additive `ping_token` above.

- DB: `ALTER TABLE test_server ADD COLUMN token_version smallint NOT NULL DEFAULT 1;` (Flyway in `open-rmbt-database`).
- Model: `TestServer.tokenVersion`.
- Registration: capture/store advertised token capability.
- `RmbtTokenFactory.createTokenV2(serverKey, uuid, unixSeconds, sourceIp)` + canonicalization + `truncate32`.
- `TestSettingsFacade`: choose v1/v2 based on `testServer.getTokenVersion()`.
- Tests: v1 unchanged (regression), v2 format + IP-hash + signature, wrap-aware time.
