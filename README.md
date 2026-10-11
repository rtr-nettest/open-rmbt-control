Open-RMBT-Control
=========

> *Open-RMBT* is an open source, multithreaded bandwidth measurement system.

It consists of the following components:
* Web site
* JavaScript client
* Android client
* iOS client
* Measurement server
* QoS measurement server
* <strong>Control server (in this repository)</strong>
* Statistics server 
* Map server

*Open-RMBT* is released under the [Apache License, Version 2.0](LICENSE). It was developed
by the [Austrian Regulatory Authority for Broadcasting and Telecommunications (RTR-GmbH)](https://www.rtr.at/).

Related material
----------------

* [RMBT specification](https://www.netztest.at/doc/)
* [RTR-NetTest/open-rmbt](https://github.com/rtr-nettest/open-rmbt) - Original repository, today mainly contains Map and QoS-server
* [RTR-NetTest/rmbt-server](https://github.com/rtr-nettest/rmbt-server) - Test Server for conducting measurements based on the RMBT protocol
* [RTR-NetTest/rmbtws](https://github.com/rtr-nettest/rmbtws) - JavaScript client for conducting RMBT-based speed measurements
* [RTR-NetTest/open-rmbt-statistics](https://github.com/rtr-nettest//open-rmbt-statistics) - Statistics server
* [RTR-NetTest/open-rmbt-ios](https://github.com/rtr-nettest/open-rmbt-ios) - iOS app
* [RTR-NetTest/open-rmbt-android](https://github.com/rtr-nettest/open-rmbt-android) - Android app
* [RTR-NetTest/rtr-nettest/open-rmbt-website](https://github.com/rtr-nettest/open-rmbt-website) - Web site

System requirements for the Control Server
-------------------

* single (virtual) server with sufficient RAM and CPU performance
* Fast disk (NVME) for database
* Base system Debian 13 or newer
* At least a single static public IPv4 address (IPv6 support recommended)

  *NOTE: other Linux distributions can also be used, but commands and package names may be different*

Installation
--------------

1. Setup IP/DNS/hostname
2. firewall (e.g. iptables)
3. Install git
4. Install and configure sshd
5. Install and configure ntp
6. dpkg-reconfigure locales (database requires en_US.UTF-8)
7. dpkg-reconfigure tzdata

## Database Server

The control server uses a Postgresql database. See [RTR-NetTest/open-rmbt](https://github.com/rtr-nettest/open-rmbt)
for basic setup instructions.

## Control Server

### Install components

* Apache Tomcat 10 or higher
* nginx; configure nginx as reverse-proxy to forward requests to this host on port 8080
* letsencrypt; create certificate
* JRE17 - JRE25
* [Maxmind GeoLite2 database](https://dev.maxmind.com/geoip/geolite2-free-geolocation-data)

### Build the RMBTControlServer.war archive

> mvn compile war:war

### Alternative: Get WAR using Github action

The `WAR build` action produces a WAR file that can be used on a server. This only applies to the `master` branch.

### Configure Tomcat

##### Configure catalina.properties
Edit `/etc/tomcat10/catalina.properties`, at the end of the file add:

```properties
spring.profiles.active=prod
```
This activates the production spring profile.

##### Configure context.xml
Edit `/etc/tomcat10/context.xml`, add to `<Context>`:

```xml
<!-- Control/Statistic - Identification used in /version endpoint -->
<Parameter name="HOST_ID" value="[host_id]" override="false"/>
        
<!-- Control: Origin -->
<Parameter name="CONTROL_ALLOWED_ORIGIN" value="https://www.example.com" override="false"/>

<!-- Control: database connection  -->
<Parameter name="CONTROL_DB_USER" value="rmbt_control" override="false"/>
<Parameter name="CONTROL_DB_PASSWORD" value="change-me" override="false"/>
<Parameter name="CONTROL_DB_HOST" value="db" override="false"/>
<Parameter name="CONTROL_DB_PORT" value="5432" override="false"/>
<Parameter name="CONTROL_DB_NAME" value="rmbt" override="false"/>

<!-- Control: server URL -->
<Parameter name="CONTROL_SERVER_URL" value="https://control.example.com/RMBTControlServer" override="false"/>

<!-- Control: logback -->
<Parameter name="LOGGING_CONFIG_FILE" value="/etc/tomcat10/logback-control.xml" override="false"/>
```

Substitute parts with `[]` and URLs with `example.com`. [host_id] is a short string
which identifies the host, e.g. "host1".
Make sure the file `context.xml` is owned by`tomcat`.

##### Configure Logging - Console or Logstash

The default configuration is to send log to `console`. In current Debian installations systemd
redirects console output to systemd's journal.
Older systems logged to `/var/log/tomcat9/catalina.out`.

The following `context.xml` configuration sends log to Logstash at `elk.example.com`:

```xml
<!-- Logging  -->
<Parameter name="LOG_HOST"     value="elk.example.com"       override="false"/>
<Parameter name="LOG_PORT"     value="5000"                  override="false"/>
<Parameter name="LOGGING_HOST" value="dev"                   override="false"/>
```

Alternatively, one might want to define a custom logging configuration.
First, the alternative configuration file need to be specified in `context.xml`:
```xml
 <Parameter name="LOGGING_CONFIG_FILE_CONTROL" value="/etc/tomcat10/logback.xml" override="false"/>
```
Again, make sure that the file `/etc/tomcat10/logback.xml` is owned by `tomcat`.

This example logs to both Logstash and console:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %5p [%t] %-40.40logger{39} : %m%n</pattern>
        </encoder>
    </appender>

    <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>elk.example.com:5000</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"app_name":"control-service","host":"dev"}</customFields>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="logstash"/>
    </root>

</configuration>
```

Terms &amp; Conditions version and platform matching
--------------------

The `POST /RMBTControlServer/settings` endpoint returns the currently valid Terms &amp; Conditions
(T&amp;C) version and URL for the requesting client. Which values are returned depends on the
`platform` field of the request, so that each client platform can be served its own T&amp;C
document and version number.

### Request

The relevant request field is:

* `platform` – the client platform. The legacy misspelling `plattform` (double *t*) is accepted as
  an alias for the same field.

Matching is **case-insensitive**, so `android`, `Android` and `ANDROID` are treated identically.

### Settings keys

The response values are read from the `settings` table (per language, with a language-neutral
fallback). The relevant keys are:

| Platform (`platform`)            | Version key          | URL key             |
|----------------------------------|----------------------|---------------------|
| `android`                        | `tc_version_android` | `tc_url_android`    |
| `ios`                            | `tc_version_ios`     | `tc_url_ios`        |
| `linux`, `darwin`, `windows_nt`  | `tc_version_desktop` | `tc_url_desktop`    |
| *anything else / missing*        | `tc_version`         | `tc_url`            |

### Resolution rules

1. The platform-specific keys are looked up first (e.g. `tc_version_android` / `tc_url_android`
   for Android).
2. **Fallback:** if the platform-specific *version* is absent (key missing or not a valid number),
   the generic `tc_version` is used. Independently, if the platform-specific *URL* is absent, the
   generic `tc_url` is used. This is why a client that sends no `platform`, or an unrecognized one,
   always receives `tc_version` / `tc_url`.

### Response

The values appear under `settings[0].terms_and_conditions`:

```json
{
  "settings": [
    {
      "terms_and_conditions": {
        "version": 10,
        "url": "https://www.example.com/en/tc_android.html"
      }
    }
  ]
}
```

### Example

Request the Android T&amp;C version (`name` must be one of the configured client names):

```bash
curl -s -X POST 'https://control.example.com/RMBTControlServer/settings' \
  -H 'Content-Type: application/json' \
  -d '{"name":"RMBT","language":"en","platform":"Android"}'
```

returns `settings[0].terms_and_conditions.version` from `tc_version_android`.

Settings table keys
--------------------

All runtime configuration lives in the `settings` table (`key`, `value`, `lang`; a `NULL`
`lang` row is the language-neutral default). The keys below are the ones the **control server**
actually reads. Keys may be present in the database that no longer have any effect — see
*Unused / obsolete keys* at the end.

### Terms &amp; Conditions

Served by `POST /settings` and the admin settings endpoints. See
*Terms &amp; Conditions version and platform matching* above for how the platform selects between
them.

| Key                  | Purpose                                            |
|----------------------|----------------------------------------------------|
| `tc_url`             | Default/fallback T&amp;C URL                       |
| `tc_version`         | Default/fallback T&amp;C version                   |
| `tc_url_android`     | Android T&amp;C URL                                |
| `tc_version_android` | Android T&amp;C version                            |
| `tc_url_ios`         | iOS T&amp;C URL                                    |
| `tc_version_ios`     | iOS T&amp;C version                                |
| `tc_url_desktop`     | Desktop (`linux`/`darwin`/`windows_nt`) T&amp;C URL     |
| `tc_version_desktop` | Desktop T&amp;C version                            |

### URLs (`settings[0].urls`)

| Key                        | Purpose                                      |
|----------------------------|----------------------------------------------|
| `url_open_data_prefix`     | Base URL for open-data result links          |
| `url_share`                | Base URL for sharing results                 |
| `url_statistics`           | Statistics URL                               |
| `control_ipv4_only`        | Control-server host forcing IPv4             |
| `control_ipv6_only`        | Control-server host forcing IPv6             |
| `url_ipv4_check`           | IPv4 check URL                               |
| `url_ipv6_check`           | IPv6 check URL                               |
| `url_map_server`           | Map-server URL                               |
| `url_statistic_server`     | Statistic-server URL                         |
| `url_web_statistic_server` | Web statistic-server URL                     |
| `url_web_open_data_server` | Web open-data-server URL                     |
| `url_web_recent_server`    | Web recent-tests-server URL                  |
| `url_web_basemap_tiles`    | Basemap tile URL                             |
| `url_web_osm_tiles`        | OpenStreetMap tile URL                       |

### Map server (`settings[0].map_server`)

| Key               | Purpose                     |
|-------------------|-----------------------------|
| `host_map_server` | Map-server hostname         |
| `ssl_map_server`  | Use SSL (`true`/`false`)    |
| `port_map_server` | Map-server port             |

### Measurement parameters

| Key                     | Purpose                                                           |
|-------------------------|-------------------------------------------------------------------|
| `rmbt_num_threads`      | Default number of test threads (fallback `3`)                     |
| `rmbt_duration_seconds` | Test duration in seconds (fallback `7`)                           |
| `rmbt_min_pings`        | Minimum number of pings (fallback `10`)                           |

### Signal / coverage measurement

| Key                                | Purpose                                                        |
|------------------------------------|----------------------------------------------------------------|
| `max_coverage_measurement_seconds` | Max duration of a single signal/coverage measurement           |
| `max_coverage_session_seconds`     | Max duration of a whole signal/coverage session                |
| `signal_measurement_available`     | Feature flag; only emitted when the value is `true` or `false` |

### Classification

| Key                         | Purpose                                                              |
|-----------------------------|----------------------------------------------------------------------|
| `classification_thresholds` | Quality classification thresholds (JSON), returned by `/settings`    |

### System

| Key           | Purpose                                                   |
|---------------|-----------------------------------------------------------|
| `system_UUID` | Server identity, reported by the `/version` endpoint      |

### Notes on special cases

* **`rmbt_duration`** – exposed through the admin settings endpoints (read/written), but the
  *actual* test duration served to clients comes from `rmbt_duration_seconds`. The plain
  `rmbt_duration` value is not used to configure measurements.
* **`signal_measurement_available`** – only emitted when the value is exactly `true` or `false`;
  any other value (or a missing row) causes the field to be omitted.
* **`classification_thresholds`** – the stored value (JSON) is returned as-is; if the row is
  missing or does not parse as valid JSON, a hard-coded default is returned instead.

### Unused / obsolete keys

The following keys may still exist in the `settings` table but are **not processed by the control
server**:

| Key                         | Status                                                              |
|-----------------------------|---------------------------------------------------------------------|
| `tc_url_android_v4`         | Obsolete – the Android app-version 4.x T&amp;C override was removed |
| `geo_accuracy_limit_map`    | Not read by the control server (used by the map/statistics servers) |
| `geo_accuracy_limit_detail` | Not read by the control server (used by the map/statistics servers) |
| `system_name`               | Not read anywhere in the control server                             |