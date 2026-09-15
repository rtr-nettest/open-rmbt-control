package at.rtr.rmbt.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration for the scheduled download of Apple's iCloud Private Relay egress IP-range list
 * (see {@code at.rtr.rmbt.service.impl.PrivateRelayServiceImpl}). All values can be overridden in
 * {@code application.yml} (or via the deployment context) under the {@code icloud-egress} prefix.
 */
@Getter
@Setter
@ConfigurationProperties("icloud-egress")
public class IcloudEgressProperties {

    /** Master switch; set to {@code false} to disable the scheduled download entirely. */
    private boolean enabled = true;

    /** Source CSV (no header): prefix, proxy_country, proxy_region, proxy_city. */
    private String url = "https://mask-api.icloud.com/egress-ip-ranges.csv";

    /** Delay after application start before the first download runs. */
    private Duration initialDelay = Duration.ofSeconds(20);

    /** Interval between downloads; the requirement is a refresh every 24 hours. */
    private Duration fixedDelay = Duration.ofHours(24);

    /** Timeout for establishing the TCP connection to the CSV host. */
    private Duration connectTimeout = Duration.ofSeconds(15);

    /** Timeout for the whole HTTP request/response. */
    private Duration requestTimeout = Duration.ofSeconds(60);
}
