package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class HistoryRequest {

    @ApiModelProperty(notes = "Client UUID")
    @JsonProperty(value = "uuid")
    private final UUID clientUUID;

    @ApiModelProperty(notes = "2 letters language code or language code with region", example = "en")
    @JsonProperty(value = "language")
    private final String language;

    @ApiModelProperty(notes = "Result limit")
    @JsonProperty(value = "result_limit")
    private final Integer resultLimit;

    @ApiModelProperty(notes = "Result offset")
    @JsonProperty(value = "result_offset")
    private final Integer resultOffset;

    @ApiModelProperty(notes = "Devices")
    @JsonProperty(value = "devices")
    private final List<String> devices;

    @ApiModelProperty(notes = "Networks")
    @JsonProperty(value = "networks")
    private final List<String> networks;

    @ApiModelProperty(notes = "Capabilities")
    @JsonProperty(value = "capabilities")
    private final CapabilitiesRequest capabilities;
}
