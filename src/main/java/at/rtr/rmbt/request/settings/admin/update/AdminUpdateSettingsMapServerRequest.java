package at.rtr.rmbt.request.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsMapServerRequest {

    @Schema(description = "Port of the map server", example = "443")
    @JsonProperty(value = "port")
    private final String port;

    @Schema(description = "Hostname of the map server", example = "dev.netztest.at")
    @JsonProperty(value = "host")
    private final String host;

    @Schema(description = "True if use ssl", example = "true")
    @JsonProperty(value = "ssl")
    private final String ssl;
}
