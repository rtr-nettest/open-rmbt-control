package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(notes = "Test UUID")
    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @ApiModelProperty(notes = "Time of the test in millis")
    @JsonProperty(value = "time")
    private final Long time;

    @ApiModelProperty(notes = "Human readable timezone", example = "Europe/Bratislava")
    @JsonProperty(value = "timezone")
    private final String timezone;

    @ApiModelProperty(notes = "Human readable time of the test")
    @JsonProperty(value = "time_string")
    private final String timeString;

    @ApiModelProperty(notes = "Upload speed of test")
    @JsonProperty(value = "speed_upload")
    private final String speedUpload;

    @ApiModelProperty(notes = "Download speed of test")
    @JsonProperty(value = "speed_download")
    private final String speedDownload;

    @ApiModelProperty(notes = "Ping of test")
    @JsonProperty(value = "ping")
    private final String ping;

    @ApiModelProperty(notes = "Ping of test for old client")
    @JsonProperty(value = "ping_shortest")
    private final String pingShortest;

    @ApiModelProperty(notes = "Model of device")
    @JsonProperty(value = "model")
    private final String model;

    @ApiModelProperty(notes = "Network type")
    @JsonProperty(value = "network_type")
    private final String networkType;

    @ApiModelProperty(notes = "Loop uuid")
    @JsonProperty(value = "loop_uuid")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String loopUUID;

    @ApiModelProperty(notes = "Upload speed classificaton of test")
    @JsonProperty(value = "speed_upload_classification")
    private final Integer speedUploadClassification;

    @ApiModelProperty(notes = "Download speed classification of test")
    @JsonProperty(value = "speed_download_classification")
    private final Integer speedDownloadClassification;

    @ApiModelProperty(notes = "Ping classification of test")
    @JsonProperty(value = "ping_classification")
    private final Integer pingClassification;

    @ApiModelProperty(notes = "Ping classification of test for old client")
    @JsonProperty(value = "ping_shortest_classification")
    private final Integer pingShortestClassification;

    @ApiModelProperty(notes = "Signal strength of test")
    @JsonProperty(value = "signal_strength")
    private final Integer signalStrength;

    @ApiModelProperty(notes = "4G/LTE signal strength")
    @JsonProperty(value = "lte_rsrp")
    private final Integer lteRSRP;

    @ApiModelProperty(notes = "Signal classification of test")
    @JsonProperty(value = "signal_classification")
    private final Integer signalClassification;
}
