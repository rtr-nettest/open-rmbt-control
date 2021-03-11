package at.rtr.rmbt.request.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsMapServerRequest {

    @ApiModelProperty(value = "Port of the map server", example = "443")
    @JsonProperty(value = "port")
    private final String port;

    @ApiModelProperty(value = "Hostname of the map server", example = "dev.netztest.at")
    @JsonProperty(value = "host")
    private final String host;

    @ApiModelProperty(value = "True if use ssl", example = "true")
    @JsonProperty(value = "ssl")
    private final String ssl;
}
