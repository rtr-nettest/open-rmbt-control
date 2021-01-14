package com.rtr.nettest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Configuration
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {

        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            // ignoring security for swagger APIs
            web.ignoring().antMatchers("/**", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/health").and()
                    .ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        }
    }
}
