package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.ServerType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ServerTypeDetailsResponse {

    @JsonProperty(value = "serverType")
    private final ServerType serverType;

    @JsonProperty(value = "port")
    private final Integer port;

    @JsonProperty(value = "portSsl")
    private final Integer portSsl;

    @JsonProperty(value = "encrypted")
    private final boolean encrypted;
}
