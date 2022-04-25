package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class CapabilitiesRequest {

    @JsonProperty(value = "classification")
    private final ClassificationRequest classification;

    @JsonProperty(value = "qos")
    private final QosRequest qos;

    @JsonProperty(value = "RMBThttp")
    @Schema(description = "True, if the client can handle the RMBThttp protocol", example = "true")
    private final boolean rmbtHttp;

}
