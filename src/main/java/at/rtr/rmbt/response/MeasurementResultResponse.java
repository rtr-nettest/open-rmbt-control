package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MeasurementResultResponse {

    @JsonProperty(value = "download_kbit")
    private final Integer downloadKBit;

    @JsonProperty(value = "download_classification")
    private final Integer downloadClassification;

    @JsonProperty(value = "upload_kbit")
    private final Integer uploadKBit;

    @JsonProperty(value = "upload_classification")
    private final Integer uploadClassification;

    @JsonProperty(value = "ping_ms")
    private final double pingMs;

    @JsonProperty(value = "ping_classification")
    private final Integer pingClassification;

    @JsonProperty(value = "signal_strength")
    private final Integer signalStrength;

    @JsonProperty(value = "signal_classification")
    private final Integer signalClassification;

    @JsonProperty(value = "lte_rsrp")
    private final Integer lteRSRP;

}
