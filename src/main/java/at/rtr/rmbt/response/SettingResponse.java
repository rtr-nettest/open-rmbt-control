package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class SettingResponse {

    @Schema(description = "Term and conditions settings")
    @JsonProperty(value = "terms_and_conditions")
    private final TermAndConditionsResponse termAndConditionsResponse;

    @Schema(description = "Urls settings")
    @JsonProperty(value = "urls")
    private final UrlsResponse urls;

    @Schema(description = "QoS test type desctription")
    @JsonProperty(value = "qostesttype_desc")
    private final List<QosTestTypeDescResponse> qosTestTypeDescResponse;

    @Schema(description = "Version of control server")
    @JsonProperty(value = "versions")
    private final VersionResponse versions;

    @Schema(description = "Servers")
    @JsonProperty(value = "servers")
    private final List<TestServerResponseForSettings> servers;

    @Schema(description = "Servers WS")
    @JsonProperty(value = "servers_ws")
    private final List<TestServerResponseForSettings> serverWSResponseList;

    @Schema(description = "Servers QoS")
    @JsonProperty(value = "servers_qos")
    private final List<TestServerResponseForSettings> serverQosResponseList;

    @Schema(description = "Client history")
    @JsonProperty(value = "history")
    private final SettingsHistoryResponse history;

    @Schema(description = "Client UUID")
    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @Schema(description = "Map server settings")
    @JsonProperty(value = "map_server")
    private final MapServerResponse mapServerResponse;
}
