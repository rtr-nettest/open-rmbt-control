package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class QosResultRequest {

    @ApiModelProperty(example = "c373f294-f332-4f1a-999e-a87a12523f4b", notes = "Test token obtained via /testRequest")
    @JsonProperty("test_token")
    private String testToken;

    @ApiModelProperty(example = "en", notes = "Language code of the client language")
    @JsonProperty("client_language")
    private String clientLanguage;

    @ApiModelProperty(example = "3", notes = "Version of the RMBT client")
    @JsonProperty("client_version")
    private String clientVersion;

    @ApiModelProperty(notes = "Name of the client")
    @JsonProperty("client_name")
    private String clientName;

    @ApiModelProperty(notes = "Used by Android")
    @JsonProperty("client_uuid")
    private String androidClientUUID;

    @ApiModelProperty(notes = "Used by iOS")
    @JsonProperty("uuid")
    private String iosClientUUID;

    @ApiModelProperty(notes = "Array with particular qos results")
    @JsonProperty("qos_result")
    private List<QosSendTestResultItem> qosResults;
}
