package at.rtr.rmbt.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Stops library-owned background threads when the web application shuts down.
 *
 * <p>{@code dnsjava} and GeoTools each start long-lived helper threads (a DNS NIO selector, the
 * GeoTools weak-reference cleaner, and the authority-factory disposer timer) that they do not stop
 * on servlet-context destroy. On a normal JVM stop this is harmless, but Tomcat's classloader-leak
 * detector logs "…appears to have started a thread … but has failed to stop it …" for each of them,
 * and on a <em>hot redeploy</em> (new WAR without a JVM restart) those threads keep the old webapp
 * classloader alive and eventually exhaust Metaspace.
 *
 * <p>Spring runs this {@code @PreDestroy} while closing the application context, which happens
 * <em>before</em> Tomcat's leak check, so stopping the threads here removes both the warnings and
 * the redeploy leak. Every step is best-effort and reflective: a missing class/method on a future
 * dependency version simply no-ops instead of breaking shutdown.
 */
@Slf4j
@Component
public class ShutdownThreadCleaner {

    /**
     * Stops the known library background threads on context shutdown.
     */
    @PreDestroy
    public void stopLibraryThreads() {
        // dnsjava NIO selector thread.
        invokeStatic("org.xbill.DNS.NioClient", "close");
        // GeoTools weak-reference cleaner thread.
        invokeOnStaticField("org.geotools.util.WeakCollectionCleaner", "DEFAULT", "exit");
        // GeoTools "GT authority factory disposer" timer thread.
        invokeStatic("org.geotools.referencing.factory.DeferredAuthorityFactory", "exit");
    }

    /** Invokes a static no-arg method {@code className.method()} if present. */
    private static void invokeStatic(final String className, final String method) {
        try {
            Class.forName(className).getMethod(method).invoke(null);
            log.debug("Shutdown cleanup: {}.{}() invoked", className, method);
        } catch (Throwable t) {
            log.debug("Shutdown cleanup skipped for {}.{}(): {}", className, method, t.toString());
        }
    }

    /** Invokes a no-arg method on the value of a public static field: {@code className.field.method()}. */
    private static void invokeOnStaticField(final String className, final String field, final String method) {
        try {
            final Class<?> clazz = Class.forName(className);
            final Object target = clazz.getField(field).get(null);
            target.getClass().getMethod(method).invoke(target);
            log.debug("Shutdown cleanup: {}.{}.{}() invoked", className, field, method);
        } catch (Throwable t) {
            log.debug("Shutdown cleanup skipped for {}.{}.{}(): {}", className, field, method, t.toString());
        }
    }
}
