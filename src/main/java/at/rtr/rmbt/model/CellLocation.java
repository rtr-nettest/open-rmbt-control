package at.rtr.rmbt.model;

import lombok.*;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "cell_location")
public class CellLocation {

    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "area_code")
    private Long areaCode;

    @Column(name = "primary_scrambling_code")
    private Integer primaryScramblingCode;

    @Column(name = "time_ns")
    private Long timeNs;

    @Column(name = "time")
    private ZonedDateTime time;
}
