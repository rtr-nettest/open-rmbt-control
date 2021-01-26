package at.rtr.rmbt.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import at.rtr.rmbt.model.enums.ServerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "Configuration for basic test")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TestSettingsResponse {
    @JsonProperty("client_remote_ip")
    @ApiModelProperty(notes = "Public IP address", example = "91.148.44.167")
    private String clientRemoteIp;

    @JsonProperty("test_uuid")
    @ApiModelProperty(notes = "UUID of the test", example = "8c8946bb-e251-42f8-b0d1-43f972c2e216")
    private String testUuid;

    @JsonProperty("result_url")
    @ApiModelProperty(notes = "URL to send basic result to", example = "https://dev.netztest.at/RMBTControlServer/result")
    private String resultUrl;

    @JsonProperty("result_qos_url")
    @ApiModelProperty(notes = "URL to send QOS result to", example = "https://dev.netztest.at/RMBTControlServer/resultQoS")
    private String resultQosUrl;

    @JsonProperty("test_duration")
    @ApiModelProperty(notes = "Duration of each main part of the test in seconds [download, upload phase]", example = "7")
    private Integer testDuration;

    @JsonProperty("test_server_name")
    @ApiModelProperty(notes = "Name of the measurement server", example = "RTR dev rmbt (Vienna)")
    private String testServerName;

    @JsonProperty("test_wait")
    @ApiModelProperty(notes = "Waiting time in seconds", example = "0")
    private Integer testWait;

    @JsonProperty("test_server_address")
    @ApiModelProperty(notes = "URL of the test server", example = "devv4-rmbt.netztest.at")
    private String testServerAddress;

    @JsonProperty("test_numthreads")
    @ApiModelProperty(notes = "Number of threads to be used for test execution", example = "5")
    private Integer testNumberOfThreads;

    @JsonProperty("test_server_port")
    @ApiModelProperty(notes = "Server port of the test server", example = "443")
    private Integer testServerPort;

    @JsonProperty("open_test_uuid")
    @ApiModelProperty(notes = "Open test UUID to be used for open data", example = "O53d69299-0206-4732-a623-6b0c2fec306d")
    private String openTestUuid;

    @JsonProperty("test_server_type")
    @ApiModelProperty(notes = "Type of the measurement server", example = "RMBT")
    private ServerType testServerType;

    @JsonProperty("test_server_encryption")
    @ApiModelProperty(notes = "True if test server uses encryption", example = "true")
    private Boolean testServerEncryption;

    @JsonProperty("test_token")
    @ApiModelProperty(notes = "Test token used to autheticate on measurement server", example = "53d69299-0206-4732-a623-6b0c2fec306d_1572526785_oFAdP8+Cw9TqvJOgNc5ABOQRxss=")
    private String testToken;

    @JsonProperty("test_numpings")
    @ApiModelProperty(notes = "Number of pings executed during the test", example = "10")
    private Integer testNumberOfPings;

    @JsonProperty("test_id")
    @ApiModelProperty(notes = "Id of the test", example = "8772493")
    private Long testId;

    @JsonProperty("loop_uuid")
    @ApiModelProperty(notes = "Generated loop_uuid if it is loop measurement", example = "53d69299-0206-4732-a623-6b0c2fec306d")
    private String loopUuid;

    @JsonProperty("provider")
    @ApiModelProperty(notes = "Name of the internet provider (meaningful mainly for fixed providers - \"SANET\", \"SWAN\", ...)", example = "02")
    private String provider;

    @JsonProperty("error_flags")
    @ApiModelProperty(notes = "Error flags")
    private ErrorResponse errorFlags;

    @JsonProperty("error")
    @ApiModelProperty(notes = "Error list", example = "[\"First error\", \"Second error\"]")
    private ErrorResponse errorList;
}
