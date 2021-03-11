package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GeoLocationRequest {

    @ApiModelProperty(notes = "Accurecy", example = "13.014")
    @JsonProperty(value = "accuracy")
    private final Double accuracy;

    @ApiModelProperty(notes = "Age", example = "3858849231")
    @JsonProperty(value = "age")
    private final Long age;

    @ApiModelProperty(notes = "Geolocation altitude", example = "148.29998779296875")
    @JsonProperty(value = "altitude")
    private final Double altitude;

    @ApiModelProperty(notes = "Geolocation bearing", example = "0.0")
    @JsonProperty(value = "bearing")
    private final Double bearing;

    @ApiModelProperty(notes = "Geolocation latitude", example = "50.037305")
    @JsonProperty(value = "geo_lat")
    private final Double geoLat;

    @ApiModelProperty(notes = "Geolocation longitude", example = "36.3543913")
    @JsonProperty(value = "geo_long")
    private final Double geoLong;

    @ApiModelProperty(notes = "Mock locatoin", example = "false")
    @JsonProperty(value = "mock_location")
    private final boolean mockLocation;

    @ApiModelProperty(notes = "Provider", example = "network")
    @JsonProperty(value = "provider")
    private final String provider;

    @ApiModelProperty(notes = "Satellites", example = "0")
    @JsonProperty(value = "satellites")
    private final Long satellites;

    @JsonProperty(value = "speed")
    private final Double speed;

    @ApiModelProperty(notes = "Timestamp of the information in millis from Location.time", example = "1614328558515")
    @JsonProperty(value = "tstamp")
    private final Long tstamp;

    @ApiModelProperty(notes = "Relative time from the start of the test System.currentTimeMillis() - millis of the test start", example = "-2282952030")
    @JsonProperty(value = "time_ns")
    private final Long timeNs;
}
