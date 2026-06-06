package at.rtr.rmbt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * One reachability/latency sample for a test server, recorded by the scheduled test-server quality
 * check (RMBT PING over WebSocket/TLS, separately for IPv4 and IPv6).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "test_server_quality")
public class TestServerQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_server_quality_seq")
    @SequenceGenerator(name = "test_server_quality_seq", sequenceName = "test_server_quality_uid_seq", allocationSize = 1)
    @Column(name = "uid")
    private Long uid;

    @Column(name = "server_uuid", nullable = false)
    private UUID serverUuid;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    /** IP protocol of the measurement: {@code 4} = IPv4, {@code 6} = IPv6. */
    @Column(name = "protocol", nullable = false)
    private Integer protocol;

    @Column(name = "reachable", nullable = false)
    private Boolean reachable;

    /** Client-measured PING-&gt;PONG round-trip in milliseconds; {@code null} when unreachable. */
    @Column(name = "latency_ms")
    private Double latencyMs;
}
