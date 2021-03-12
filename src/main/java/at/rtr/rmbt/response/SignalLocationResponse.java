package at.rtr.rmbt.response;

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
public class SignalLocationResponse {

    @JsonProperty(value = "time")
    private final Double time;

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonProperty(value = "location")
    private final Geometry location;

    @JsonProperty(value = "accuracy")
    private final String accuracy;

    @JsonProperty(value = "speed")
    private final String speed;

    @JsonProperty(value = "bearing")
    private final String bearing;

    @JsonProperty(value = "altitude")
    private final String altitude;
}
