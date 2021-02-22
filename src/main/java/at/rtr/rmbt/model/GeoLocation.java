package at.rtr.rmbt.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "geo_location")
public class GeoLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long id;

    @Column(name = "geo_location_uuid")
    private UUID geoLocationUUID;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;

    @Column(name = "test_id")
    private Long testId;

    @Column(name = "time_ns")
    private Long timeNs;

    @CreationTimestamp
    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "bearing")
    private Double bearing;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "provider")
    private String provider;

    @Column(name = "geo_lat")
    private Double geoLat;

    @Column(name = "geo_long")
    private Double geoLong;

    @Column(name = "mock_location")
    private boolean mockLocation;

    @Column(name = "location")
    private Geometry location;
}
