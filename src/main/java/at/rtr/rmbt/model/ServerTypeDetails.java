package at.rtr.rmbt.model;

import at.rtr.rmbt.enums.ServerType;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "test_server_types")
public class ServerTypeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    @ManyToOne
    @JoinColumn(name = "test_server_uid")
    private TestServer testServer;

    @Column(name = "server_type")
    private ServerType serverType;

    @Column(name = "port")
    private Integer port;

    @Column(name = "port_ssl")
    private Integer portSsl;

    @Column(name = "encrypted")
    private boolean encrypted;
}
