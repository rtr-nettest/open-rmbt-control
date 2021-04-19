package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class ResultUpdateRequest {

    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @JsonProperty(value = "aborted")
    private final boolean isAborted;

    @JsonProperty(value = "geo_lat")
    private final Double geoLat;

    @JsonProperty(value = "geo_long")
    private final Double geoLong;

    @JsonProperty(value = "accuracy")
    private final Double accuracy;

    @JsonProperty(value = "provider")
    private final String provider;
}
