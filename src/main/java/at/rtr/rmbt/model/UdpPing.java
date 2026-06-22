package at.rtr.rmbt.model;

import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "udp_ping")
public class UdpPing {

    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;

    /** Send time relative to the measurement start, in nanoseconds. */
    @Column(name = "time_ns")
    private Long timeNs;

    /** Round-trip time in milliseconds; {@code null} when the ping was lost. */
    @Column(name = "ping_ms")
    private Float pingMs;
}
