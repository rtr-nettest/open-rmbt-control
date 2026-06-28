package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CoverageFenceDebugTechnologyRequest {

    @JsonProperty("technology")
    private final String technology;

    @JsonProperty("technology_id")
    private final Integer technologyId;

    @JsonProperty("frequency_band")
    private final String frequencyBand;

    @JsonProperty("timestamp_ms")
    private final Long timestampMs;
}
