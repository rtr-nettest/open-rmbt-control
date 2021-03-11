package at.rtr.rmbt.response;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(value = "testUuid")
    private final UUID testUuid;

    @JsonProperty(value = "userUuid")
    private final UUID userUuid;

    @JsonProperty(value = "testType")
    private final String testType;

    @JsonProperty(value = "technology")
    private final String technology;

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonProperty(value = "location")
    private final Geometry location;

    @JsonProperty(value = "duration")
    private final Integer duration;

    @JsonProperty(value = "time")
    private final ZonedDateTime time;

}
