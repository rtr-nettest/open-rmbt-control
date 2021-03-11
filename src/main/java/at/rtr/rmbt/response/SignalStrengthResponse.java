package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.NetworkGroupName;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.locationtech.jts.geom.Geometry;

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

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonProperty(value = "location")
    private final Geometry location;

    @JsonProperty(value = "ci")
    private final Integer ci;

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

    @JsonProperty(value = "accuracy")
    private final String accuracy;

    @JsonProperty(value = "speed")
    private final String speed;

    @JsonProperty(value = "bearing")
    private final String bearing;

    @JsonProperty(value = "altitude")
    private final String altitude;
}
