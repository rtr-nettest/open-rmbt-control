RMBT Control Server
=======

Building a war archive
-----

> mvn compile war:war


Setting database connection
-----

In case the DB connection is not set in the `application.yml`, placeholders can be used in the `context.xml`

```xml
<Parameter name="CONTROL_DB_USER" value="rmbt_control" override="false"/>
<Parameter name="CONTROL_DB_PASSWORD" value="password" override="false"/>
<Parameter name="CONTROL_DB_HOST" value="127.0.0.1" override="false"/>
<Parameter name="CONTROL_DB_PORT" value="5432" override="false"/>
<Parameter name="CONTROL_DB_NAME" value="rmbt" override="false"/>
<Parameter name="CONTROL_HOST" value="localhost" override="false"/>
<Parameter name="CONTROL_SERVER_URL" value="https://server.netztest.at" override="false"/>
<Parameter name="CONTROL_SERVER_CONTEXT" value="/RMBTControlServer" override="false"/>
```

and referenced in the `application.yml` like

```yaml
url: jdbc:postgresql://${CONTROL_DB_HOST}:${CONTROL_DB_PORT}/${CONTROL_DB_NAME}
```

In `catalina.properties` the desired spring profile can be set:

```properties
spring.profiles.active=prod
```


Github action
----

The `WAR build` action produces a WAR file that can be used on a server. This only applies to the `feature/war` branch.


Logstash configuration
---

In tomcats `context.xml`, set the path to a config like

```xml
<Parameter name="LOGGING_CONFIG_FILE" value="/var/lib/tomcat9/conf/logback.xml" override="false"/>
```

and set the configured xml accordingly, e.g.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration>

<configuration scan="true">
    <include resource="org/springframework/boot/logging/logback/base.xml"/>

    <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <param name="Encoding" value="UTF-8"/>
        <remoteHost>your-host.example.com</remoteHost>
        <port>5000</port>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"app_name":"control-service"}</customFields>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="logstash"/>
    </root>
</configuration>


```