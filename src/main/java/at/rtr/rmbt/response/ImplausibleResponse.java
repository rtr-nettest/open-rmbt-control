package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImplausibleResponse extends ErrorResponse {

    @JsonProperty(value = "status")
    private final String status;

    @JsonProperty(value = "affected_rows")
    private final Integer affectedRows;
}
