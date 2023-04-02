Open-RMBT-Control
=========

> *Open-RMBT* is an open source, multi-threaded bandwidth measurement system.

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
* [RTR-NetTest/open-rmbt](https://github.com/rtr-nettest/open-rmbt) - Orignal repository, today mainly contains Map and QoS-server
* [RTR-NetTest/rmbt-server](https://github.com/rtr-nettest/rmbt-server) - Test Server for conducting measurements based on the RMBT protocol
* [RTR-NetTest/rmbtws](https://github.com/rtr-nettest/rmbtws) - JavaScript client for conducting RMBT-based speed measurements
* [RTR-NetTest/open-rmbt-statistics](https://github.com/rtr-nettest//open-rmbt-statistics) - Statistics server
* [RTR-NetTest/open-rmbt-ios](https://github.com/rtr-nettest/open-rmbt-ios) - iOS app
* [RTR-NetTest/open-rmbt-android](https://github.com/rtr-nettest/open-rmbt-android) - Android app
* [RTR-NetTest/rtr-nettest/open-rmbt-website](https://github.com/rtr-nettest/open-rmbt-website) - Web site

System requirements for the Control Server
-------------------

* single (virtual) server with sufficient RAM and CPU performance
* Fast disk (NVME) for data base
* Base system Debian 11 or newer (or similar)
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

* Apache Tomcat 9 or higher
* nginx; configure nginx to forward requests to localhost:8080
* letsencrypt; create certificate
* openjdk-11-jre (do not use a higher version)
* [Maxmind GeoLite2 database](https://dev.maxmind.com/geoip/geolite2-free-geolocation-data)

### Build the RMBTControlServer.war archive

> mvn compile war:war

### Alternative: Get WAR using Github action

The `WAR build` action produces a WAR file that can be used on a server. This only applies to the `feature/war` branch.

### Configure Tomcat

##### Configure catalina.properties
Edit `/etc/tomcat9/catalina.properties`, add the end add:

```properties
spring.profiles.active=prod
```
This activates the production spring profile.

##### Configure context.xml
Edit `/etc/tomcat9/context.xml`, add to `<Context>`:

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
<Parameter name="LOGGING_CONFIG_FILE" value="/etc/tomcat9/logback-control.xml" override="false"/>
```

Substitute parts with `[]` and URLs with `example.com`. [host_id] is a short string
which identifies the host, e.g. "host1".
Make sure the file `context.xml` is owned by`tomcat`.

##### Configure Logstash

Logstash is configured in `etc/tomcat9/logback-control.xml`.
The basic logging configuration is to send log to `console`. In newer Debian installations systemd is
configured to redirect that output to systemd log. Older systems send log to `/var/log/tomcat9/catalina.out`.

The following configuration sends log to `console`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration>
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
<!-- log levels: TRACE, DEBUG, INFO, WARN, ERROR -->
  <root level="INFO">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```
Alternatively, log can be sent to Logstash on a remote ELK instance
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration>
<configuration scan="true">
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <param name="Encoding" value="UTF-8"/>
<!-- define remote logging  host here -->
        <remoteHost>elk.example.com</remoteHost>
        <port>5000</port>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
<!-- add custom fields to identify server and host -->
            <customFields>{"app_name":"control-service", "host":"[host_id]"}</customFields>
        </encoder>
    </appender>
<!-- log levels: TRACE, DEBUG, INFO, WARN, ERROR -->
    <root level="INFO">
        <appender-ref ref="logstash"/>
    </root>
</configuration>
```
Again, make sure the file `etc/tomcat9/logback-control.xml` is owned by`tomcat`.


