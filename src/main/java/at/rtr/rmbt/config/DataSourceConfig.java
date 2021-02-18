package at.rtr.rmbt.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
@Setter
@ConfigurationProperties("spring.datasource")
@Accessors(chain = true)
public class DataSourceConfig {
    private int poolSize;
    private int minIdle;
    private List<String> db;
    private String username;
    private String driverClassName;
    private String password;
    private String url;
}
