package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.enums.ServerType;
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
            "AND ts.uid in (select tst.test_server_uid from test_server_types tst where tst.server_type in (:serverTypes)) " +
            "AND ( CAST(:country AS TEXT) = ANY(ts.countries) OR 'any' = ANY(ts.countries)) " + // need to cast string
            "ORDER BY 'any' != ANY (ts.countries) DESC, " + // because null value is converted to varbinary by hibernate
            "ts.priority, " +                               //  which causes an error
            "RANDOM() * ts.weight DESC " +
            "LIMIT 1",
        nativeQuery = true
    )
    TestServer findActiveByServerTypeInAndCountries(
        @Param("serverTypes") List<String> serverTypes,
        @Param("country") String country
    );

    List<TestServer> findDistinctByActiveTrueAndSelectableTrueAndServerTypesIn(Collection<ServerType> serverTypes);
}
