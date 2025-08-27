package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.ServerType;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "test_server")
@EqualsAndHashCode
@ToString
public class TestServer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_server_seq")
    @SequenceGenerator(name = "test_server_seq", sequenceName = "test_server_uid_seq", allocationSize = 1)
    @Column(name = "uid")
    private Long uid;

    @Column(name = "name")
    private String name;

    @Column(name = "web_address")
    private String webAddress;

    @Column(name = "port")
    private Integer port;

    @Column(name = "port_ssl")
    private Integer portSsl;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "geo_lat")
    private Double latitude;

    @Column(name = "geo_long")
    private Double longitude;

    @Column(name = "location")
    private Geometry location;

    @Column(name = "web_address_ipv4")
    private String webAddressIpV4;

    @Column(name = "web_address_ipv6")
    private String webAddressIpV6;

    @Column(name = "server_type")
    @Enumerated(EnumType.STRING)
    private ServerType serverType; //todo remove after release of new Android App

    @OneToMany(mappedBy = "testServer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ServerTypeDetails> serverTypeDetails = new HashSet<>();

    public void setServerTypeDetails(Set<ServerTypeDetails> serverTypeDetails) {
        if (Objects.nonNull(serverTypeDetails)) {
            serverTypeDetails.forEach(std -> std.setTestServer(this));
            this.serverTypeDetails.clear();
            this.serverTypeDetails.addAll(serverTypeDetails);
        } else {
            this.serverTypeDetails = new HashSet<>();
        }
    }

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "key")
    private String key;

    @Column(name = "selectable")
    private Boolean selectable;

    @Column(name = "node")
    private String node;

    @Column(name = "archived")
    private boolean archived;

    @Column(name = "coverage")
    private Boolean coverage;
}
