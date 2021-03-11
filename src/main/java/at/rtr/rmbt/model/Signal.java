package at.rtr.rmbt.model;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "signal")
public class Signal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @Column(name = "signal_uuid")
    private UUID signalUUID;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;

    @CreationTimestamp
    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "time_ns")
    private Long timeNs;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "network_type_id")
    private Integer networkTypeId;

    @Column(name = "wifi_link_speed")
    private Integer wifiLinkSpeed;

    @Column(name = "gsm_bit_error_rate")
    private Integer gsmBitErrorRate;

    @Column(name = "wifi_rssi")
    private Integer wifiRSSI;

    @Column(name = "lte_rsrp")
    private Integer lteRSRP;

    @Column(name = "lte_rsrq")
    private Integer lteRSRQ;

    @Column(name = "lte_rssnr")
    private Integer lteRSSNR;

    @Column(name = "lte_cqi")
    private Integer lteCQI;
}
