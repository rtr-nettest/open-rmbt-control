package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CapabilitiesRequest {

    private final ClassificationRequest classification;

    private final QosRequest qos;

    @JsonProperty(value = "RMBThttp")
    @ApiModelProperty(notes = "True, if the client can handle the RMBThttp protocol", example = "true")
    private final boolean rmbtHttp;

}
