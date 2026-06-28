package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CoverageFenceDebugPingRequest {

    @JsonProperty("sequence")
    private final Integer sequence;

    /** round-trip time in milliseconds, or null if the ping was lost */
    @JsonProperty("rtt_ms")
    private final Double rttMs;
}
