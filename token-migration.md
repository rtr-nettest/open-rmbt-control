# Measurement-server token migration — v1 (SHA1) → v2 (SHA256, IP + time bound)

## What's implemented ✅

The control server issues a **combined** `test_token` that carries both versions in one string:

```
<v1 token>_#v2#<base64 v2 token>
```

- The **v1 part** is unchanged: `<openTestUuid>_<testSlot>_<base64(HmacSHA1("uuid_slot", testServer.key))>`.
- The **v2 part** (after the `#v2#` marker) is the `open-rmbt-udp-ping` schema token:
  ```
  base64( time(4 BE) ‖ HMAC-SHA256(key, time)[0..8] ‖ HMAC-SHA256(key, time ‖ ipv6_16)[0..4] )
  ```
  - `key` = `test_server.key` (same per-server shared secret as v1).
  - `time` = the test slot (unix seconds, low 32 bits — periodicity 2³² s).
  - `ipv6_16` = the client source IP as IPv4-mapped IPv6 (16 bytes).

The v2 part is appended only when the client source IP is known. It binds **source IP + time**.

Example:
```
8723358c-2037-4029-a70c-91e5d9d35cf3_1700000000_oFAdP8+Cw9TqvJOgNc5ABOQRxss=_#v2#ZVPxAEzErM6+VBk3HmTzPw==
```

### Why this shape
- **No client change.** The whole combined string lives in the existing `test_token` field; the client forwards the same token to every server. (An earlier attempt added a separate `ping_token` JSON field — rejected because it would require a client update.)
- **Old measurement servers keep working.** They parse the leading `uuid_ts_hmacSha1` and ignore the trailing `_#v2#…` field. The v1 part is byte-for-byte unchanged.
- **New measurement servers** (`open-rmbt-server`) detect the `#v2#` marker and validate the v2 part (source IP + time); see that repo's `token-migration.md`.
- **The control server's own lookups are unchanged.** Because the token still *starts* with the v1 token, `request.getTestToken().split("_")[0]` is still the `open_test_uuid`, so `/result` (`findAndLockByUuidOrOpenTestUuid`) and `/resultQoS` (`findByOpenTestUuid` + full-token equality) work as before — no `findByToken` rework needed.
- **One token, two servers.** The v2 part is identical to what `RmbtUdpTokenFactory` / the reference `makeToken.py` produce, so it also authenticates the UDP-ping server.

### Code
- `TestSettingsFacade` builds `token = <v1>` then appends `"_#v2#" + base64(RmbtUdpTokenFactory.createToken(testServer.getKey(), clientAddress, testSlot))` when `clientAddress != null`; stores it on `test.token` and returns it as `test_token`.
- `RmbtUdpTokenFactory` produces the 16-byte v2 token (verified byte-for-byte by `RmbtUdpTokenFactoryTest`).
- `TestSettingsResponse` carries it in the existing `test_token` field (the separate `ping_token` field was removed).

### Verification
`mvn test` on JDK 25 — full suite green (292 tests). Key coverage: `TestSettingsFacadeTest`,
`RegistrationControllerTest`, `RmbtUdpTokenFactoryTest`, `RmbtTokenFactoryTest`.

## Caveats
- **Source-IP authority (make-or-break for v2):** the control server hashes the IP it sees at
  `/testRequest`; the measurement server checks the IP it sees on the measurement connection. If
  NAT/proxy/CDN differs between the client→control and client→measurement paths, the v2 IP HMAC won't
  match. The v1 part still validates, so this only matters where a server enforces v2.
- **Clocks:** v2 still uses the accept window, so client/server clocks must be roughly in sync.

## Alternatives considered (not taken)
- **Separate `ping_token` JSON field** — needs a client update; rejected.
- **Per-server `token_version` capability flag + a pure `v2.<…>` token** — cleaner single-format token,
  but requires control-server capability tracking and breaks the in-token `open_test_uuid` lookup.
  The combined `<v1>_#v2#<v2>` form avoids both by keeping the v1 token intact as the prefix.
