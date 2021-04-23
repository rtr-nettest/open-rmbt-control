package at.rtr.rmbt.config;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static at.rtr.rmbt.constant.URIConstants.ADMIN_NEWS;
import static at.rtr.rmbt.constant.URIConstants.ADMIN_SETTING;
import static at.rtr.rmbt.constant.URIConstants.ADMIN_SET_IMPLAUSIBLE;
import static at.rtr.rmbt.constant.URIConstants.ADMIN_SIGNAL;
import static at.rtr.rmbt.constant.URIConstants.HISTORY;
import static at.rtr.rmbt.constant.URIConstants.IP;
import static at.rtr.rmbt.constant.URIConstants.MEASUREMENT_QOS_REQUEST;
import static at.rtr.rmbt.constant.URIConstants.MEASUREMENT_QOS_RESULT;
import static at.rtr.rmbt.constant.URIConstants.NEWS_URL;
import static at.rtr.rmbt.constant.URIConstants.PROVIDERS;
import static at.rtr.rmbt.constant.URIConstants.QOS_BY_OPEN_TEST_UUID;
import static at.rtr.rmbt.constant.URIConstants.QOS_BY_OPEN_TEST_UUID_AND_LANGUAGE;
import static at.rtr.rmbt.constant.URIConstants.REGISTRATION_URL;
import static at.rtr.rmbt.constant.URIConstants.REQUEST_DATA_COLLECTOR;
import static at.rtr.rmbt.constant.URIConstants.RESULT_QOS_URL;
import static at.rtr.rmbt.constant.URIConstants.RESULT_UPDATE;
import static at.rtr.rmbt.constant.URIConstants.RESULT_URL;
import static at.rtr.rmbt.constant.URIConstants.SETTINGS_URL;
import static at.rtr.rmbt.constant.URIConstants.SIGNAL_REQUEST;
import static at.rtr.rmbt.constant.URIConstants.SIGNAL_RESULT;
import static at.rtr.rmbt.constant.URIConstants.SYNC;
import static at.rtr.rmbt.constant.URIConstants.TEST_RESULT;
import static at.rtr.rmbt.constant.URIConstants.TEST_RESULT_DETAIL;
import static at.rtr.rmbt.constant.URIConstants.TEST_SERVER;
import static at.rtr.rmbt.constant.URIConstants.VERSION;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        final PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        pageableResolver.setOneIndexedParameters(true);
        argumentResolvers.add(pageableResolver);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Configuration
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        private static final String[] clients = new String[]{"client:specure", "client:rtr"};

        @Value("${auth0.apiAudience}")
        private String audience;
        @Value("${auth0.issuer}")
        private String issuer;

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.cors();
            JwtWebSecurityConfigurer.forRS256(audience, issuer)
                    .configure(httpSecurity)
                    .authorizeRequests()
                    .antMatchers(IP, REQUEST_DATA_COLLECTOR, TEST_RESULT_DETAIL, MEASUREMENT_QOS_REQUEST, SIGNAL_REQUEST,
                            SIGNAL_RESULT, NEWS_URL, REGISTRATION_URL, RESULT_QOS_URL, RESULT_URL, SETTINGS_URL,
                            PROVIDERS, TEST_RESULT, HISTORY, SYNC, MEASUREMENT_QOS_RESULT, VERSION, RESULT_UPDATE,
                            QOS_BY_OPEN_TEST_UUID, QOS_BY_OPEN_TEST_UUID_AND_LANGUAGE, ADMIN_SET_IMPLAUSIBLE).permitAll()
                    .antMatchers(ADMIN_SIGNAL).hasAuthority("read:reports/signal")
                    .antMatchers(ADMIN_SIGNAL).hasAnyAuthority(clients)
                    .antMatchers(ADMIN_NEWS).hasAuthority("read:config/news")
                    .antMatchers(ADMIN_NEWS).hasAnyAuthority(clients)
                    .antMatchers(HttpMethod.GET, TEST_SERVER).hasAuthority("read:servers")
                    .antMatchers(HttpMethod.POST, TEST_SERVER).hasAuthority("write:servers")
                    .antMatchers(TEST_SERVER).hasAnyAuthority(clients)
                    .antMatchers(ADMIN_SETTING).hasAuthority("write:settings")
                    .antMatchers(ADMIN_SETTING).hasAnyAuthority(clients)
                    .anyRequest().authenticated();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            // ignoring security for swagger APIs
            web.ignoring().antMatchers("/v3/api-docs", "/swagger-ui/**", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/health").and()
                    .ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        }
    }
}
