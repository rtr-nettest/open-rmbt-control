package com.rtr.nettest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.specure.core", "com.rtr.nettest"})
@EnableJpaRepositories(basePackages = {"com.specure.core.repository", "com.rtr.nettest.repository"})
@EntityScan(basePackages = {"com.specure.core.model", "com.rtr.nettest.entity"})
public class RTRApplication {

    public static void main(String[] args) {
        SpringApplication.run(RTRApplication.class, args);
    }
}
