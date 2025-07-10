package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class ResultUpdateRequest {

    @Schema(description = "Client UUID", example = "1347ccdf-f31b-453d-98bc-71f3889f09cd")
    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @Schema(description = "Test UUID", example = "a6e5d1bb-d466-437a-ad25-fda294ade359")
    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @Schema(description = "Test failed as it was aborted by the user", example = "true")
    @JsonProperty(value = "aborted", defaultValue = "false")
    private final boolean isAborted;

    @Schema(description = "Other reason for test failure", example = "true")
    @JsonProperty(value = "failed", defaultValue = "false")
    private final boolean isFailed;

    @Schema(description = "Geographic latitude (WGS84)", example = "15.4140672")
    @JsonProperty(value = "geo_lat")
    private final Double geoLat;

    @Schema(description = "Geographic longitude (WGS84)", example = "47.0843392")
    @JsonProperty(value = "geo_long")
    private final Double geoLong;

    @Schema(description = "Accuracy in meter of geographic location", example = "10.392")
    @JsonProperty(value = "accuracy")
    private final Double accuracy;

    @Schema(description = "Provider (source) of geographic location", example = "GPS")
    @JsonProperty(value = "provider")
    private final String provider;
}
