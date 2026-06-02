package at.rtr.rmbt.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import jakarta.servlet.ServletContext;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;

/**
 * Applies logging configuration at runtime from the deployment context, so the value can come
 * from a Tomcat {@code conf/context.xml} {@code <Parameter>} (and equally from a JVM system
 * property or an environment variable). The bundled {@code logback.xml} only sets up the
 * console; doing the rest here avoids the chicken-and-egg problem that those context values are
 * not yet available when logback initialises during early Spring Boot startup.
 *
 * <p>Order of precedence:
 * <ol>
 *   <li>{@code LOGGING_CONFIG_FILE_CONTROL} points at an existing file &rarr; load that logback config
 *       verbatim (full control for advanced setups);</li>
 *   <li>{@code LOG_HOST} is set &rarr; ship to Logstash at {@code LOG_HOST:LOG_PORT} (INFO) and
 *       limit the console to ERROR;</li>
 *   <li>otherwise &rarr; leave the console-only baseline in place.</li>
 * </ol>
 *
 * <p>Any failure here is swallowed - logging configuration must never stop the app from starting.
 */
@Component
public class LoggingConfigurer implements ApplicationListener<ApplicationReadyEvent> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LoggingConfigurer.class);

    private static final String APP_NAME = "control-service";
    private static final String CONSOLE_APPENDER = "CONSOLE";
    private static final String LOGSTASH_APPENDER = "logstash";
    private static final String DEFAULT_PORT = "5000";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            final ApplicationContext appContext = event.getApplicationContext();
            final ServletContext servletContext = (appContext instanceof WebApplicationContext wac)
                    ? wac.getServletContext() : null;
            final Environment env = appContext.getEnvironment();

            final String configFile = resolve(servletContext, env, "LOGGING_CONFIG_FILE_CONTROL");
            final String logHost = resolve(servletContext, env, "LOG_HOST");
            LOG.info("Logging: resolved LOGGING_CONFIG_FILE_CONTROL={}, LOG_HOST={}", configFile, logHost);

            // A context-supplied logback file always wins (it is the most specific, per-WAR config).
            if (configFile != null) {
                if (new File(configFile).isFile()) {
                    applyExternalConfig(configFile);
                    return;
                }
                LOG.warn("Logging: LOGGING_CONFIG_FILE_CONTROL [{}] is not a readable file - ignoring it", configFile);
            }

            if (logHost == null) {
                LOG.info("Logging: no LOGGING_CONFIG_FILE_CONTROL and no LOG_HOST - console only");
                return;
            }
            final String logPort = firstNonBlank(resolve(servletContext, env, "LOG_PORT"), DEFAULT_PORT);
            final String loggingHost = firstNonBlank(resolve(servletContext, env, "LOGGING_HOST"), "");
            enableLogstash(logHost, logPort, loggingHost);
        } catch (Exception e) {
            LOG.warn("Logging: failed to reconfigure from context, keeping console config: {}", e.getMessage());
        }
    }

    /**
     * Reads a setting from the Tomcat {@code conf/context.xml} {@code <Parameter>} entries
     * (servlet context init-params) first, then falls back to the Spring Environment (JVM system
     * property or environment variable). Returns {@code null} when unset or blank.
     */
    private static String resolve(ServletContext servletContext, Environment env, String name) {
        String value = (servletContext != null) ? servletContext.getInitParameter(name) : null;
        if (trimToNull(value) == null) {
            value = env.getProperty(name);
        }
        return trimToNull(value);
    }

    private void enableLogstash(String host, String port, String hostField) {
        final LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
        final Logger root = ctx.getLogger(Logger.ROOT_LOGGER_NAME);

        if (root.getAppender(LOGSTASH_APPENDER) != null) {
            return; // already configured (e.g. a duplicate event)
        }

        final LogstashEncoder encoder = new LogstashEncoder();
        encoder.setContext(ctx);
        encoder.setCustomFields("{\"app_name\":\"" + APP_NAME + "\",\"host\":\"" + hostField + "\"}");
        encoder.start();

        final LogstashTcpSocketAppender appender = new LogstashTcpSocketAppender();
        appender.setName(LOGSTASH_APPENDER);
        appender.setContext(ctx);
        appender.addDestination(host + ":" + port);
        appender.setEncoder(encoder);
        appender.start();
        root.addAppender(appender);

        // Console: ERROR and above only, once we have a remote sink for everything else.
        final Appender<ILoggingEvent> console = root.getAppender(CONSOLE_APPENDER);
        if (console != null) {
            final ThresholdFilter threshold = new ThresholdFilter();
            threshold.setContext(ctx);
            threshold.setLevel("ERROR");
            threshold.start();
            console.addFilter(threshold);
        }
        LOG.info("Logging: shipping to Logstash {}:{} (console limited to ERROR)", host, port);
    }

    private void applyExternalConfig(String path) throws Exception {
        final LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(ctx);
        ctx.reset();
        configurator.doConfigure(new File(path));
        LOG.info("Logging: applied external configuration from {}", path);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        final String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String firstNonBlank(String value, String fallback) {
        final String trimmed = trimToNull(value);
        return trimmed == null ? fallback : trimmed;
    }
}
