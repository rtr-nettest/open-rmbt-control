package at.rtr.rmbt.config;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static at.rtr.rmbt.constant.URIConstants.*;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    }

    @Configuration
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
                    .antMatchers(SIGNAL_REQUEST, SIGNAL_RESULT, NEWS_URL, REGISTRATION_URL,
                            RESULT_QOS_URL, RESULT_URL, SETTINGS_URL).permitAll()
                    .antMatchers(ADMIN_SIGNAL).hasAuthority("read:reports/signal")
                    .anyRequest().authenticated();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            // ignoring security for swagger APIs
            web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/health").and()
                    .ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        }
    }
}
