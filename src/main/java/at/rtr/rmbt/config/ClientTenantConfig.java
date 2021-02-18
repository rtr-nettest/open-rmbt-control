package at.rtr.rmbt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties("client-tenant")
public class ClientTenantConfig {
    private Map<String, String> clientTenantMapping;
    private String defaultTenant;
}


