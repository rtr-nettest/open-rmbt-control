package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsBodyRequest {

    @ApiModelProperty(value = "Default URL of terms and conditions for app", example = "https://www.netztest.at/en/tc.html")
    @JsonProperty(value = "tc_url")
    private final String tcUrl;

    @ApiModelProperty(value = "Terms and conditions version number" , example = "6")
    @JsonProperty(value = "tc_version")
    private final String tcVersion;

    @ApiModelProperty(value = "URL of terms and conditions for Android" , example = "https://www.netztest.at/en/tc_android.html")
    @JsonProperty(value = "tc_url_android")
    private final String tcUrlAndroid;

    @ApiModelProperty(value = "URL of terms and conditions for the usage of NDT tests for Android" , example = "https://www.netztest.at/en/tc_android.html")
    @JsonProperty(value = "tc_ndt_url_android")
    private final String tcNdtUrlAndroid;

    @ApiModelProperty(value = "Terms and conditions version number for Android" , example = "6")
    @JsonProperty(value = "tc_version_android")
    private final String tcVersionAndroid;

    @ApiModelProperty(value = "URL of terms and conditions for IOS" , example = "https://www.netztest.at/en/tc_ios.html")
    @JsonProperty(value = "tc_url_ios")
    private final String tcUrlIOS;

    @ApiModelProperty(value = "Terms and conditions version number for IOS" , example = "6")
    @JsonProperty(value = "tc_version_ios")
    private final String tcVersionIOS;

    @ApiModelProperty(value = "Base URL for sharing results" , example = "https://dev.netztest.at/share/")
    @JsonProperty(value = "url_share")
    private final String urlShare;

    @ApiModelProperty(value = "Base URL for checking IPv6" , example = "https://devv6.netztest.at/RMBTControlServer/ip")
    @JsonProperty(value = "url_ipv6_check")
    private final String urlIPV6Check;

    @ApiModelProperty(value = "Base URL for control server to for IPv4 networks only (force IPv4)" , example = "devv4.netztest.at")
    @JsonProperty(value = "control_ipv4_only")
    private final String controlIPV4Only;

    @ApiModelProperty(value = "Base URL for control server to use with opendata result" , example = "https://dev.netztest.at/en/Opentest?")
    @JsonProperty(value = "url_open_data_prefix")
    private final String openDataPrefix;

    @ApiModelProperty(value = "Base URL for map server" , example = "https://dev.netztest.at/RMBTMapServer")
    @JsonProperty(value = "url_map_server")
    private final String urlMapServer;

    @ApiModelProperty(value = "Base URL for checking IPv4" , example = "https://devv4.netztest.at/RMBTControlServer/ip")
    @JsonProperty(value = "url_ipv4_check")
    private final String urlIPV4Check;

    @ApiModelProperty(value = "Base URL for control server to for IPv6 networks only (force IPv6)" , example = "devv6.netztest.at")
    @JsonProperty(value = "control_ipv6_only")
    private final String controlIPV6Only;

    @ApiModelProperty(value = "URL for statistic" , example = "https://dev.netztest.at/en/Statistik#noMMenu")
    @JsonProperty(value = "url_statistics")
    private final String statistics;

    @ApiModelProperty(value = "Port of the map server" , example = "443")
    @JsonProperty(value = "port_map_server")
    private final Long port;

    @ApiModelProperty(value = "Hostname of the map server" , example = "dev.netztest.at")
    @JsonProperty(value = "host_map_server")
    private final String host;

    @ApiModelProperty(value = "True if use ssl" , example = "true")
    @JsonProperty(value = "ssl_map_server")
    private final Boolean ssl;
}