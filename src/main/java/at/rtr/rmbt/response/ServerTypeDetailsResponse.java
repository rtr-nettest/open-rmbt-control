package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.ServerType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ServerTypeDetailsResponse {

    private final ServerType serverType;

    private final Integer port;

    private final Integer portSsl;

    private final boolean encrypted;
}
