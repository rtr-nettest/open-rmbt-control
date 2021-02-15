package at.rtr.rmbt.model;


import at.rtr.rmbt.model.enums.*;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@ToString
@Entity
@Table(name = "test")
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Test {
    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_seq")
    @SequenceGenerator(name = "test_seq", sequenceName = "test_uid_seq", allocationSize = 1)
    private Long uid;

    @Column(name = "uuid")
    private UUID uuid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private RtrClient client;

    @Column(name = "client_version")
    private String clientVersion;

    @Column(name = "client_name")
    private ServerType clientName;

    @Column(name = "client_language")
    private String clientLanguage;

    @Column(name = "token")
    private String token;

    @Column(name = "server_id")
    private Long serverId;

    @Column(name = "port")
    private Integer serverPort;

    @Column(name = "use_ssl")
    private Boolean useSsl;

    @CreationTimestamp
    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "speed_upload")
    private Integer uploadSpeed;

    @Column(name = "speed_download")
    private Integer downloadSpeed;

    @Column(name = "ping_shortest")
    private Long shortestPing;

    @Column(name = "encryption")
    private String encryption;

    @Column(name = "client_public_ip")
    private String clientPublicIp;

    @Column(name = "plattform")
    private TestPlatform platform;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "api_level")
    private String apiLevel;

    @Column(name = "device")
    private String device;

    @Column(name = "model")
    private String model;

    @Column(name = "product")
    private String product;

    @Column(name = "phone_type")
    private Integer phoneType;

    @Column(name = "data_state")
    private Integer dataState;

    @Column(name = "network_country")
    private String networkCountry;

    @Column(name = "network_operator")
    private String networkOperator;

    @Column(name = "network_operator_name")
    private String networkOperatorName;

    @Column(name = "network_sim_country")
    private String networkSimCountry;

    @Column(name = "network_sim_operator")
    private String networkSimOperator;

    @Column(name = "network_sim_operator_name")
    private String networkSimOperatorName;

    @Column(name = "wifi_ssid")
    private String wifiSsid;

    @Column(name = "wifi_bssid")
    private String wifiBssid;

    @Column(name = "wifi_network_id")
    private String wifiNetworkId;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "num_threads")
    private Integer numberOfThreads;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TestStatus status;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "bytes_download")
    private Long bytesDownload;

    @Column(name = "bytes_upload")
    private Long bytesUpload;

    @Column(name = "nsec_download")
    private Long nsecDownload;

    @Column(name = "nsec_upload")
    private Long nsecUpload;

    @Column(name = "server_ip")
    private String serverIp;

    @Column(name = "client_software_version")
    private String clientSoftwareVersion;

    @Column(name = "geo_lat")
    private Double latitude;

    @Column(name = "geo_long")
    private Double longitude;

    @Column(name = "network_type")
    private Integer networkType;

    @Column(name = "location")
    private Geometry location;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "software_revision")
    private String softwareRevision;

    @Column(name = "client_test_counter")
    private Long clientTestCounter;

    @Column(name = "nat_type")
    private String natType;

    @Column(name = "client_previous_test_status")
    @Enumerated(EnumType.STRING)
    private TestStatus clientPreviousTestStatus;

    @Column(name = "public_ip_asn")
    private Long publicIpAsn;

    @Column(name = "speed_upload_log")
    private Double speedUploadLog;

    @Column(name = "speed_download_log")
    private Double speedDownloadLog;

    @Column(name = "total_bytes_download")
    private Long totalBytesDownload;

    @Column(name = "total_bytes_upload")
    private Long totalBytesUpload;

    @Column(name = "wifi_link_speed")
    private Integer wifiLinkSpeed;

    @Column(name = "public_ip_rdns")
    private String publicIpRdns;

    @Column(name = "public_ip_as_name")
    private String publicIpAsName;

    @Column(name = "testSlot")
    private Integer testSlot;

    @Column(name = "provider_id")
    private Integer providerId;

    @Column(name = "network_is_roaming")
    private Boolean networkIsRoaming;

    @Column(name = "ping_shortest_log")
    private Double pingShortestLog;

    @Column(name = "run_ndt")
    private Boolean runNdt;

    @Column(name = "num_threads_requested")
    private Integer numberOfThreadsRequested;

    @Column(name = "client_public_ip_anonymized")
    private String clientPublicIpAnonymized;

    @Column(name = "zip_code")
    private Integer zipCode;

    @Column(name = "geo_provider")
    private String geoProvider;

    @Column(name = "geo_accuracy")
    private Double geoAccuracy;

    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "comment")
    private String comment;

    @Column(name = "open_uuid")
    private UUID openUuid;

    @Column(name = "client_time")
    private ZonedDateTime clientTime;

    @Column(name = "zip_code_geo")
    private Integer zipCodeGeo;

    @Column(name = "mobile_provider_id")
    private Integer mobileProviderId;

    @Column(name = "roaming_type")
    private Integer roamingType;

    @Column(name = "open_test_uuid")
    private UUID openTestUuid;

    @Column(name = "country_asn")
    private String countryAsn;

    @Column(name = "country_location")
    private String countryLocation;

    @Column(name = "test_if_bytes_download")
    private Long testIfBytesDownload;

    @Column(name = "test_if_bytes_upload")
    private Long testIfBytesUpload;

    @Column(name = "testdl_if_bytes_download")
    private Long testdlIfBytesDownload;

    @Column(name = "testdl_if_bytes_upload")
    private Long testdlIfBytesUpload;

    @Column(name = "testul_if_bytes_download")
    private Long testulIfBytesDownload;

    @Column(name = "testul_if_bytes_upload")
    private Long testulIfBytesUpload;

    @Column(name = "implausible")
    @Builder.Default
    private Boolean implausible = false;

    @Column(name = "country_geoip")
    private String countryGeoip;

    @Column(name = "location_max_distance")
    private Integer locationMaxDistance;

    @Column(name = "location_max_distance_gps")
    private Integer locationMaxDistanceGps;

    @Column(name = "network_group_name")
    private NetworkGroupName networkGroupName;

    @Column(name = "network_group_type")
    @Enumerated(EnumType.STRING)
    private NetworkGroupType networkGroupType;

    @Column(name = "time_dl_ns")
    private Long downloadTimeNanoSeconds;

    @Column(name = "time_ul_ns")
    private Long uploadTimeNanoSeconds;

    @Column(name = "num_threads_ul")
    private Integer numberOfThreadsUpload;

    @Column(name = "timestamp")
    private ZonedDateTime timestamp;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(name = "lte_rsrp")
    private Integer lteRsrp;

    @Column(name = "lte_rsrq")
    private Integer lteRsrq;

    @Column(name = "mobile_network_id")
    private Integer mobileNetworkId;

    @Column(name = "mobile_sim_id")
    private Integer mobileSimId;

    @Column(name = "dist_prev")
    private Double distPrev;

    @Column(name = "speed_prev")
    private Double speedPrev;

    @Column(name = "tag")
    private String tag;

    @Column(name = "ping_median")
    private Long pingMedian;

    @Column(name = "ping_median_log")
    private Double pingMedianLog;

    @Column(name = "source_ip_anonymized")
    private String sourceIpAnonymized;

    @Column(name = "client_ip_local")
    private String clientIpLocal;

    @Column(name = "client_ip_local_anonymized")
    private String clientIpLocalAnonymized;

    @Column(name = "client_ip_local_type")
    private String clientIpLocalType;

    @Column(name = "hidden_code")
    private String hiddenCode;

    @Column(name = "origin")
    private UUID origin;

    @Column(name = "developer_code")
    private String developerCode;

    @Column(name = "dual_sim")
    private Boolean dualSim;

    @Column(name = "gkz_obsolete")
    private Integer gkzObsolete;

    @Column(name = "android_permissions", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private List<AndroidPermission> androidPermissions;

    @Column(name = "dual_sim_detection_method")
    private String dualSimDetectionMethod;

    @Column(name = "pinned")
    @Builder.Default
    private Boolean pinned = false;

    @Column(name = "similar_test_uid")
    private Long similarTestUid;

    @Column(name = "user_server_selection")
    private Boolean userServerSelection;

    @Column(name = "radio_band")
    private Integer radioBand;

    @Column(name = "sim_count")
    private Integer simCount;

    @Column(name = "time_qos_ns")
    private Long timeQosNs;

    @Column(name = "test_nsec_qos")
    private Long testNsecQos;

    @Column(name = "channel_number")
    private Integer channelNumber;

    @Column(name = "gkz_bev_obsolete")
    private Integer gkzBevObsolete;

    @Column(name = "gkz_sa_obsolete")
    private Integer gkzSaObsolete;

    @Column(name = "kg_nr_bev")
    private Integer kgNrBev;

    @Column(name = "land_cover_obsolete")
    private Integer landCoverObsolete;

    @Column(name = "cell_location_id")
    private Integer cellLocationId;

    @Column(name = "cell_area_code")
    private Integer cellAreaCode;

    @Column(name = "link_distance_obsolete")
    private Integer linkDistanceObsolete;

    @Column(name = "link_id_obsolete")
    private Integer linkIdObsolete;

    @Column(name = "settlementTypeObsolete")
    private Integer settlementTypeObsolete;

    @Column(name = "linkNameObsolete")
    private String linkNameObsolete;

    @Column(name = "frc_obsolete")
    private Integer frcObsolete;

    @Column(name = "edge_id_obsolete")
    private Integer edgeIdObsolete;

    @Column(name = "geo_location_uuid")
    private UUID geoLocationUuid;

    @Column(name = "last_client_status")
    @Enumerated(EnumType.STRING)
    private ClientStatus lastClientStatus;

    @Column(name = "last_qos_status")
    @Enumerated(EnumType.STRING)
    private QosStatus lastQosStatus;

    @Column(name = "test_error_cause")
    private String testErrorCause;

    @Column(name = "last_sequence_number")
    private Integer lastSequenceNumber;

    @Column(name = "submission_retry_count")
    private Integer submissionRetryCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "uuid",
            referencedColumnName = "test_uuid",
            insertable = false, nullable = false, updatable = false
    )
    private LoopModeSettings loopModeSettings;

    @PrePersist
    protected void preInsert() {
        this.time = ZonedDateTime.now();
    }
}
