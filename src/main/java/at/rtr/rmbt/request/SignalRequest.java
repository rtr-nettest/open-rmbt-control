package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignalRequest {

    @Schema(description = "Network type id", example = "99")
    @JsonProperty(value = "network_type_id")
    private final Integer networkTypeId;

    @Schema(description = "Signal strength", example = "-92")
    @JsonProperty(value = "signal_strength")
    private final Integer signalStrength;

    @Schema(description = "GSM bit error rate", example = "4")
    @JsonProperty(value = "gsm_bit_error_rate")
    private final Integer gsmBitErrorRate;

    @Schema(description = "Wifi link speed", example = "43")
    @JsonProperty(value = "wifi_link_speed")
    private final Integer wifiLinkSpeed;

    @Schema(description = "Received Signal Strength Indicator dBm", example = "-57")
    @JsonProperty(value = "wifi_rssi")
    private final Integer wifiRSSI;

    @Schema(description = "Reference Signal Received Power dBm", example = "-72")
    @JsonProperty(value = "lte_rsrp")
    private final Integer lteRSRP;

    @Schema(description = "Reference Signal Received Quality dB", example = "-9")
    @JsonProperty(value = "lte_rsrq")
    private final Integer lteRSRQ;

    @Schema(description = "Reference Signal Signal to Noise Ratio ", example = "-9")
    @JsonProperty(value = "lte_rssnr")
    private final Integer lteRSSNR;

    @Schema(description = "Channel Quality Indicator", example = "1")
    @JsonProperty(value = "lte_cqi")
    private final Integer lteCQI;

    @Schema(description = "Current time_ns of the client at the time of this submission.", example = "1112516562")
    @JsonProperty(value = "time_ns")
    private final Long timeNs;

    @Schema(description = "Time instant of client", example = "168588655699338")
    @JsonProperty(value = "time")
    private final Long time;
}
