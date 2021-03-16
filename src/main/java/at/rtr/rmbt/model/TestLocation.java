package at.rtr.rmbt.model;

import lombok.*;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "test_location")
public class TestLocation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @Column(name = "open_test_uuid")
    private UUID openTestUUID;

    @OneToOne
    @JoinColumn(name = "geo_location_uuid", referencedColumnName = "geo_location_uuid")
    private GeoLocation geoLocation;

    @Column(name = "location")
    private Geometry location;

    @Column(name = "geo_long")
    private Double geoLong;

    @Column(name = "geo_lat")
    private Double geoLat;

    @Column(name = "geo_accuracy")
    private Double geoAccuracy;

    @Column(name = "geo_provider")
    private String geoProvider;

    @OneToOne
    @JoinColumn(name = "kg_nr_bev", referencedColumnName = "kg_nr_int")
    private AdministrativeBoundaries administrativeBoundaries;

    @Column(name = "gkz_bev")
    private Integer gkzBev;

    @Column(name = "gkz_sa")
    private Integer gkzSa;

    @Column(name = "land_cover")
    private Integer landCover;

    @Column(name = "settlement_type")
    private Integer settlementType;

    @JoinColumn(name = "link_id", referencedColumnName = "link_id")
    @OneToOne
    private Linknet linknet;

    @Column(name = "link_name")
    private String linkName;

    @Column(name = "link_distance")
    private Integer linkDistance;

    @Column(name = "frc")
    private Integer frc;

    @Column(name = "edge_id")
    private Integer edgeId;

    @Column(name = "country_location")
    private String countryLocation;

    @Column(name = "dtm_level")
    private Integer dtmLevel;
}