package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.NetworkGroupName;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "radio_cell")
public class RadioCell implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;

    @Enumerated(EnumType.STRING)
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
    private Long primaryScramblingCode;

    @Column(name = "registered")
    private boolean registered;

    @Column(name = "channel_number")
    private Long channelNumber;

    @Column(name = "active")
    private boolean active;
}
