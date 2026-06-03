package at.rtr.rmbt.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;


/**
 * Fences request class.
 */
@Getter
@Builder
public class FencesRequest {

    @NotNull(message = "Location is mandatory")
    @Schema(description = "Center of fence as latitude and longitude", example = "{\"latitude\": 48.197872928901063, \"longitude\": 16.349006434053081}")
    @JsonProperty(value = "location")
    private final SimpleLocationRequest location;

    @Schema(description = "Numeric id of technology", example = "41")
    @JsonProperty(value = "technology_id")
    private final Long technologyId;

    @Schema(description = "Name of technology", example = "NR NSA")
    @JsonProperty(value = "technology")
    private final String technology;

    @NotNull(message = "Time offset is mandatory")
    @Schema(description = "Time offset relative to /coverageRequest in ms, can be negative", example = "13506")
    @JsonProperty(value = "offset_ms")
    private final Long offsetMs;

    @NotNull(message = "Duration_ms is mandatory")
    @Schema(description = "Duration of client within fence in ms", example = "2123")
    @JsonProperty(value = "duration_ms")
    private final Long durationMs;

    @NotNull(message = "Radius is mandatory")
    @Schema(description = "Radius of fence in m", example = "25.4")
    @JsonProperty(value = "radius")
    private final Double radius;

    @Schema(description = "Median ping value in ms, can be null", example = "4.42")
    @JsonProperty(value = "avg_ping_ms")
    private final Double avgPingMs;

    @Schema(description = "Minimum signal (RSRP) of fence in dBm", example = "-103.5")
    @JsonProperty(value = "signal")
    private final Double signal;
}
