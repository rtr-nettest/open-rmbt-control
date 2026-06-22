package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UdpPingRequest {

    @Schema(description = "Send time relative to the start of the test in nanos", example = "9789343538")
    @JsonProperty(value = "t_ns")
    private final Long timeNs;

    @Schema(description = "Round-trip time in milliseconds, or null if the ping was lost", example = "23.4")
    @JsonProperty(value = "value_ms")
    private final Float valueMs;
}
