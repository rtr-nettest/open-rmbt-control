package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * One row of {@code /testServerStatus}: the latest reachability/latency sample for a test server +
 * IP protocol, plus its 24-hour aggregates. Mirrors the {@code test_server_qos_view} columns.
 */
@Getter
@Builder
@ToString
public class TestServerStatusResponse {

    @JsonProperty("server_uuid")
    private final UUID serverUuid;

    @JsonProperty("name")
    private final String name;

    /** Server type, e.g. {@code RMBThttp} / {@code RMBTudp}. */
    @JsonProperty("server_type")
    private final String serverType;

    /** 4 = IPv4, 6 = IPv6. */
    @JsonProperty("protocol")
    private final Integer protocol;

    @JsonProperty("reachable")
    private final Boolean reachable;

    @JsonProperty("latency_ms")
    private final Double latencyMs;

    @JsonProperty("max_latency_ms")
    private final Double maxLatencyMs;

    @JsonProperty("min_latency_ms")
    private final Double minLatencyMs;

    @JsonProperty("reachability_pct")
    private final Double reachabilityPct;

    /**
     * Maps a native-query row {@code [server_uuid, name, server_type, protocol, reachable, latency_ms,
     * max_latency_ms, min_latency_ms, reachability_pct]} to this DTO, tolerating the various JDBC
     * types PostgreSQL returns (UUID/String, Integer, Double, BigDecimal).
     */
    public static TestServerStatusResponse fromRow(final Object[] row) {
        return TestServerStatusResponse.builder()
                .serverUuid(toUuid(row[0]))
                .name((String) row[1])
                .serverType((String) row[2])
                .protocol(toInteger(row[3]))
                .reachable((Boolean) row[4])
                .latencyMs(toDouble(row[5]))
                .maxLatencyMs(toDouble(row[6]))
                .minLatencyMs(toDouble(row[7]))
                .reachabilityPct(toDouble(row[8]))
                .build();
    }

    private static UUID toUuid(final Object o) {
        if (o == null) {
            return null;
        }
        return (o instanceof UUID u) ? u : UUID.fromString(o.toString());
    }

    private static Integer toInteger(final Object o) {
        return (o instanceof Number n) ? n.intValue() : null;
    }

    private static Double toDouble(final Object o) {
        return (o instanceof Number n) ? n.doubleValue() : null;
    }
}
