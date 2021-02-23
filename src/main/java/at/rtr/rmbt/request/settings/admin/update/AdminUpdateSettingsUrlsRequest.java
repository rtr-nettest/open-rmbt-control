package at.rtr.rmbt.request.settings.admin.update;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsUrlsRequest {

    @ApiModelProperty(value = "Base URL for sharing results", example = "https://dev.netztest.at/share/")
    private final String urlShare;

    @ApiModelProperty(value = "Base URL for checking IPv6", example = "https://devv6.netztest.at/RMBTControlServer/ip")
    private final String urlIpV6Check;

    @ApiModelProperty(value = "Base URL for checking IPv4", example = "https://devv4.netztest.at/RMBTControlServer/ip")
    private final String urlIpV4Check;

    @ApiModelProperty(value = "Base URL for control server to for IPv4 networks only (force IPv4)", example = "devv4.netztest.at")
    private final String controlIpV4Only;

    @ApiModelProperty(value = "Base URL for control server to for IPv6 networks only (force IPv6)", example = "devv6.netztest.at")
    private final String controlIpV6Only;

    @ApiModelProperty(value = "Base URL for control server to use with opendata result", example = "https://dev.netztest.at/en/Opentest?")
    private final String openDataPrefix;

    @ApiModelProperty(value = "Base URL for map server", example = "https://dev.netztest.at/RMBTMapServer")
    private final String urlMapServer;

    @ApiModelProperty(value = "URL for statistic", example = "https://dev.netztest.at/en/Statistik#noMMenu")
    private final String statistics;
}
