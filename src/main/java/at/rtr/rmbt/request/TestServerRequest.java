package at.rtr.rmbt.request;

import at.rtr.rmbt.constant.Constants;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.locationtech.jts.geom.Geometry;

import java.util.Optional;
import java.util.Set;

@Getter
@EqualsAndHashCode
public class TestServerRequest {

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "webAddress")
    private String webAddress;

    @JsonProperty(value = "port")
    private Integer port;

    @JsonProperty(value = "portSsl")
    private Integer portSsl;

    @JsonProperty(value = "city")
    private String city;

    @JsonProperty(value = "country")
    private String country;

    @JsonProperty(value = "latitude")
    private Double latitude;

    @JsonProperty(value = "longitude")
    private Double longitude;

    @JsonDeserialize(using = GeometryDeserializer.class)
    @JsonProperty(value = "location")
    private Geometry location;

    @JsonProperty(value = "webAddressIpV4")
    private String webAddressIpV4;

    @JsonProperty(value = "webAddressIpV6")
    private String webAddressIpV6;

    @JsonProperty(value = "serverTypeDetails")
    private Set<ServerTypeDetailsRequest> serverTypeDetails;

    @JsonProperty(value = "priority")
    private Integer priority;

    @JsonProperty(value = "weight")
    private Integer weight;

    @JsonProperty(value = "active")
    private boolean active;

    @JsonProperty(value = "secretKey")
    private String secretKey;

    @JsonProperty(value = "selectable")
    private boolean selectable;

    @JsonProperty(value = "node")
    private String node;

    @JsonProperty(value = "encrypted")
    private boolean encrypted;

    @Builder
    public TestServerRequest(String name, String webAddress, Integer port, Integer portSsl, String city, String country, Double latitude, Double longitude, Geometry location, String webAddressIpV4, String webAddressIpV6, Set<ServerTypeDetailsRequest> serverTypeDetails, Integer priority, Integer weight, boolean active, String secretKey, boolean selectable, String node, boolean encrypted) {
        this.name = name;
        this.webAddress = webAddress;
        this.port = port;
        this.portSsl = portSsl;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.webAddressIpV4 = webAddressIpV4;
        this.webAddressIpV6 = webAddressIpV6;
        this.serverTypeDetails = serverTypeDetails;
        this.priority = Optional.ofNullable(priority).orElse(Constants.PRIORITY);
        this.weight = Optional.ofNullable(weight).orElse(Constants.WEIGHT);
        this.active = active;
        this.secretKey = secretKey;
        this.selectable = selectable;
        this.node = node;
        this.encrypted = encrypted;
    }
}
