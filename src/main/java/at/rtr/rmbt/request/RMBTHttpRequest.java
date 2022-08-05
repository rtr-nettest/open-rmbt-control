package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RMBTHttpRequest {

    @JsonProperty(value = "RMBThttp")
    @Schema(description = "True, if the client can handle the RMBThttp protocol", example = "true")
    private final boolean rmbtHttp;
}
