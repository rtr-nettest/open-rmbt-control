package com.rtr.nettest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.specture.core"})
@EnableElasticsearchRepositories(basePackages = {"com.specture.core.repository"})
@EnableJpaRepositories(basePackages = {"com.specture.core.repository"})
@EntityScan(basePackages = {"com.specture.core.model"})
public class RTRApplication {

    public static void main(String[] args) {
        SpringApplication.run(RTRApplication.class, args);
    }

}
