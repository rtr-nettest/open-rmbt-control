package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.ServerType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ServerTypeDetailsRequest {

    @JsonProperty(value = "serverType")
    private final ServerType serverType;

    @JsonProperty(value = "port")
    private final Integer port;

    @JsonProperty(value = "portSsl")
    private final Integer portSsl;

    @JsonProperty(value = "encrypted")
    private final boolean encrypted;
}
