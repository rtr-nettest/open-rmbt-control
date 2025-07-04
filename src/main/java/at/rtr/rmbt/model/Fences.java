package at.rtr.rmbt.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fences")
public class Fences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid") // serial4
    private Long id;

    @Column(name = "open_test_uuid") // UUID
    private UUID openTestUUID;

    @Column(name = "fence_id") // int4
    private Long fenceId;

    @Column(name = "technology_id") // int4
    private Long technologyId;

    @Column(name = "technology") // varchar
    private String technology;

    @Column(name = "offset_ms") // int4
    private Long offsetMs;

    @Column(name = "duration_ms") // int4
    private Long durationMs;

    @Column(name = "radius") // int4
    private Integer radius;

    // point geometry with 4326 projection
    @Column(name = "geom4326") // name in PostgreSQL
    private Geometry geom4326; // name in Java
}

