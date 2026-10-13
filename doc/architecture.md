# RMBT Control Server — Architecture

## 1. What this server is

**RMBT** ("RTR Multithreaded Broadband Test") is the engine behind **RTR-Netztest**, the public
internet-quality measurement service operated by RTR-GmbH. An end user runs a measurement from
a browser, a mobile app, or a desktop client. That measurement consists of a download test, an
upload test, a ping/latency test, and optionally a battery of **QoS** tests (DNS, TCP, UDP,
VoIP, web-page, traceroute, …) and **signal measurement**.

The system has several cooperating servers:

| Server | Repo | Role |
|--------|------|------|
| **Control Server** | `open-rmbt-control` (this repo) | The brain. Registers tests, hands out tokens, stores results, computes classifications, serves result pages. |
| Measurement Server(s) | (separate) | The "speed" peers the client actually pushes/pulls bytes against. The Control Server only *tells the client which one to use* (plus a lightweight periodic reachability/latency PING — see §10). |
| QoS Server | `open-rmbt-qos` | Runs the active QoS probes (UDP/TCP/VoIP/…). |
| Map Server | `open-rmbt-map` | Serves the public map / tiles of aggregated results. |
| Statistics Server | `open-rmbt-statistics` | Serves statistics, open-data exports, the results search. |

**The Control Server never moves measurement payload bytes.** It is a transactional REST/JSON
API plus a PostgreSQL database. Its job is orchestration and bookkeeping: who is testing, which
measurement server to use, what the results were, and what those results *mean* (good/medium/bad
classifications, QoE estimates).

---

## 2. Technology stack

- **Language / runtime:** Java 17.
- **Framework:** Spring Boot 3.x (Spring MVC, Spring Data JPA, Spring Security).
- **Packaging / hosting:** built as a **WAR** (`<packaging>war</packaging>`) and deployed into an
  external **Apache Tomcat 10**. The class `RTRApplication` extends `SpringBootServletInitializer`
  so the same artifact can also be run standalone (`main()`), which is how you debug it in the IDE.
- **Persistence:** PostgreSQL with the **PostGIS** extension. Hibernate 6 via Spring Data JPA;
  **hibernate-spatial** + **postgis-jdbc** + **JTS** (`org.locationtech.jts.geom.Geometry`) for
  geographic columns. **Flyway** manages schema migrations.
- **Mapping:** DTO ↔ entity translation through a mapper layer (interfaces in `mapper`, impls in
  `mapper/impl`; MapStruct is on the build path, some impls are hand-written).
- **API docs:** springdoc-openapi (Swagger UI at `/swagger-ui`).
- **Auxiliary libraries:** Lombok (boilerplate), Guava, commons-lang3/io, **MaxMind GeoIP2**
  (geo/ASN lookup), **dnsjava**, **semver4j** (client-version checks), **woothee** (User-Agent
  parsing), **JavaMelody** (runtime monitoring), logstash-logback-encoder (structured logging).

**It's a fairly textbook layered Spring Boot service** — the
domain (broadband measurement) is the unusual part, not the framework usage.

---

## 3. High-level architecture

The code is a classic layered architecture. Requests flow downward; data flows back up.

```
                        HTTP (JSON)
                            │
        ┌───────────────────────────────────────────┐
        │  Servlet filters                          │   ApiLoggingFilter, JavaMelody,
        │  + Spring Security filter chain           │   Spring Security
        └───────────────────────────────────────────┘
                            │
                  ┌──────────────────┐
                  │   Controllers    │   thin; map URL → method, (de)serialize DTOs
                  │  (controller/)   │
                  └──────────────────┘
                            │
            ┌───────────────────────────────┐
            │  Facade / Services            │   business logic, @Transactional boundaries
            │  (facade/, service/, impl/)   │
            └───────────────────────────────┘
              │                     │
      ┌───────────────┐     ┌───────────────┐
      │  Mappers      │     │ Repositories  │   Spring Data JPA + custom queries
      │ (mapper/)     │     │ (repository/) │
      └───────────────┘     └───────────────┘
                                    │
                          ┌──────────────────┐
                          │ Entities (model/)│  JPA @Entity, incl. PostGIS geometry
                          └──────────────────┘
                                    │
                            PostgreSQL + PostGIS
```

Cross-cutting concerns wrap around all of this:

- **`advice/RtrAdvice`** — global `@RestControllerAdvice`: turns exceptions into clean JSON error
  responses (so a validation failure is a 400 with `{"error":[...]}`, not a 500 + stack trace).
- **`filter/ApiLoggingFilter`** — logs every request/response (method, path, body, headers).
- **`config/`** — all the wiring: security, datasource, CORS, Jackson, logging, OpenAPI, etc.

---

## 4. The measurement lifecycle

This is the heart of the system. A single speed test is **not** one request — it is a short
conversation between the client and the Control Server, with the actual byte-pushing happening
against a *different* (measurement) server in between. Understanding this sequence explains why
the endpoints exist.

```
 CLIENT                          CONTROL SERVER                      MEASUREMENT SERVER
   │                                  │                                     │
   │  1. POST /settings ──────────────►  returns client uuid, URLs,         │
   │  ◄───────────────────────────────  terms, classification thresholds    │
   │                                  │                                     │
   │  2. POST /testRequest ───────────►  RegistrationController →           │
   │                                  │  TestSettingsFacade.updateTestSettings:
   │                                  │   • validate client name/version    │
   │                                  │   • create a `test` row (status     │
   │                                  │     implicitly "started")           │
   │                                  │   • pick a measurement server       │
   │                                  │     (by server type, GeoIP country) │
   │                                  │   • mint a test_token (HMAC-signed) │
   │  ◄───────────────────────────────  returns test_token, server addr,    │
   │     test_uuid, result_url, …     │  ports, #threads, #pings            │
   │                                  │                                     │
   │  3. run download/upload/ping ─────────────────────────────────────────►  (raw bytes;
   │                                  │                                     │   control server
   │                                  │                                     │   not involved)
   │                                  │                                     │
   │  4. POST /result ────────────────►  ResultController →                 │
   │     (speeds, pings, geo,         │  ResultServiceImpl.processResultRequest (@Transactional):
   │      signals, radio cells, …)    │   • find test by token uuid         │
   │                                  │   • verify status == STARTED        │
   │                                  │   • persist all sub-results         │
   │                                  │   • derive network type, operator   │
   │                                  │   • status FINISHED/ERROR/ABORTED   │
   │  ◄───────────────────────────────  {"error":[]}                        │
   │                                  │                                     │
   │  5. POST /resultQoS  (optional) ─►  QosMeasurementController →         │
   │     (QoS probe outcomes)         │  QosMeasurementServiceImpl: store & │
   │                                  │  evaluate against qos_test_objective│
   │                                  │                                     │
   │  6. POST /testresult ────────────►  fetch the finished, classified     │
   │  ◄───────────────────────────────  result for the result page          │
```

Key consequences of this design:

- **Tokens & state.** `/testRequest` creates a `test` row and an **HMAC-signed `test_token`**
  (`uuid_timestamp_signature`). `/result` and `/resultQoS` present that token; the server splits
  it, looks up the `test`, and checks the HMAC and the **status**. A test must be `STARTED` to
  accept a result — re-submitting a finished test is rejected (you'll see
  `Invalid status of test to be updated`).
- **Atomicity.** `/result` and `/resultQoS` write many child rows (pings, speeds, geolocations,
  signals, radio cells, QoS results). These handlers are `@Transactional` so a failure rolls
  everything back instead of leaving half-written, un-retryable data.
- **Idempotency caveat.** Sub-tables like `radio_cell` have client-supplied UUIDs with unique
  constraints, so a *retry after a partially-committed failure* could collide — the transactional
  boundary is what keeps this clean.
- **Measurement server selection** (`TestSettingsFacade`): based on the requested server type
  (`RMBThttp` / `RMBTws` / `RMBT`), the client's **GeoIP country** (`GeoIpHelper.lookupCountry`),
  and optionally a preferred server.

---

## 5. Endpoint map

All paths are defined as constants in **`constant/URIConstants`** — that file is your index of
the public surface. Grouped by purpose:

**Measurement flow (public):**
- `POST /settings` — client bootstrap: uuid, server URLs, T&C, classification thresholds, QoS
  test-type descriptions. (`RTRSettingsController` → `RtrSettingsService`.)
- `POST /testRequest` — register a test, get token + measurement-server details.
  (`RegistrationController` → `TestSettingsFacade`.)
- `POST /result` — submit a finished measurement. (`ResultController` → `ResultService`.)
- `POST /resultQoS`, `POST /qosTestRequest`, `POST /qosTestResult` — QoS objectives & results.
  (`QosMeasurementController` → `QosMeasurementService`.)
- `POST /testresult`, `POST /testresultdetail` (deprecated) — fetch a classified result.
  (`TestController` → `TestService`.)
- `POST /signalRequest`, `/signalResult`, `/coverageRequest`, `/coverageResult` — dedicated
  signal-monitoring / coverage measurements. (`SignalController`.)
- `GET /version`, `GET /requestDataCollector`, `POST /ip` — metadata / client environment.
- `POST /history`, `POST /sync` — per-client test history and account sync. (`ClientController`,
  `TestController`.)
- `GET /providers`, `GET /qos/O{open_test_uuid}` — lookups / public QoS result view.

**Admin (secured — see §7):**
- `/admin/news`, `/admin/settings`, `/admin/setImplausible`, `/reports/signal`,
  `/measurementServer`.

Swagger UI (`/swagger-ui`) renders all of these from the controller annotations.

---

## 6. Package-by-package guide

Everything is under `at.rtr.rmbt`. Here is what each package is for and how to read it.

### `controller/`
Thin HTTP adapters. A controller method binds a URL (constant from `URIConstants`), accepts a
**request DTO** (`@RequestBody`), delegates to a service/facade, and returns a **response DTO**.
There is almost no logic here. Start here when you want to find "what handles endpoint X".

### `facade/`
Currently one class: **`TestSettingsFacade`**. A facade orchestrates *several* services for a
flow too big for a single service — here, the whole `/testRequest` registration (client lookup,
loop-mode settings, server selection, token minting, response assembly). Treat it as a service
that happens to coordinate other services.

### `service/` + `service/impl/`
The business-logic layer. Convention: an **interface** in `service/` and an **`…Impl`** in
`service/impl/`. Controllers depend on the interface. This is where validation, transactions, and
domain rules live. The important ones:
- **`ResultServiceImpl`** — processes `/result` (the most code-dense flow): persists pings,
  speeds, geolocations, radio cells, signals; derives network type and operator; finalizes status.
- **`QosMeasurementServiceImpl`** — stores QoS results and **evaluates** them against the stored
  `qos_test_objective` definitions to produce pass/fail descriptions.
- **`RtrSettingsServiceImpl`** — assembles the big `/settings` payload.
- **`SignalServiceImpl`**, **`TestServiceImpl`**, **`TestServerServiceImpl`**, plus per-sub-result
  services (`PingService`, `SpeedService`, `GeoLocationService`, `RadioCellService`,
  `RadioSignalService`, `CellLocationService`, `SignalService`).
- **`QoeClassificationService`** / **`GeoAnalyticsService`** — turn raw numbers into the
  quality-of-experience categories and geo aggregates shown on result pages.

### `repository/` + `repository/impl/`
Spring Data JPA repositories (interfaces extending `JpaRepository` / `PagingAndSortingRepository`).
Most methods are derived queries or `@Query` (some native SQL, including spatial). Two things to
know:
- **`CustomRepository` / `CustomRepositoryImpl`** is a shared base repository
  (`repositoryBaseClass = CustomRepositoryImpl.class` in `RTRApplication`). It adds operations the
  standard repository lacks, notably **`refresh(entity)`** (re-read DB-computed columns; it flushes
  first so it never discards your pending writes) and a native bulk **`updateImplausible(...)`**.
- Native queries that join sub-tables (e.g. deriving the aggregate network type per test) live
  here; treat them as the read side of the domain.

### `model/`
JPA `@Entity` classes — the database in Java form. The central one is **`Test`** (table `test`):
the row created at `/testRequest` and filled in at `/result`. It carries the uuid/open-test-uuid,
token, client link, speeds, pings, network info, a PostGIS `Geometry` location, status, etc.
Other notable entities: `RtrClient`, `TestServer`, `TestServerQuality` (reachability/latency
samples — see §10), `QosTestResult`, `QosTestObjective`, `QosTestTypeDesc`, `RadioCell`, `Signal`,
`GeoLocation`, `News`, `Provider`, `NetworkType`.

Domain-specific persistence details to be aware of:
- **PostGIS geometry** columns map to JTS `Geometry` (needs hibernate-spatial; queries use
  `ST_*` functions).
- **The `qostest` Postgres enum.** `qos_test_objective.test` / `qos_test_type_desc.test` are a
  native Postgres `enum` type. The Java side is the `TestType` enum, mapped via a custom Hibernate
  `UserType` (`model/type/TestTypeUserType`) that binds with `Types.OTHER` so Postgres casts the
  string to the enum. **Do not** reintroduce a plain `String` converter for these — it produces
  `column "test" is of type qostest but expression is of type character varying`.
- **Reference data is `@Immutable`.** `QosTestObjective` and `QosTestTypeDesc` are read-only
  catalog tables; the entities are annotated `@org.hibernate.annotations.Immutable` so Hibernate
  never tries to UPDATE/INSERT them on flush.

### `request/` and `response/`
**DTOs** — the JSON contract. `request/*` are inbound bodies (e.g. `ResultRequest`,
`TestSettingsRequest`, `QosResultRequest`); `response/*` are outbound (e.g. `TestSettingsResponse`,
`ErrorResponse`). These are Lombok data classes — deliberately dumb. They are intentionally
*separate* from `model/` entities so the wire format and the schema can evolve independently.

### `mapper/` + `mapper/impl/`
Translation between DTOs and entities (e.g. `TestMapper.updateTestWithResultRequest(request, test)`
copies request fields onto the managed `Test` entity). Keeping this in one layer means controllers
and services never hand-copy fields.

### `dto/`
Internal value objects that are neither the persisted entity nor the wire DTO — e.g. QoS result
sub-structures under `dto/qos/` (`AbstractResult`, `DnsResult`, `TcpResult`, `VoipResult`, …) used
while evaluating QoS outcomes.

### `enums/`
Domain enumerations: **`TestType`** (the QoS test kinds — values like `udp`, `tcp`, `website`;
note the lowercase wire values differ from the Java constant names), `TestStatus`
(`STARTED`/`FINISHED`/`ERROR`/`ABORTED`/…), `ServerType` (`RMBThttp`/`RMBTws`/`RMBT`),
network/signal classifications, etc.

### `constant/`
Static constants: **`URIConstants`** (all endpoint paths — your endpoint index) and
**`ErrorMessage`** (canonical error strings used both in responses and logs).

### `utils/`
Stateless helpers: **`GeoIpHelper`** (MaxMind country/ASN lookup with lazy DB loading and retry
back-off), `HelperFunctions` (IP anonymisation, NAT-type detection, HMAC, AS info), `QosUtil`
(QoS evaluation), `BandCalculationUtil` (radio frequency → band), header extraction, etc.

### `filter/`
**`ApiLoggingFilter`** — a servlet `Filter` that wraps request/response, assigns a request id
(MDC), and logs method, path, parameters, (textual) body and headers. It guards against logging
binary bodies and treats client disconnects as a friendly message rather than a stack trace.

### `advice/`
**`RtrAdvice`** — `@RestControllerAdvice`. Central exception → HTTP mapping. Examples:
`IllegalArgumentException` → 400 `{"error": message}` (no stack trace); `TestNotFoundException` →
400; `SQLException` → 500 with a generic message; `EmptyClientVersionException` → 200 empty (a
deliberately tolerated case); JSON parse errors → 400 with the offending field. **When you throw
from a service, choose an exception this advice already maps** so the client gets a clean response.

### `config/`
All cross-cutting wiring (detailed in §7–§8): `WebMvcConfiguration` (security + CORS + MVC),
`DataSourceConfig`, `ClientTenantConfig` + `MultiTenantFlywayMigrationStrategy`, `JacksonConfig`,
`ClockConfiguration`, `OpenApiConfiguration`, `ApiLoggingFilterConfig`, `LoggingConfigurer`,
`RollBackService`, `UUIDGenerator`.

### `exception/`
Custom exceptions (`TestNotFoundException`, `ClientNotFoundException`,
`NotSupportedClientVersionException`, `InvalidSequenceException`, `SyncException`,
`EmptyClientVersionException`, …) — paired with handlers in `RtrAdvice`.

### `properties/`
`ApplicationProperties` — typed binding of `app.*` config (default language, durations, thread
counts, allowed client names, …), enabled via `@EnableConfigurationProperties` in `RTRApplication`.

---

## 7. Cross-cutting concerns

### Security (`config/WebMvcConfiguration`)
Spring Security is enabled (`@EnableWebSecurity`). The filter chain:
- **CSRF disabled, CORS handled by MVC config** (this is a token-based JSON API, not a
  cookie/session web app).
- **Public measurement endpoints** (`/settings`, `/testRequest`, `/result`, `/resultQoS`,
  `/testresult`, `/signalRequest`, …) are `permitAll()` — they are protected by the HMAC test
  **token**, not by an authenticated session.
- **Admin endpoints** (`/admin/**`, `/measurementServer`, `/reports/signal`) require OAuth2/JWT
  **authorities** (e.g. `write:implausible`, `read:reports/signal`) plus a client scope. The
  `auth0.issuer` / `auth0.apiAudience` settings configure the JWT validation.
- `anyRequest().authenticated()` is the default-deny backstop for anything not explicitly listed.

Mental model: **the public API trusts the signed token; the admin API trusts a JWT.**

### Error handling
See `RtrAdvice` (§6). The contract is: a recoverable/expected problem comes back as HTTP
4xx with `{"error":[...]}`; only genuinely unexpected failures become 5xx. Prefer throwing a
mapped exception over returning ad-hoc error structures.

### Logging (`config/LoggingConfigurer` + `logback.xml`)
`logback.xml` is a **console-only baseline that can never fail to load**. At runtime,
`LoggingConfigurer` (an `ApplicationReadyEvent` listener) reads the deployment context
(Tomcat `conf/context.xml` `<Parameter>` entries, or JVM system properties / env vars) and
reconfigures logging:
1. `LOGGING_CONFIG_FILE_CONTROL` → load that logback file verbatim;
2. else `LOG_HOST` set → ship INFO to **Logstash** and limit the console to ERROR;
3. else → stay console-only.
This indirection exists because Tomcat `<Parameter>` values aren't visible to logback during its
early initialisation, so we apply them after the context is up. Request-level logging is done by
`ApiLoggingFilter`.

### Monitoring & docs
**JavaMelody** (`javamelody-spring-boot-starter`) gives a runtime monitoring console; Spring Boot
**Actuator** exposes health/metrics; **springdoc** publishes the OpenAPI/Swagger UI.

### Internationalisation
`SystemMessages*.properties` (loaded by the `messageSource` bean in `RTRApplication`) provide
localized strings; many responses pick a `Locale` from the client's `language`.

---

## 8. Persistence, schema & multi-tenancy

- **Hibernate/JPA**, entities in `model/`, repositories in `repository/`. Reads that span
  sub-tables use native `@Query` SQL (including PostGIS `ST_*`).
- **Flyway** owns the schema. Migrations live in `src/main/resources/db/migration` (`V2__…` … and
  the test/baseline `V1__init_db.sql` under test resources). On boot, Flyway runs pending
  migrations.
- **Multi-tenancy.** The deployment can serve multiple "clients" (white-label tenants), each with
  its **own database**. `ClientTenantConfig` maps a client name → database name;
  `MultiTenantFlywayMigrationStrategy` migrates **every** configured tenant DB at startup (the
  datasource URL is a template, `String.format`-ed with the tenant DB name). The runtime datasource
  itself is the standard Spring Boot-configured one. If you add a migration, remember it must apply
  cleanly to *all* tenant databases.
- **The `qostest` enum** and **`@Immutable` reference tables** — see §6 (`model/`). These two are
  the most common "why did my QoS write fail" gotchas.

---

## 9. Database triggers & server-side post-processing

A large amount of per-test derivation does **not** happen in Java — it happens in **PL/pgSQL
triggers on the `test` table**. This matters because a trigger fires for **every** writer of the
table (the control server, the statistics/map servers, batch jobs, migrations, manual `UPDATE`s),
so many `test` columns are filled in by the database regardless of which code wrote the row.

### The `test` trigger

`trigger_test()` is a row-level `BEFORE INSERT OR UPDATE` trigger that runs **inside the same
transaction** as the write and rewrites/derives many `NEW` columns. It bundles ~15 concerns:

| Category | Examples |
|---|---|
| **Scalar derivations** | `speed_download_log`, `speed_upload_log`, `ping_shortest_log`, `ping_median(_log)`, `network_group_name/type` |
| **Reference lookups** | `roaming_type`, `mobile_provider_id`, `mobile_network_id`, `mobile_sim_id`, `mobile_provider_id2` (`mccmnc2provider`, `mccmnc2name`, `provider`, `network_type`) |
| **Cross-test logic** | `open_uuid` (loop-mode finalization), `similar_test_uid`/`pinned` (statistics pinning), `dist_prev`/`speed_prev` |
| **Spatial / PostGIS** | upsert into `test_location`, `location_max_distance`, Austria-boundary checks (`rmbt_get_distance_iso_a2`), `radio_signal_location` interpolation |
| **Plausibility flags** | sets `status='UPDATE ERROR'`, `implausible`, `deleted`, `comment` (exclude CLI `#211`, AT operators outside AT `#272`, `model='unknown'` `#356`, …) |

A second trigger, **`trigger_test_location()`**, sits on the `test_location` table and performs the
heavy **GIS enrichment** (below).

### How the location reaches the database (Java side)

The location stored on a `test` row comes from the **client-submitted points** in a request
(`/result`, `/signalResult`, `/coverageResult`). Two helpers put it on the row:

- **`GeoLocationServiceImpl.processGeoLocationRequests`** saves every point to `geo_location`,
  picks the **most accurate** one, and `updateTestGeo(...)` copies its `geo_location_uuid`,
  `geo_accuracy`, `geo_long`, `geo_lat`, `geo_provider` onto `test`.
- **`TestMapperImpl.updateTestLocation`** derives the projected geometries `location` (EPSG:900913),
  `geom4326`, `geom3857` from that lat/long.

The control server **never writes `test_location`** — the `TestLocation` entity is mapped read-only
(the inverse side of the association).

### How the trigger processes the location

On save, `trigger_test()` upserts the basic location columns into `test_location` (keyed by
`open_test_uuid`), but **only when the geometry changed** (`NEW.geom4326 IS DISTINCT FROM
OLD.geom4326` and `geo_location_uuid` set), copying `geom4326`/`geom3857`/`location`/`geo_lat`/
`geo_long`/`geo_accuracy`/`geo_provider` straight from the `test` row. That write then fires
`trigger_test_location()`, which **enriches** the row via PostGIS spatial joins (only for
`geo_accuracy <= 2000`): `settlement_type` (`dsr`), `link_*`/`frc`/`edge_id` (`linknet`),
`gkz_bev`/`kg_nr_bev` (`bev_vgd`), `land_cover`, `country_location`, raster cells, `dtm_level`, …

```
Java: best client point → test.geo_* + geom4326/geom3857
        → trigger_test()          : upsert basic columns into test_location
        → trigger_test_location() : GIS enrichment of that row
```

`trigger_test()` afterwards re-reads `country_location` back from `test_location` and uses it for the
mobile-provider logic — an ordering dependency to respect.

### What it would mean to move this into Java

This is a recurring question, so the trade-offs are worth stating.

- **It is a split, not an elimination.** The spatial joins against large GIS tables (`dsr`,
  `linknet`, `bev_vgd`, boundaries, rasters) and functions like `ST_DistanceSpheroid` belong in
  PostGIS for performance; you would move *invocation* into Java, not the computation.
- **The decisive caveat:** the trigger guarantees these invariants for **every** writer. Java logic
  only runs on application code paths, so any out-of-band write to `test` (sibling servers, SQL,
  admin fixes) would silently skip the derivations — unless all writes go through the app or a thin
  trigger is kept as a backstop.

| Concern | Portability | Notes |
|---|---|---|
| Scalar derivations | Easy | arithmetic / one query |
| Reference lookups | Medium | many small queries; cache reference data |
| Cross-test logic | Hard | queries other `test` rows; partly spatial |
| `test_location` basic upsert | Easy–Medium | column copy; make `TestLocation` writable |
| GIS enrichment, boundary/movement, interpolation | Keep in DB | PostGIS-bound |
| Plausibility flags / status rewrites | Medium | replicate guard conditions exactly |

**Pros:** testable/debuggable/versioned with the app; no PL/pgSQL "magic" rewriting `NEW.*`; removes
real trigger overhead (it even runs `pg_locks` debug loops and `RAISE NOTICE` on every write); rules
evolve without DB migrations. **Cons:** the trigger encodes years of edge cases
(`#211, #272, #356, #664, #668, #759, …`) that are easy to port wrongly; loss of the
single-source-of-truth invariant; ordering dependencies; per-write query volume.

**Recommendation:** don't wholesale-port. Move scalar/lookup concerns to a shared Java
post-processor *one at a time, with parity tests*, and keep spatial/GIS in PostGIS. For the coverage
path, a clean first slice is to move just the `test_location` basic upsert into Java (persist a
`TestLocation` from the geo fields already on `test`) and leave `trigger_test_location()` enrichment
in the database.

---

## 10. Scheduled background tasks

Two `@Scheduled` jobs run inside the control server (scheduling is enabled by `@EnableScheduling`
on `RTRApplication`). Each is configured under its own top-level key in `application.yml` and can be
disabled by setting its cron to `-`; failures are logged and never break the scheduler thread.

### `CleanupTask` (`service/impl/CleanupTask`)
Runs daily (default `cleanup-task.cron = "0 0 4 * * *"`). It executes the configured
`cleanup-task.statements` in order via `JdbcTemplate.execute(...)` — which, unlike `update()`,
allows value-returning calls such as `SELECT rmbt_purge_obsolete(...)` — and logs each statement's
result plus any PostgreSQL `NOTICE`. The shipped default is a harmless dummy (`select version()`);
the real privacy-purge statement is supplied per deployment via Tomcat `context.xml`.

### `TestServerQualityService` (`service/impl/TestServerQualityService`)
Runs every 5 minutes (default `test-server-quality.cron = "0 */5 * * * *"`). It measures the
reachability and latency of every **active** measurement server of `server_type = RMBThttp`, over
**both IPv4 and IPv6**, and stores one sample per family in `test_server_quality`.

This is the **one place the control server actively connects to a measurement server** — everywhere
else it only *names* the server for the client. It still moves **no payload**: it performs a single
RMBT-protocol **PING** and records the round-trip. Per server, per family:

1. Take `web_address_ipv4` / `web_address_ipv6` and the SSL port (`port_ssl`); a family whose
   address is blank is skipped.
2. Mint a fresh HMAC token signed with the server's `key` (`utils/RmbtTokenFactory`) — the same
   `uuid_timestamp_signature` scheme as the client `test_token`, with the timestamp set to "now" so
   it falls inside the server's accept window.
3. `RmbtPinger` (impl `service/quality/RmbtWebSocketPinger`) opens a TLS socket, performs the
   `GET /rmbt` **WebSocket** upgrade, runs the greeting (`RMBTv…` → `TOKEN …` → `OK`/`CHUNKSIZE`),
   then `PING → PONG → OK → TIME`. `latency_ms` is the **client-measured** PING→PONG round-trip;
   server certificates are not verified (measurement servers commonly self-sign).
4. The outcome (`protocol` 4/6, `reachable`, `latency_ms`) is written to `test_server_quality`. Any
   failure (connect / TLS / handshake / auth / timeout) is recorded as `reachable = false`. Servers
   are checked **sequentially** (there are only a few).

The transport and protocol mirror the reference implementations in `open-rmbt-server` (Rust) and the
`open-rmbt-client-cli` Java client (`RmbtConn`). The orchestration is split from the network code
behind the `RmbtPinger` interface so it is unit-testable with a mocked pinger. The
`test_server_quality` table and its indexes are created by the standalone `open-rmbt-database`
migration set (`V104__create_test_server_quality.sql`).

---

## 11. Configuration & deployment

- **Profiles** (`application.yml`): a default block plus `dev` and `prod` documents
  (`spring.config.activate.on-profile`). The active profile is selected at the Tomcat level
  (e.g. `spring.profiles.active=prod` in `catalina.properties`).
- **Externalised settings** come from environment / Tomcat `context.xml` `<Parameter>` entries:
  DB host/port/name/user/password (`CONTROL_DB_*`), `RMBT_SECRETKEY` (HMAC signing key),
  `auth0.*`, `CONTROL_ALLOWED_ORIGIN` (CORS), and the logging parameters
  (`LOG_HOST`, `LOG_PORT`, `LOGGING_HOST`, `LOGGING_CONFIG_FILE_CONTROL`).
- **Build:** `mvn clean package` → a WAR. Deploy into Tomcat 10 (`webapps/RMBTControlServer.war`).
  Java 17. The DB must exist and the `rmbt_control` role needs privileges on the schema
  (including `SELECT` on the QoS reference tables).
- **Local run/debug:** run `RTRApplication.main()` from the IDE (it boots an embedded servlet
  container). Point it at a local PostgreSQL+PostGIS with the schema migrated by Flyway.

---

## 12. Conventions you should follow

- **Layer discipline:** controller (HTTP only) → service/facade (logic, `@Transactional`) →
  repository (data) → entity. Don't put logic in controllers or SQL in services.
- **DTOs in, DTOs out:** never serialize `model/` entities directly to the client; map to a
  `response/*` DTO. Never bind request JSON straight onto an entity except through a `mapper`.
- **Transactions wrap multi-write flows.** Anything that writes several sub-tables for one logical
  operation must be `@Transactional` on the service method (see `ResultServiceImpl`,
  `QosMeasurementServiceImpl`).
- **Throw mapped exceptions.** If you need to reject a request, throw something `RtrAdvice` knows
  about (often `IllegalArgumentException` for "bad input") so the client gets a clean
  `{"error":[…]}` instead of a 500 + stack trace.
- **Tokens are the auth for public endpoints.** Validate the HMAC and the test status; never trust
  a uuid alone.
- **Reference catalog tables are read-only** (`@Immutable`); the app should never write
  `qos_test_objective` / `qos_test_type_desc`.

---

## 13. Where to start reading (a suggested path)

1. **`constant/URIConstants`** — the catalogue of endpoints.
2. **`facade/TestSettingsFacade`** — follow `/testRequest` end to end; it touches clients, server
   selection, GeoIP, tokens, and the `Test` entity, so it's a guided tour of the domain.
3. **`service/impl/ResultServiceImpl`** — follow `/result`; this is where a measurement becomes
   persisted, classified data. Notice the `@Transactional`, the status check, and `refresh(test)`.
4. **`model/Test`** — the central entity; skim its columns to learn the vocabulary.
5. **`config/WebMvcConfiguration`** — see exactly which endpoints are public vs. secured.
6. **`advice/RtrAdvice`** — learn the error contract before you add an endpoint.
7. **`service/impl/QosMeasurementServiceImpl` + `model/QosTestObjective`** — the QoS subsystem,
   including the `qostest` enum mapping.

---

## 14. Glossary

- **Test / measurement** — one run of download+upload+ping (+optional QoS). Stored as a `test` row.
- **`test_token`** — `uuid_timestamp_signature`; the HMAC-signed credential a client uses to submit
  results for its test.
- **`open_test_uuid`** — the public, shareable identifier of a test (used in result URLs / the map).
- **Measurement server** — the separate peer the client exchanges bytes with; the Control Server
  only selects and names it.
- **QoS test** — an active quality probe (DNS/TCP/UDP/VoIP/website/traceroute…), defined by a
  `qos_test_objective` and described by `qos_test_type_desc`; the kind is a `TestType` /
  Postgres `qostest` enum.
- **QoE classification** — derived "experience" categories (gaming, video, VoIP, …) computed from
  the raw numbers for the result page.
- **Signal / coverage request** — a lighter-weight measurement that reports radio signal/coverage
  without a full speed test.
- **Tenant** — a white-label client served from its own database; see §8.
- **Implausible** — an admin/automatic flag marking a test as not trustworthy (excluded from
  statistics); set via `/admin/setImplausible`.

---

*This document describes the structure and intent of the code as it stands. When in doubt, the
code is the source of truth — start from the reading path in §13 and follow the calls.*
