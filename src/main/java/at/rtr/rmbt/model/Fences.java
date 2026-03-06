package at.rtr.rmbt.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.time.ZonedDateTime;
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
    @Schema(description = "Sequence uid", example = "12345")
    @Column(name = "uid") // serial4
    private Long id;

    @Schema(description = "Open Test UUID", example = "5fa8a7a8-e70c-490a-a771-b0d191d64410")
    @Column(name = "open_test_uuid") // UUID
    private UUID openTestUUID;

    @Schema(description = "Number of fence, starts with 1", example = "1")
    @Column(name = "fence_id") // int4
    private Long fenceId;

    @Schema(description = "Numeric id of technology", example = "41")
    @Column(name = "technology_id") // int4
    private Long technologyId;

    @Schema(description = "Median ping value in ms, can be null", example = "4.42")
    @Column(name = "avg_ping_ms") // float8
    private Double avgPingMs;

    @Schema(description = "Name of technology", example = "5G SA")
    @Column(name = "technology") // varchar
    private String technology;

    @Schema(description = "Time offset relative to /coverageRequest in ms, can be negative", example = "13506")
    @Column(name = "offset_ms") // int4
    private Long offsetMs;

    @Schema(description = "Duration of client within fence in ms", example = "2123")
    @Column(name = "duration_ms") // int4
    private Long durationMs;

    @Schema(description = "Radius of fence in meter", example = "25.4")
    @Column(name = "radius") // float8
    private Double radius;

    @Schema(description = "Timestamp of fence", example = "2026-01-11 15:48:25.321 +0100")
    @Column(name = "fence_time") // timestamptz
    private ZonedDateTime fenceTime;

    @Schema(description = "point geometry with 4326 projection", example = "POINT (16.3738 48.2082)")
    @Column(name = "geom4326") // name in PostgreSQL
    private Geometry geom4326; // name in Java

    @Schema(description = "Minimum signal (RSRP) of fence in dBm", example = "-103.5")
    @Column(name = "signal") // float8
    private Double signal;
}

