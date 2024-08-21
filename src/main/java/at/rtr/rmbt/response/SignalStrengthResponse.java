package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class SignalStrengthResponse {

    @JsonProperty(value = "time")
    private final Double time;

    @JsonProperty(value = "technology")
    private final String technology;

    @JsonProperty(value = "signalStrength")
    private final String signalStrength;

    @JsonProperty(value = "ci")
    private final Long ci;

    @JsonProperty(value = "tac")
    private final Long tac;

    @JsonProperty(value = "pci")
    private final Integer pci;

    @JsonProperty(value = "earfcn")
    private final Integer earfcn;

    @JsonProperty(value = "frequency")
    private final Double frequency;

    @JsonProperty(value = "band")
    private final Integer band;
}
