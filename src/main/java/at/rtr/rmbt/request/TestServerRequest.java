package at.rtr.rmbt.request;

import at.rtr.rmbt.model.enums.ServerType;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TestServerRequest {

    private String name;

    private String webAddress;

    private Integer port;

    private Integer portSsl;

    private String city;

    private String country;

    private Double latitude;

    private Double longitude;

    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry location;

    private String webAddressIpV4;

    private String webAddressIpV6;

    private ServerType serverType;

    private Integer priority;

    private Integer weight;

    private Boolean active;

    private String key;

    private Boolean selectable;

    private String node;
}
