package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class IpResponse {

    @JsonProperty(value = "v")
    private final String version;

    @JsonProperty(value = "ip")
    private final String ip;
}
