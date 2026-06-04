package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class SignalMeasurementSettingsResponse {

    @Schema(description = "Public IP of client", example = "1.2.3.4")
    @JsonProperty(value = "client_remote_ip")
    private final String clientRemoteIp;

    @Schema(description = "Provider name", example = "Some ISP")
    @JsonProperty(value = "provider")
    private final String provider;

    @Schema(description = "Test UUID", example = "30114752-141e-46f1-b574-fc421e932d39")
    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @Schema(description = "Token for ping measurement", example = "aWKUzZEyb7t34wCPfV0ATA==")
    @JsonProperty(value = "ping_token")
    private final String pingToken;

    @Schema(description = "Hostname for ping measurement", example = "udp-ping.example.com")
    @JsonProperty(value = "ping_host")
    private final String pingHost;

    @Schema(description = "UDP Port for ping measurement", example = "444")
    @JsonProperty(value = "ping_port")
    private final String pingPort;

    @Schema(description = "IP version (4 for IPv4, 6 for IPv6) of request", example = "6")
    @JsonProperty(value = "ip_version")
    private final Integer ipVersion;

    @Schema(description = "Maximum duration of single session in seconds", example = "3600")
    @JsonProperty(value = "max_coverage_session_seconds")
    private final Long maxSignalMeasurementSessionSeconds;

    @Schema(description = "Maximum total duration of signal measurement in seconds", example = "86400")
    @JsonProperty(value = "max_coverage_measurement_seconds")
    private final Long maxSignalMeasurementSeconds;

    @Schema(description = "UUID of sequence, generated if not existing", example = "dfa91a7a-fa8f-4bcd-86f5-e0b906162c4e")
    @JsonProperty(value = "loop_uuid")
    private final UUID loopUUID;

    @Schema(description = "Counter in loop (1st, 2nd, 3rd test)", example = "42")
    @JsonProperty(value = "loop_test_counter")
    private final Integer loopTestCounter;

    /**
     * Legacy camelCase alias of {@code max_coverage_session_seconds}. Older clients read this
     * key, so it is emitted in addition to the snake_case variant to let the client base migrate
     * gradually. Remove once all clients consume {@code max_coverage_session_seconds}.
     *
     * @deprecated use {@code max_coverage_session_seconds} instead.
     */
    @Deprecated
    @Schema(deprecated = true,
            description = "Deprecated camelCase alias of max_coverage_session_seconds; will be removed once clients migrate",
            example = "3600")
    @JsonProperty(value = "maxCoverageSessionSeconds", access = JsonProperty.Access.READ_ONLY)
    public Long getMaxCoverageSessionSecondsLegacy() {
        return maxSignalMeasurementSessionSeconds;
    }

}
