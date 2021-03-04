package at.rtr.rmbt.response;

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

    private final List<SignalStrengthResponse> signalStrength;

    private final TestResponse testResponse;
}
