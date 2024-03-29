package at.rtr.rmbt.repository;

import org.flywaydb.core.Flyway;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@Configuration
public class TestDatabaseConfig {

    @Bean
    public DataSource dataSource() {
        PostgreSQLContainer instance = DatabaseContainer.getInstance();
        return DataSourceBuilder.create()
            .driverClassName(instance.getDriverClassName())
            .url(instance.getJdbcUrl())
            .username(instance.getUsername())
            .password(instance.getPassword())
            .build();
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure().dataSource(dataSource).load();
    }
}
