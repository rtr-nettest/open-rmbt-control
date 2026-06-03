package at.rtr.rmbt.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * HTTP Basic gate for the JavaMelody report ({@code /monitoring}).
 *
 * <p>The report path is permitted in Spring Security so it can be reached at all, and is protected
 * here instead by a password taken from the deployment context: the {@code MELODY_PW} value of a
 * Tomcat {@code conf/context.xml} {@code <Parameter>} (falling back to a JVM system property /
 * environment variable, like {@link LoggingConfigurer} does). Only the password is checked - the
 * Basic-auth username may be anything.
 *
 * <p>If {@code MELODY_PW} is not configured, access is denied outright with HTTP 403 (no auth
 * prompt): monitoring stays effectively disabled until a password is set.
 */
@Slf4j
public class MonitoringAuthFilter implements Filter {

    static final String MELODY_PW = "MELODY_PW";
    private static final String REALM = "JavaMelody monitoring";
    private static final String BASIC_PREFIX = "Basic ";

    private final Environment environment;

    public MonitoringAuthFilter(final Environment environment) {
        this.environment = environment;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final String password = resolvePassword(request.getServletContext());
        if (password == null) {
            // No password configured -> monitoring is disabled. Do not prompt, just refuse.
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Monitoring is disabled (MELODY_PW not configured)");
            return;
        }

        if (!isAuthorized(request, password)) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"" + REALM + "\", charset=\"UTF-8\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isAuthorized(final HttpServletRequest request, final String expectedPassword) {
        final String header = request.getHeader("Authorization");
        if (header == null || !header.regionMatches(true, 0, BASIC_PREFIX, 0, BASIC_PREFIX.length())) {
            return false;
        }
        final String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(header.substring(BASIC_PREFIX.length()).trim()),
                    StandardCharsets.UTF_8);
        } catch (IllegalArgumentException invalidBase64) {
            return false;
        }
        // Credentials are "username:password"; the username is intentionally not checked.
        final int colon = decoded.indexOf(':');
        final String suppliedPassword = (colon >= 0) ? decoded.substring(colon + 1) : "";
        return constantTimeEquals(suppliedPassword, expectedPassword);
    }

    /**
     * Reads {@code MELODY_PW} from the Tomcat {@code conf/context.xml} {@code <Parameter>}
     * (servlet context init-param) first, then from the Spring Environment (JVM system property /
     * environment variable). Returns {@code null} when unset or blank.
     */
    private String resolvePassword(final ServletContext servletContext) {
        String value = (servletContext != null) ? servletContext.getInitParameter(MELODY_PW) : null;
        if (value == null || value.isBlank()) {
            value = environment.getProperty(MELODY_PW);
        }
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private static boolean constantTimeEquals(final String a, final String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
