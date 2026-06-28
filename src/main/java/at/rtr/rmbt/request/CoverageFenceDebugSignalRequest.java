package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CoverageFenceDebugSignalRequest {

    @JsonProperty("technology")
    private final String technology;

    @JsonProperty("signal_dbm")
    private final Integer signalDbm;

    @JsonProperty("timestamp_ms")
    private final Long timestampMs;
}
