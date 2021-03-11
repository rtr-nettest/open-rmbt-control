package at.rtr.rmbt.response;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(value = "id")
    private final Long id;

    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @JsonProperty(value = "name")
    private final String name;

    @JsonProperty(value = "webAddress")
    private final String webAddress;

    @JsonProperty(value = "provider")
    private final ProviderResponse provider;

    @JsonProperty(value = "secretKey")
    private final String secretKey;

    @JsonProperty(value = "city")
    private final String city;

    @JsonProperty(value = "email")
    private final String email;

    @JsonProperty(value = "company")
    private final String company;

    @JsonProperty(value = "expiration")
    private final Date expiration;

    @JsonProperty(value = "ipAddress")
    private final String ipAddress;

    @JsonProperty(value = "comment")
    private final String comment;

    @JsonProperty(value = "countries")
    private final String countries;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "timeOfLastMeasurement")
    private final Timestamp timeOfLastMeasurement;

    @JsonProperty(value = "lastMeasurementSuccess")
    private final boolean lastMeasurementSuccess;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "lastSuccessfulMeasurement")
    private final Timestamp lastSuccessfulMeasurement;

    @JsonProperty(value = "port")
    private final Integer port;

    @JsonProperty(value = "portSsl")
    private final Integer portSsl;

    @JsonProperty(value = "country")
    private final String country;

    @JsonProperty(value = "latitude")
    private final Double latitude;

    @JsonProperty(value = "longitude")
    private final Double longitude;

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonProperty(value = "location")
    private final Geometry location;

    @JsonProperty(value = "webAddressIpV4")
    private final String webAddressIpV4;

    @JsonProperty(value = "webAddressIpV6")
    private final String webAddressIpV6;

    @JsonProperty(value = "serverTypeDetails")
    private final Set<ServerTypeDetailsResponse> serverTypeDetails;

    @JsonProperty(value = "priority")
    private final Integer priority;

    @JsonProperty(value = "weight")
    private final Integer weight;

    @JsonProperty(value = "active")
    private final Boolean active;

    @JsonProperty(value = "selectable")
    private final Boolean selectable;

    @JsonProperty(value = "node")
    private final String node;

    @JsonProperty(value = "encrypted")
    private final boolean encrypted;
}
