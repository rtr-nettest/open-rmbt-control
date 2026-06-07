package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * One row of {@code /testServerStatus}: the latest reachability/latency sample for a test server +
 * IP protocol, plus its 24-hour aggregates. Mirrors the {@code test_server_qos_view} columns.
 */
@Getter
@Builder
@ToString
public class TestServerStatusResponse {

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
     * Maps a native-query row {@code [name, server_type, protocol, reachable, latency_ms,
     * max_latency_ms, min_latency_ms, reachability_pct]} to this DTO, tolerating the various JDBC
     * numeric types PostgreSQL returns (Integer, Double, BigDecimal).
     */
    public static TestServerStatusResponse fromRow(final Object[] row) {
        return TestServerStatusResponse.builder()
                .name((String) row[0])
                .serverType((String) row[1])
                .protocol(toInteger(row[2]))
                .reachable((Boolean) row[3])
                .latencyMs(toDouble(row[4]))
                .maxLatencyMs(toDouble(row[5]))
                .minLatencyMs(toDouble(row[6]))
                .reachabilityPct(toDouble(row[7]))
                .build();
    }

    private static Integer toInteger(final Object o) {
        return (o instanceof Number n) ? n.intValue() : null;
    }

    private static Double toDouble(final Object o) {
        return (o instanceof Number n) ? n.doubleValue() : null;
    }
}
