package at.rtr.rmbt.repository;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class DatabaseContainer {
    private static PostgreSQLContainer instance;

    private DatabaseContainer() {
    }

    public static synchronized PostgreSQLContainer getInstance() {
        if (instance == null) {
            instance = new PostgreSQLContainer(DockerImageName.parse("postgis/postgis:13-3.1-alpine").asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("rmbt")
                .withUsername("rmbt")
                .withPassword("rmbt");
            instance.start();
        }
        return instance;
    }
}
