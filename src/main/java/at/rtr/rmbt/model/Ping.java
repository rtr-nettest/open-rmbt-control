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
@Table(name = "ping")
public class Ping {

    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @Column(name = "value")
    private Long value;

    @Column(name = "value_server")
    private Long valueServer;

    @Column(name = "time_ns")
    private Long timeNs;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;
}
