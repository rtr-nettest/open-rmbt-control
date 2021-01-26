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
public class SignalResponse {

    @JsonProperty(value = "result_url")
    private final String resultUrl;

    @JsonProperty(value = "client_remote_ip")
    private final String clientRemoteIp;

    @JsonProperty(value = "provider")
    private final String provider;

    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;
}
