package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class SignalDetailsResponse {

    @JsonProperty(value = "signalStrength")
    private final List<SignalStrengthResponse> signalStrength;

    @JsonProperty(value = "testResponse")
    private final TestResponse testResponse;
}
