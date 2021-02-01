package at.rtr.rmbt.response;

import at.rtr.rmbt.model.enums.ServerType;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Geometry;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = false)
public class TestServerResponse {

    private final Long uid;

    private final String name;

    private final String webAddress;

    private final Integer port;

    private final Integer portSsl;

    private final String city;

    private final String country;

    private final Double latitude;

    private final Double longitude;

    @JsonSerialize(using = GeometrySerializer.class)
    private final Geometry location;

    private final String webAddressIpV4;

    private final String webAddressIpV6;

    private final ServerType serverType;

    private final Integer priority;

    private final Integer weight;

    private final Boolean active;

    private final String key;

    private final Boolean selectable;

    private final String node;
}
