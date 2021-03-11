package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class RadioSignalRequest {

    @JsonProperty(value = "bit_error_rate")
    private final Integer bitErrorRate;

    @JsonProperty(value = "cell_uuid")
    private final UUID cellUUID;

    @JsonProperty(value = "network_type_id")
    private final Integer networkTypeId;

    @JsonProperty(value = "signal")
    private final Integer signal;

    @JsonProperty(value = "time_ns_last")
    private final Long timeNsLast;

    @JsonProperty(value = "time_ns")
    private final Long timeNs;

    @JsonProperty(value = "wifi_link_speed")
    private final Integer wifiLinkSpeed;

    @JsonProperty(value = "lte_rsrp")
    private final Integer lteRSRP;

    @JsonProperty(value = "lte_rsrq")
    private final Integer lteRSRQ;

    @JsonProperty(value = "lte_rssnr")
    private final Integer lteRSSNR;

    @JsonProperty(value = "lte_cqi")
    private final Integer lteCQI;

    @JsonProperty(value = "timing_advance")
    private final Integer timingAdvance;
}
