package at.rtr.rmbt.request;

import at.rtr.rmbt.constant.Constants;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
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

    private Set<ServerTypeDetailsRequest> serverTypeDetails;

    private Integer priority;

    private Integer weight;

    private boolean active;

    private String secretKey;

    private boolean selectable;

    private String node;

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
