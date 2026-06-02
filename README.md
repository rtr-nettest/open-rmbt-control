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