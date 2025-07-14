package at.rtr.rmbt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
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

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        final PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        pageableResolver.setOneIndexedParameters(true);
        argumentResolvers.add(pageableResolver);
    }

    @Value("#{'${origin}'.split(',')}")
    private String[] origin;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
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
                            SIGNAL_REQUEST, SIGNAL_RESULT, COVERAGE_REQUEST, COVERAGE_RESULT,
                            NEWS_URL, REGISTRATION_URL, RESULT_QOS_URL, RESULT_URL, SETTINGS_URL,
                            PROVIDERS, TEST_RESULT, HISTORY, SYNC, MEASUREMENT_QOS_RESULT, VERSION, RESULT_UPDATE,
                            QOS_BY_OPEN_TEST_UUID, QOS_BY_OPEN_TEST_UUID_AND_LANGUAGE, ADMIN_SET_IMPLAUSIBLE,
                            "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll();
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
        return (web) -> web.ignoring().requestMatchers("/v3/api-docs", "/swagger-ui/**", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/health").and()
                .ignoring().requestMatchers(HttpMethod.OPTIONS, "/**");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        Arrays.asList(SETTINGS_URL, HISTORY, ADMIN_SET_IMPLAUSIBLE, NEWS_URL, MEASUREMENT_QOS_REQUEST, MEASUREMENT_QOS_REQUEST,
                        REGISTRATION_URL, RESULT_URL, RESULT_UPDATE, SYNC, TEST_RESULT_DETAIL, TEST_RESULT)
                .forEach(url -> {
                    registry.addMapping(url)
                            .allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.OPTIONS.name())
                            .allowedOrigins(origin);

                });

        Arrays.asList(IP, QOS_RESULT, QOS_RESULT + "/**", VERSION, REQUEST_DATA_COLLECTOR)
                .forEach(url -> registry.addMapping(url));
    }

}
