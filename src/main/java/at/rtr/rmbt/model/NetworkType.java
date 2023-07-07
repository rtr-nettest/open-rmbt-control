package at.rtr.rmbt.model;


import at.rtr.rmbt.enums.NetworkGroupName;
import at.rtr.rmbt.enums.NetworkGroupType;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name = "network_type")
public class NetworkType {

    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;

    @Column(name = "name")
    private String name;

    @Column(name = "group_name")
    private NetworkGroupName groupName;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NetworkGroupType type;

    @Column(name = "technology_order")
    private Integer technologyOrder;

    @Column(name = "min_speed_download_kbps")
    private Integer minSpeedDownloadKbps;

    @Column(name = "max_speed_download_kbps")
    private Integer maxSpeedDownloadKbps;

    @Column(name = "min_speed_upload_kbps")
    private Integer minSpeedUploadKbps;

    @Column(name = "max_speed_upload_kbps")
    private Integer maxSpeedUploadKbps;
}
