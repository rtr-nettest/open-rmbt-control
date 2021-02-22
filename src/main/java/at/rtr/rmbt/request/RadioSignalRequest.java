package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class RadioSignalRequest {

    @JsonProperty(value = "bit_error_rate")
    private final Long bitErrorRate;

    @JsonProperty(value = "cell_uuid")
    private final UUID cellUUID;

    @JsonProperty(value = "network_type_id")
    private final Long networkTypeId;

    @JsonProperty(value = "signal")
    private final Long signal;

    @JsonProperty(value = "time_ns_last")
    private final Long timeNsLast;

    @JsonProperty(value = "time_ns")
    private final Long timeNs;

    @JsonProperty(value = "wifi_link_speed")
    private final Long wifiLinkSpeed;

    @JsonProperty(value = "lte_rsrp")
    private final Long lteRSRP;

    @JsonProperty(value = "lte_rsrq")
    private final Long lteRSRQ;

    @JsonProperty(value = "lte_rssnr")
    private final Long lteRSSNR;

    @JsonProperty(value = "lte_cqi")
    private final Long lteCQI;

    @JsonProperty(value = "timing_advance")
    private final Long timingAdvance;
}
