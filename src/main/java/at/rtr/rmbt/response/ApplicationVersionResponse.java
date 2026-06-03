package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Application version response class.
 */
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "profile")
    private final String profile;

}
