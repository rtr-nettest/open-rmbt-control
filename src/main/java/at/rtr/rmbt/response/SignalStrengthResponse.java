package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.NetworkGroupName;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
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

    private final Double time;

    private final String technology;

    private final String signalStrength;

    @JsonSerialize(using = GeometrySerializer.class)
    private final Geometry location;

    private final Long ci;

    private final Long tac;

    private final Long pci;

    private final Long earfcn;

    private final Double frequency;

    private final Integer band;

    private final String accuracy;

    private final String speed;

    private final String bearing;

    private final String altitude;
}
