package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SettingsHistoryResponse {

    @Schema(description = "Devices")
    @JsonProperty(value = "devices")
    private final List<String> devices;

    @Schema(description = "Networks")
    @JsonProperty(value = "networks")
    private final List<String> networks;
}
