package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class QosRequest {

    @JsonProperty(value = "supports_info")
    @Schema(description = "True, if client third state (=INFO) is supported", example = "true")
    private final boolean supportsInfo;
}
