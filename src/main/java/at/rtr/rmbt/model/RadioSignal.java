package at.rtr.rmbt.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "radio_signal")
public class RadioSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "radio_signal_uuid")
    private UUID radioSignalUUID;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;

    @Column(name = "cell_uuid")
    private UUID cellUUID;

    @Column(name = "time_ns")
    private Long timeNs;

    @Column(name = "time_ns_last")
    private Long timeNsLast;

    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "lte_rsrp")
    private Integer lteRSRP;

    @Column(name = "lte_rsrq")
    private Integer lteRSRQ;

    @Column(name = "lte_rssnr")
    private Integer lteRSSNR;

    @Column(name = "lte_cqi")
    private Integer lteCQI;

    @Column(name = "bit_error_rate")
    private Integer bitErrorRate;

    @Column(name = "timing_advance")
    private Integer timingAdvance;

    @Column(name = "wifi_link_speed")
    private Integer wifiLinkSpeed;

    @Column(name = "network_type_id")
    private Integer networkTypeId;
}
