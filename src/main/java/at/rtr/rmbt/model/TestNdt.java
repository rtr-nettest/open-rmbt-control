package at.rtr.rmbt.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "test_ndt")
public class TestNdt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @OneToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @Column(name = "s2cspd")
    private Double s2cspd;

    @Column(name = "c2sspd")
    private Double c2sspd;

    @Column(name = "avgrtt")
    private Double avgrtt;

    @Column(name = "main")
    private String main;

    @Column(name = "stat")
    private String stat;

    @Column(name = "diag")
    private String diag;

    @Column(name = "time_ns")
    private Long timeNs;

    @Column(name = "time_end_ns")
    private Long timeEndNs;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;
}
