package at.rtr.rmbt.response;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Geometry;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = false)
public class TestServerResponse {

    private final Long id;

    private final UUID uuid;

    private final String name;

    private final String webAddress;

    private final ProviderResponse provider;

    private final String secretKey;

    private final String city;

    private final String email;

    private final String company;

    private final Date expiration;

    private final String ipAddress;

    private final String comment;

    private final String countries;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Timestamp timeOfLastMeasurement;

    private final boolean lastMeasurementSuccess;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Timestamp lastSuccessfulMeasurement;

    private final Integer port;

    private final Integer portSsl;

    private final String country;

    private final Double latitude;

    private final Double longitude;

    @JsonSerialize(using = GeometrySerializer.class)
    private final Geometry location;

    private final String webAddressIpV4;

    private final String webAddressIpV6;

    private final Set<ServerTypeDetailsResponse> serverTypeDetails;

    private final Integer priority;

    private final Integer weight;

    private final Boolean active;

    private final Boolean selectable;

    private final String node;

    private final boolean encrypted;
}
