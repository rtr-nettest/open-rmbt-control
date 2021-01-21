package com.rtr.nettest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.rtr.nettest"})
@EnableJpaRepositories(basePackages = {"com.rtr.nettest.repository"})
@EntityScan(basePackages = {"com.rtr.nettest.model"})
@PropertySource({"classpath:git.properties"})
public class RTRApplication {

    public static void main(String[] args) {
        SpringApplication.run(RTRApplication.class, args);
    }
}
