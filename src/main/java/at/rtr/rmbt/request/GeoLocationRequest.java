package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GeoLocationRequest {

    private final Double accuracy;

    private final Long age;

    private final Double altitude;

    private final Double bearing;

    @JsonProperty(value = "geo_lat")
    private final Double geoLat;

    @JsonProperty(value = "geo_long")
    private final Double geoLong;

    @JsonProperty(value = "mock_location")
    private final boolean mockLocation;

    private final String provider;

    private final Long satellites;

    private final Double speed;

    @JsonProperty(value = "tstamp")
    private final Long tstamp;

    @JsonProperty(value = "time_ns")
    private final Long timeNs;
}
