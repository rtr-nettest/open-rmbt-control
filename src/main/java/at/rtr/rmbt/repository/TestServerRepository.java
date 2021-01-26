package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.enums.ServerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestServerRepository extends JpaRepository<TestServer, Long> {
    Optional<TestServer> findByUuidAndActive(UUID uuid, Boolean active);

    @Query(
        value = "SELECT * FROM test_server ts " +
            "WHERE ts.active " +
            "AND ts.server_type IN :serverTypes " +
            "AND ( :country = ANY(ts.countries) OR 'any' = ANY(ts.countries)) " +
            "ORDER BY 'any' != ANY (ts.countries) DESC, " +
            "ts.priority, " +
            "RANDOM() * ts.weight DESC " +
            "LIMIT 1",
        nativeQuery = true
    )
    TestServer findActiveByServerTypeInAndCountries(
        @Param("serverTypes") List<ServerType> serverTypes,
        @Param("country") String country
    );

    List<TestServer> getByActiveTrueAndSelectableTrueAndServerTypeIn(Collection<String> serverTypes);
}
