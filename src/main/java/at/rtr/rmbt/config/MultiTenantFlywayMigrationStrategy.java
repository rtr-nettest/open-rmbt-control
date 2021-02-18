package at.rtr.rmbt.config;

import lombok.AllArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MultiTenantFlywayMigrationStrategy implements FlywayMigrationStrategy {

    private DataSourceConfig dataSourceConfig;
    private ClientTenantConfig clientTenantConfig;

    @Override
    public void migrate(Flyway flyway) {
        clientTenantConfig.getClientTenantMapping().values().iterator()
                .forEachRemaining(dbName ->
                        Flyway.configure()
                                .validateOnMigrate(false)
                                .table("_SCHEMA_VERSION")
                                .baselineOnMigrate(true)
                                .outOfOrder(false)
                                .dataSource(String.format(dataSourceConfig.getUrl(), dbName),
                                        dataSourceConfig.getUsername(),
                                        dataSourceConfig.getPassword())
                                .load()
                                .migrate());
    }
}
