package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SettingsHistoryResponse {

    @ApiModelProperty(notes = "Devices")
    @JsonProperty(value = "devices")
    private final List<String> devices;

    @ApiModelProperty(notes = "Networks")
    @JsonProperty(value = "networks")
    private final List<String> networks;
}
