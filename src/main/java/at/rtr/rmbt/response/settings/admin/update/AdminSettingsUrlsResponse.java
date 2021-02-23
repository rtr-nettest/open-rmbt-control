package at.rtr.rmbt.response.settings.admin.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsUrlsResponse {

    private final String urlShare;

    private final String controlIpV6Only;

    private final String controlIpV4Only;

    private final String openDataPrefix;

    private final String urlMapServer;

    private final String urlIpV6Check;

    private final String urlIpV4Check;

    private final String statistics;
}
