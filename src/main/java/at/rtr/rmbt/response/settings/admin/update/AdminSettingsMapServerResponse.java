package at.rtr.rmbt.response.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsMapServerResponse {

    @JsonProperty(value = "port")
    private final String port;

    @JsonProperty(value = "host")
    private final String host;

    @JsonProperty(value = "ssl")
    private final String ssl;
}
