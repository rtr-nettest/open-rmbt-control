package at.rtr.rmbt.repository;

import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.model.TestServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.io.*;

public interface TestServerRepository extends JpaRepository<TestServer, Long> {
    Optional<TestServer> findByUuidAndActive(UUID uuid, Boolean active);

    // use server with coverage = true when coverage in request is true
    @Query(
        value = "SELECT * FROM test_server ts " +
            "WHERE ts.active " +
            "AND (((ts.coverage = TRUE) AND (CAST(CAST(:coverage AS CHARACTER VARYING) AS BOOLEAN) = TRUE))" +
            "OR ts.server_type in (:serverTypes) " +
            "AND ( CAST(:country AS TEXT) = ANY(ts.countries) OR 'any' = ANY(ts.countries))) " + // need to cast string
            "ORDER BY 'any' != ANY (ts.countries) DESC, " + // because null value is converted to varbinary by hibernate
            "ts.priority, " +                               //  which causes an error
            "RANDOM() * ts.weight DESC " +
            "LIMIT 1",
        nativeQuery = true
    )
    TestServer findActiveByServerTypeInAndCountries(
        @Param("serverTypes") List<String> serverTypes,
        @Param("country") String country,
        @Param("coverage") Boolean coverage
    );

    @Query("select distinct t from TestServer t, ServerTypeDetails std " +
            "where t.uid = std.testServer.uid " +
            "and t.active = true " +
            "and t.selectable = true " +
            "and std.serverType in (:serverTypes)")
    List<TestServer> findDistinctByActiveTrueAndSelectableTrueAndServerTypesIn(Collection<ServerType> serverTypes);
}
