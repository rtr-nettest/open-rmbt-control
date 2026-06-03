package at.rtr.rmbt.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MonitoringAuthFilter}: no {@code MELODY_PW} configured denies outright,
 * a configured password requires a matching HTTP Basic credential, and the context.xml value takes
 * precedence over the environment.
 */
@ExtendWith(MockitoExtension.class)
class MonitoringAuthFilterTest {

    @Mock
    private Environment environment;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private ServletContext servletContext;

    private MonitoringAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new MonitoringAuthFilter(environment);
        when(request.getServletContext()).thenReturn(servletContext);
    }

    private static String basic(final String user, final String password) {
        final String token = Base64.getEncoder()
                .encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }

    @Test
    void noPasswordConfigured_isForbiddenAndNotChained() throws Exception {
        when(servletContext.getInitParameter(MonitoringAuthFilter.MELODY_PW)).thenReturn(null);
        when(environment.getProperty(MonitoringAuthFilter.MELODY_PW)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void blankPasswordConfigured_isForbidden() throws Exception {
        when(servletContext.getInitParameter(MonitoringAuthFilter.MELODY_PW)).thenReturn("   ");
        when(environment.getProperty(MonitoringAuthFilter.MELODY_PW)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void passwordConfiguredButNoAuthHeader_isUnauthorizedWithChallenge() throws Exception {
        when(servletContext.getInitParameter(MonitoringAuthFilter.MELODY_PW)).thenReturn("secret");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).setHeader(eq("WWW-Authenticate"), anyString());
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void wrongPassword_isUnauthorized() throws Exception {
        when(servletContext.getInitParameter(MonitoringAuthFilter.MELODY_PW)).thenReturn("secret");
        when(request.getHeader("Authorization")).thenReturn(basic("admin", "wrong"));

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void correctPassword_anyUsername_passesThrough() throws Exception {
        when(servletContext.getInitParameter(MonitoringAuthFilter.MELODY_PW)).thenReturn("secret");
        when(request.getHeader("Authorization")).thenReturn(basic("whoever", "secret"));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void contextParameterTakesPrecedenceOverEnvironment() throws Exception {
        when(servletContext.getInitParameter(MonitoringAuthFilter.MELODY_PW)).thenReturn("ctx-pw");
        when(request.getHeader("Authorization")).thenReturn(basic("admin", "ctx-pw"));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
