package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.TestServerQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestServerQualityRepository extends JpaRepository<TestServerQuality, Long> {

    /**
     * Per server + IP protocol: the latest reachability/latency sample, plus 24h aggregates
     * (max/min latency, reachability %, sample count). Mirrors the {@code test_server_qos_view} view.
     * Optionally filtered to a single {@code test_server.uuid} (pass {@code null} for all servers).
     *
     * <p>Returns rows of {@code [server_uuid, name, server_type, protocol, reachable, latency_ms,
     * max_latency_ms, min_latency_ms, reachability_pct]} (see {@code TestServerStatusResponse.fromRow}).
     */
    @Query(value =
            "WITH latest_entries AS ( " +
            "    SELECT DISTINCT ON (tsq.server_uuid, tsq.protocol) " +
            "        tsq.server_uuid, tsq.protocol, tsq.reachable, tsq.latency_ms " +
            "    FROM test_server_quality tsq " +
            "    ORDER BY tsq.server_uuid, tsq.protocol, tsq.timestamp DESC " +
            "), stats_24h AS ( " +
            "    SELECT tsq.server_uuid, tsq.protocol, " +
            "        max(tsq.latency_ms) AS max_latency_ms, " +
            "        min(tsq.latency_ms) AS min_latency_ms, " +
            "        round(100.0 * count(*) FILTER (WHERE tsq.reachable)::numeric / NULLIF(count(*), 0)::numeric, 2) AS reachability_pct " +
            "    FROM test_server_quality tsq " +
            "    WHERE tsq.timestamp > (now() - interval '24 hours') " +
            "    GROUP BY tsq.server_uuid, tsq.protocol " +
            ") " +
            "SELECT latest.server_uuid AS server_uuid, ts.name AS name, ts.server_type AS server_type, " +
            "    latest.protocol AS protocol, latest.reachable AS reachable, latest.latency_ms AS latency_ms, " +
            "    stats.max_latency_ms AS max_latency_ms, stats.min_latency_ms AS min_latency_ms, " +
            "    stats.reachability_pct AS reachability_pct " +
            "FROM latest_entries latest " +
            "    JOIN test_server ts ON ts.uuid = latest.server_uuid " +
            "    LEFT JOIN stats_24h stats ON stats.server_uuid = latest.server_uuid AND stats.protocol = latest.protocol " +
            "WHERE (CAST(:testServer AS uuid) IS NULL OR ts.uuid = CAST(:testServer AS uuid)) " +
            "ORDER BY latest.reachable DESC, latest.latency_ms",
            nativeQuery = true)
    List<Object[]> findStatus(@Param("testServer") String testServer);
}
