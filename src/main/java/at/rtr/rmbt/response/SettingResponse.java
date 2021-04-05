package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class SettingResponse {

    @ApiModelProperty(notes = "Term and conditions settings")
    @JsonProperty(value = "terms_and_conditions")
    private final TermAndConditionsResponse termAndConditionsResponse;

    @ApiModelProperty(notes = "Urls settings")
    @JsonProperty(value = "urls")
    private final UrlsResponse urls;

    @ApiModelProperty(notes = "QoS test type desctription")
    @JsonProperty(value = "qostesttype_desc")
    private final List<QosTestTypeDescResponse> qosTestTypeDescResponse;

    @ApiModelProperty(notes = "Version of control server")
    @JsonProperty(value = "versions")
    private final VersionResponse versions;

    @ApiModelProperty(notes = "Servers")
    @JsonProperty(value = "servers")
    private final List<TestServerResponseForSettings> servers;

    @ApiModelProperty(notes = "Servers WS")
    @JsonProperty(value = "servers_ws")
    private final List<TestServerResponseForSettings> serverWSResponseList;

    @ApiModelProperty(notes = "Servers QoS")
    @JsonProperty(value = "servers_qos")
    private final List<TestServerResponseForSettings> serverQosResponseList;

    @ApiModelProperty(notes = "Client history")
    @JsonProperty(value = "history")
    private final SettingsHistoryResponse history;

    @ApiModelProperty(notes = "Client UUID")
    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @ApiModelProperty(notes = "Map server settings")
    @JsonProperty(value = "map_server")
    private final MapServerResponse mapServerResponse;
}
