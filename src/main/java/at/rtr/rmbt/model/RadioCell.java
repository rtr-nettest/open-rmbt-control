package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.NetworkGroupName;
import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "radio_cell")
@ToString
public class RadioCell implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "uuid")
    private UUID uuid;

    @JoinColumn(
            name = "open_test_uuid",
            referencedColumnName = "open_test_uuid",
            nullable = false,
            updatable = false
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Test test;

    @Column(name = "technology")
    private NetworkGroupName technology;

    @Column(name = "mnc")
    private Long mnc;

    @Column(name = "mcc")
    private Long mcc;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "area_code")
    private Long areaCode;

    @Column(name = "primary_scrambling_code")
    private Integer primaryScramblingCode;

    @Column(name = "primary_data_subscription")
    private String primaryDataSubscription;

    @Column(name = "registered")
    private boolean registered;

    @Column(name = "channel_number")
    private Integer channelNumber;

    @Column(name = "active")
    private boolean active;

    @Column(name = "cell_state")
    private String cellState;
}
