package at.rtr.rmbt.request.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsUrlsRequest {

    @Schema(description = "Base URL for sharing results", example = "https://dev.netztest.at/share/")
    @JsonProperty(value = "urlShare")
    private final String urlShare;

    @Schema(description = "Base URL for checking IPv6", example = "https://devv6.netztest.at/RMBTControlServer/ip")
    @JsonProperty(value = "urlIpV6Check")
    private final String urlIpV6Check;

    @Schema(description = "Base URL for checking IPv4", example = "https://devv4.netztest.at/RMBTControlServer/ip")
    @JsonProperty(value = "urlIpV4Check")
    private final String urlIpV4Check;

    @Schema(description = "Base URL for control server to for IPv4 networks only (force IPv4)", example = "devv4.netztest.at")
    @JsonProperty(value = "controlIpV4Only")
    private final String controlIpV4Only;

    @Schema(description = "Base URL for control server to for IPv6 networks only (force IPv6)", example = "devv6.netztest.at")
    @JsonProperty(value = "controlIpV6Only")
    private final String controlIpV6Only;

    @Schema(description = "Base URL for control server to use with opendata result", example = "https://dev.netztest.at/en/Opentest?")
    @JsonProperty(value = "openDataPrefix")
    private final String openDataPrefix;

    @Schema(description = "Base URL for map server", example = "https://dev.netztest.at/RMBTMapServer")
    @JsonProperty(value = "urlMapServer")
    private final String urlMapServer;

    @Schema(description = "URL for statistic", example = "https://dev.netztest.at/en/Statistik#noMMenu")
    @JsonProperty(value = "statistics")
    private final String statistics;
}
