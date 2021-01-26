package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsBodyRequest {

    @JsonProperty(value = "tc_url")
    private final String tcUrl;

    @JsonProperty(value = "tc_version")
    private final String tcVersion;

    @JsonProperty(value = "tc_url_android")
    private final String tcUrlAndroid;

    @JsonProperty(value = "tc_ndt_url_android")
    private final String tcNdtUrlAndroid;

    @JsonProperty(value = "tc_version_android")
    private final String tcVersionAndroid;

    @JsonProperty(value = "tc_url_ios")
    private final String tcUrlIOS;

    @JsonProperty(value = "tc_ndt_url_ios")
    private final String tcNdtUrlIOS;

    @JsonProperty(value = "tc_version_ios")
    private final String tcVersionIOS;

    @JsonProperty(value = "url_share")
    private final String urlShare;

    @JsonProperty(value = "url_ipv6_check")
    private final String urlIPV6Check;

    @JsonProperty(value = "control_ipv4_only")
    private final String controlIPV4Only;

    @JsonProperty(value = "url_open_data_prefix")
    private final String openDataPrefix;

    @JsonProperty(value = "url_map_server")
    private final String urlMapServer;

    @JsonProperty(value = "url_ipv4_check")
    private final String urlIPV4Check;

    @JsonProperty(value = "control_ipv6_only")
    private final String controlIPV6Only;

    @JsonProperty(value = "url_statistics")
    private final String statistics;

    @JsonProperty(value = "port_map_server")
    private final Long port;

    @JsonProperty(value = "host_map_server")
    private final String host;

    @JsonProperty(value = "ssl_map_server")
    private final Boolean ssl;
}
