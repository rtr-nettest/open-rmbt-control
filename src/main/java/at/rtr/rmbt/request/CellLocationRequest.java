package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CellLocationRequest {

    @JsonProperty(value = "primary_scrambling_code")
    private final Integer primaryScramblingCode;

    @JsonProperty(value = "time")
    private final Long time;

    @JsonProperty(value = "time_ns")
    private final Long timeNs;

    @JsonProperty(value = "area_code")
    private final Long areaCode;

    @JsonProperty(value = "location_id")
    private final Long locationId;
}
