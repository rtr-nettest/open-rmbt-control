package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PingRequest {

    @Schema(description = "Ping value in nanos on client side, used to get shortest ping", example = "129696354")
    @JsonProperty(value = "value")
    private final Long value;

    @Schema(description = "Ping value in nanos on server side, used to get median ping", example = "129835773")
    @JsonProperty(value = "value_server")
    private final Long valueServer;

    @Schema(description = "Relative time from the start of the test in nanos", example = "9789343538")
    @JsonProperty(value = "time_ns")
    private final Long timeNs;
}
