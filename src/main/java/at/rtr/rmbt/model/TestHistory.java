package at.rtr.rmbt.model;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TestHistory {

    @Id
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "open_test_uuid")
    private UUID openTestUuid;

    @Column(name = "time")
    private Date time;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "speed_upload")
    private Integer speedUpload;

    @Column(name = "speed_download")
    private Integer speedDownload;

    @Column(name = "ping_median")
    private Long pingMedian;

    @Column(name = "lte_rsrp")
    private Integer lteRsrp;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "dual_sim")
    private Boolean dualSim;

    @Column(name = "sim_count")
    private Integer simCount;

    @Column(name = "network_type")
    private Integer networkType;

    @Column(name = "network_type_group_name")
    private String networkTypeGroupName;

    @Column(name = "loop_uuid")
    private UUID loopUuid;

    @Column(name = "model")
    private String model;

    @Column(name = "status")
    private String status;
}
