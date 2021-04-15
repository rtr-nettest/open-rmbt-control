package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ApplicationVersionResponse {

    @JsonProperty(value = "version")
    private final String version;

    @JsonProperty(value = "system_UUID")
    private final String systemUUID;

    @JsonProperty(value = "host")
    private final String host;
}
