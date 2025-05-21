package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class CoverageSettingsResponse {

    @JsonProperty(value = "client_remote_ip")
    private final String clientRemoteIp;

    @JsonProperty(value = "provider")
    private final String provider;

    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @JsonProperty(value = "ping_token")
    private final String pingToken;

    @JsonProperty(value = "ping_host")
    private final String pingHost;

    @JsonProperty(value = "ping_port")
    private final String pingPort;

    @JsonProperty(value = "ip_version")
    private final Integer ipVersion;
}
