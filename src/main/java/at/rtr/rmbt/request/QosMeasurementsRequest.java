package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class QosMeasurementsRequest {
    @NotNull
    @ApiModelProperty(notes = "Uuid of the test", example = "c373f294-f332-4f1a-999e-a87a12523f4b")
    @JsonProperty("test_uuid")
    private UUID testUuid;

    @ApiModelProperty(notes = "Language code of the client language", example = "en")
    @JsonProperty("language")
    private String language;

    @ApiModelProperty(notes = "Capabilities")
    @JsonProperty("capabilities")
    private CapabilitiesRequest capabilities;
}
