package at.rtr.rmbt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

import static at.rtr.rmbt.constant.URIConstants.*;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * The Spring Boot auto-configured Jackson 2 ObjectMapper (com.fasterxml.*). It carries the
     * application's Jackson 2 Modules (JtsModule, lenient-UUID — see {@link JacksonConfig}) and the
     * {@code spring.jackson2.*} settings (non_null inclusion, epoch timestamps).
     */
    private final ObjectMapper jackson2ObjectMapper;

    public WebMvcConfiguration(ObjectMapper jackson2ObjectMapper) {
        this.jackson2ObjectMapper = jackson2ObjectMapper;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        final PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        pageableResolver.setOneIndexedParameters(true);
        argumentResolvers.add(pageableResolver);
    }

    /**
     * Forces the REST API to use Jackson 2 for JSON. {@code @EnableWebMvc} disables Spring Boot's
     * message-converter auto-configuration, so {@code spring.http.converters.preferred-json-mapper}
     * has no effect and Spring MVC would otherwise install the Framework 7 default Jackson 3 converter.
     * Jackson 3 ignores this app's Jackson 2 Modules, changes date serialization (epoch -> ISO) and is
     * stricter on deserialization (it rejects a JSON null bound to a primitive, e.g.
     * {@code {"user_server_selection": null}}). Prepending a Jackson 2 converter makes it win for JSON
     * (converters are tried in order), so behaviour matches the pre-Boot-4 API.
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new MappingJackson2HttpMessageConverter(jackson2ObjectMapper));
    }

    @Value("#{'${origin}'.split(',')}")
    private String[] origin;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Registers the {@link MonitoringAuthFilter} in front of the JavaMelody report. Ordered ahead of
     * everything else so it gates {@code /monitoring} before JavaMelody's own filter serves it.
     */
    @Bean
    public FilterRegistrationBean<MonitoringAuthFilter> monitoringAuthFilter(Environment environment) {
        final FilterRegistrationBean<MonitoringAuthFilter> registration =
                new FilterRegistrationBean<>(new MonitoringAuthFilter(environment));
        registration.setName("monitoringAuthFilter");
        registration.addUrlPatterns("/monitoring", "/monitoring/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }


    private static final String[] clients = new String[]{"client:specure", "client:rtr"};

    @Value("${auth0.apiAudience}")
    private String audience;
    @Value("${auth0.issuer}")
    private String issuer;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(cors -> cors.disable());
        httpSecurity.csrf(csrf -> csrf.disable());
        httpSecurity.authorizeHttpRequests((authorize -> {
            authorize.requestMatchers(IP, REQUEST_DATA_COLLECTOR, TEST_RESULT_DETAIL, MEASUREMENT_QOS_REQUEST,
                            COVERAGE_REQUEST, COVERAGE_RESULT,
                            NEWS_URL, REGISTRATION_URL, RESULT_QOS_URL, RESULT_URL, SETTINGS_URL,
                            PROVIDERS, TEST_RESULT, HISTORY, SYNC, MEASUREMENT_QOS_RESULT, VERSION, RESULT_UPDATE,
                            QOS_BY_OPEN_TEST_UUID, QOS_BY_OPEN_TEST_UUID_AND_LANGUAGE, TEST_SERVER_STATUS,
                            // Swagger UI + OpenAPI spec. Both the springdoc default (/v3/api-docs)
                            // and the deployment's overridden path (/api-docs, incl. .yaml/.json) are
                            // whitelisted, so the spec is not blocked by anyRequest().authenticated().
                            "/swagger-ui/**", "/swagger-ui.html",
                            "/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml",
                            "/api-docs", "/api-docs/**", "/api-docs.yaml", "/api-docs.json",
                            // JavaMelody report: reachable, but guarded by MonitoringAuthFilter (MELODY_PW)
                            "/monitoring", "/monitoring/**")
                    .permitAll();
            // Admin moderation action: must NOT be public. Gated like the other admin routes.
            authorize.requestMatchers(ADMIN_SET_IMPLAUSIBLE).hasAuthority("write:implausible");
            authorize.requestMatchers(ADMIN_SET_IMPLAUSIBLE).hasAnyAuthority(clients);
            authorize.requestMatchers(ADMIN_SIGNAL).hasAuthority("read:reports/signal");
            authorize.requestMatchers(ADMIN_SIGNAL).hasAnyAuthority(clients);
            authorize.requestMatchers(ADMIN_NEWS).hasAuthority("read:config/news");
            authorize.requestMatchers(ADMIN_NEWS).hasAnyAuthority(clients);
            authorize.requestMatchers(HttpMethod.GET, TEST_SERVER).hasAuthority("read:servers");
            authorize.requestMatchers(HttpMethod.POST, TEST_SERVER).hasAuthority("write:servers");
            authorize.requestMatchers(TEST_SERVER).hasAnyAuthority(clients);
            authorize.requestMatchers(ADMIN_SETTING).hasAuthority("write:settings");
            authorize.requestMatchers(ADMIN_SETTING).hasAnyAuthority(clients);
            authorize.anyRequest().authenticated();
        }));

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // OpenAPI spec + Swagger UI. Both the springdoc default (/v3/api-docs) and the
                // path this app serves the spec at (/api-docs, incl. .yaml/.json and the
                // swagger-config sub-path) bypass the security chain, so the spec is never 403'd.
                .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml",
                        "/api-docs", "/api-docs/**", "/api-docs.yaml", "/api-docs.json",
                        "/swagger-ui/**", "/v2/api-docs", "/configuration/ui",
                        "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/health")
                .requestMatchers(HttpMethod.OPTIONS, "/**");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        Arrays.stream(origin).forEach((s) -> {
            log.info("adding CORS mapping for {}", s);
        });


        Arrays.asList(SETTINGS_URL, HISTORY, ADMIN_SET_IMPLAUSIBLE, NEWS_URL, MEASUREMENT_QOS_REQUEST, MEASUREMENT_QOS_REQUEST,
                        REGISTRATION_URL, RESULT_URL, RESULT_UPDATE, SYNC, TEST_RESULT_DETAIL, TEST_RESULT)
                .forEach(url -> {
                    registry.addMapping(url)
                            .allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.OPTIONS.name())
                            .allowedOrigins(origin);

                });

        Arrays.asList(IP, QOS_RESULT, QOS_RESULT + "/**", VERSION, REQUEST_DATA_COLLECTOR, TEST_SERVER_STATUS)
                .forEach(url -> registry.addMapping(url));
    }

}
