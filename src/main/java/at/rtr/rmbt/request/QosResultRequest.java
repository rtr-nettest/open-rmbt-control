package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class QosResultRequest {

    @Schema(example = "c373f294-f332-4f1a-999e-a87a12523f4b", description = "Test token obtained via /testRequest")
    @JsonProperty("test_token")
    private String testToken;

    @Schema(example = "en", description = "Language code of the client language")
    @JsonProperty("client_language")
    private String clientLanguage;

    @Schema(example = "3", description = "Version of the RMBT client")
    @JsonProperty("client_version")
    private String clientVersion;

    @Schema(description = "Name of the client")
    @JsonProperty("client_name")
    private String clientName;

    @Schema(description = "Used by Android")
    @JsonProperty("client_uuid")
    private String androidClientUUID;

    @Schema(description = "Used by iOS")
    @JsonProperty("uuid")
    private String iosClientUUID;

    @Schema(description = "Array with particular qos results")
    @JsonProperty("qos_result")
    private List<QosSendTestResultItem> qosResults;
}
