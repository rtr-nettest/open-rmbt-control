package at.rtr.rmbt.response;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class SignalMeasurementResponse {

    private final UUID testUuid;

    private final UUID userUuid;

    private final String testType;

    private final String technology;

    @JsonSerialize(using = GeometrySerializer.class)
    private final Geometry location;

    private final Integer duration;

    private final ZonedDateTime time;

}
