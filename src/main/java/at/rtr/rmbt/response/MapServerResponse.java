package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class MapServerResponse {

    @JsonProperty(value = "port")
    private final Long port;

    @JsonProperty(value = "host")
    private final String host;

    @JsonProperty(value = "ssl")
    private final boolean ssl;
}
