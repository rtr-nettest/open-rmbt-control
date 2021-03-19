package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NetworkInfoResponse {

    @JsonProperty(value = "network_type_label")
    private final String networkTypeLabel;

    @JsonProperty(value = "provider_name")
    private final String providerName;

    @JsonProperty(value = "wifi_ssid")
    private final String wifiSSID;

    @JsonProperty(value = "roaming_type_label")
    private final String roamingTypeLabel;
}
