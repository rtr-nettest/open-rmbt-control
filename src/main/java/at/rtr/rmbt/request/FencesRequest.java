package at.rtr.rmbt.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class FencesRequest {

    @Schema(description = "Center of fence as latitude and longitude", example = "{\"latitude\": 48.197872928901063, \"longitude\": 16.349006434053081}")
    @JsonProperty(value = "location")
    private final SimpleLocationRequest location;

    @Schema(description = "Numeric id of technology", example = "41")
    @JsonProperty(value = "technology_id")
    private final Long technologyId;

    @Schema(description = "Name of technology", example = "NR NSA")
    @JsonProperty(value = "technology")
    private final String technology;

    @Schema(description = "Time offset from measurement start in ms", example = "13000")
    @JsonProperty(value = "offset_ms")
    private final Long offsetMs;

    @Schema(description = "Duration of fence in ms", example = "2123")
    @JsonProperty(value = "duration_ms")
    private final Long durationMs;

    @Schema(description = "Radius of fence in m", example = "25")
    @JsonProperty(value = "radius")
    private final Long radius;

    @Schema(description = "Average ping in ms", example = "15")
    @JsonProperty(value = "avg_ping_ms")
    private final Long avgPingMs;

    // from iOS prototype, to be removed
    @Schema(description = "Absolute client time in us", example = "1750762114081328")
    @JsonProperty(value = "timestamp_microseconds")
    private final Long timestampMicroseconds;
}
