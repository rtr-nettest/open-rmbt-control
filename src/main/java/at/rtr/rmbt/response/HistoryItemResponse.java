package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class HistoryItemResponse {

    @Schema(description = "Test UUID")
    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @Schema(description = "Open Test UUID")
    @JsonProperty(value = "open_test_uuid")
    private final String openTestUuid;

    @Schema(description = "Time of the test in millis")
    @JsonProperty(value = "time")
    private final Long time;

    @Schema(description = "Human readable timezone", example = "Europe/Bratislava")
    @JsonProperty(value = "timezone")
    private final String timezone;

    @Schema(description = "Human readable time of the test")
    @JsonProperty(value = "time_string")
    private final String timeString;

    @Schema(description = "Upload speed of test")
    @JsonProperty(value = "speed_upload")
    private final String speedUpload;

    @Schema(description = "Download speed of test")
    @JsonProperty(value = "speed_download")
    private final String speedDownload;

    @Schema(description = "Ping of test")
    @JsonProperty(value = "ping")
    private final String ping;

    @Schema(description = "Ping of test for old client")
    @JsonProperty(value = "ping_shortest")
    private final String pingShortest;

    @Schema(description = "Model of device")
    @JsonProperty(value = "model")
    private final String model;

    @Schema(description = "Network type")
    @JsonProperty(value = "network_type")
    private final String networkType;

    @Schema(description = "Loop uuid")
    @JsonProperty(value = "loop_uuid")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String loopUUID;

    @Schema(description = "Upload speed classificaton of test")
    @JsonProperty(value = "speed_upload_classification")
    private final Integer speedUploadClassification;

    @Schema(description = "Download speed classification of test")
    @JsonProperty(value = "speed_download_classification")
    private final Integer speedDownloadClassification;

    @Schema(description = "Ping classification of test")
    @JsonProperty(value = "ping_classification")
    private final Integer pingClassification;

    @Schema(description = "Ping classification of test for old client")
    @JsonProperty(value = "ping_shortest_classification")
    private final Integer pingShortestClassification;

    @Schema(description = "Signal strength of test")
    @JsonProperty(value = "signal_strength")
    private final Integer signalStrength;

    @Schema(description = "4G/LTE signal strength")
    @JsonProperty(value = "lte_rsrp")
    private final Integer lteRSRP;

    @Schema(description = "Signal classification of test")
    @JsonProperty(value = "signal_classification")
    private final Integer signalClassification;

    @JsonProperty(value = "status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String status;
}
