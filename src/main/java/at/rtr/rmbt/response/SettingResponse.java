package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class SettingResponse {

    @JsonProperty(value = "terms_and_conditions")
    private final TermAndConditionsResponse termAndConditionsResponse;

    @JsonProperty(value = "urls")
    private final UrlsResponse urls;

    @JsonProperty(value = "qostesttype_desc")
    private final List<QosTestTypeDescResponse> qosTestTypeDescResponse;

    @JsonProperty(value = "versions")
    private final VersionResponse versions;

    @JsonProperty(value = "servers")
    private final List<TestServerResponseForSettings> servers;

    @JsonProperty(value = "servers_ws")
    private final List<TestServerResponseForSettings> serverWSResponseList;

    @JsonProperty(value = "servers_qos")
    private final List<TestServerResponseForSettings> serverQosResponseList;

    @JsonProperty(value = "history")
    private final HistoryResponse history;

    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @JsonProperty(value = "map_server")
    private final MapServerResponse mapServerResponse;
}
