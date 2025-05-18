package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GeoLocationRequest {

    @Schema(description = "Accuracy", example = "13.014")
    @JsonProperty(value = "accuracy")
    private final Double accuracy;

    @Schema(description = "Age", example = "3858849231")
    @JsonProperty(value = "age")
    private final Long age;

    @Schema(description = "Geolocation altitude", example = "148.29998779296875")
    @JsonProperty(value = "altitude")
    private final Double altitude;

    @Schema(description = "Geolocation bearing", example = "0.0")
    @JsonProperty(value = "bearing")
    private final Double bearing;

    @Schema(description = "Geolocation latitude", example = "50.037305")
    @JsonProperty(value = "geo_lat")
    private final Double geoLat;

    @Schema(description = "Geolocation longitude", example = "36.3543913")
    @JsonProperty(value = "geo_long")
    private final Double geoLong;

    @Schema(description = "Mock location", example = "false")
    @JsonProperty(value = "mock_location")
    private final Boolean mockLocation;

    @Schema(description = "Provider", example = "network")
    @JsonProperty(value = "provider")
    private final String provider;

    @Schema(description = "Satellites", example = "0")
    @JsonProperty(value = "satellites")
    private final Long satellites;

    @JsonProperty(value = "speed")
    private final Double speed;

    @Schema(description = "Timestamp of the information in millis from Location.time", example = "1614328558515")
    @JsonProperty(value = "tstamp")
    private final Long tstamp;

    @Schema(description = "Relative time from the start of the test System.currentTimeMillis() - millis of the test start", example = "-2282952030")
    @JsonProperty(value = "time_ns")
    private final Long timeNs;
}
