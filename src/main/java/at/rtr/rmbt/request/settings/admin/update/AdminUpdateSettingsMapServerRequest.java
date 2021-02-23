package at.rtr.rmbt.request.settings.admin.update;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsMapServerRequest {

    @ApiModelProperty(value = "Port of the map server", example = "443")
    private final String port;

    @ApiModelProperty(value = "Hostname of the map server", example = "dev.netztest.at")
    private final String host;

    @ApiModelProperty(value = "True if use ssl", example = "true")
    private final String ssl;
}
