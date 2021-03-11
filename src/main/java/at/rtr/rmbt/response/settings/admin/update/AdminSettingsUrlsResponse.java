package at.rtr.rmbt.response.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsUrlsResponse {

    @JsonProperty(value = "urlShare")
    private final String urlShare;

    @JsonProperty(value = "controlIpV6Only")
    private final String controlIpV6Only;

    @JsonProperty(value = "controlIpV4Only")
    private final String controlIpV4Only;

    @JsonProperty(value = "openDataPrefix")
    private final String openDataPrefix;

    @JsonProperty(value = "urlMapServer")
    private final String urlMapServer;

    @JsonProperty(value = "urlIpV6Check")
    private final String urlIpV6Check;

    @JsonProperty(value = "urlIpV4Check")
    private final String urlIpV4Check;

    @JsonProperty(value = "statistics")
    private final String statistics;
}
