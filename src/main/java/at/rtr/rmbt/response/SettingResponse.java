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

    private final UrlsResponse urls;

    @JsonProperty(value = "qostesttype_desc")
    private final List<QoSTestTypeDescResponse> qosTestTypeDescResponse;

    private final VersionResponse versions;

    @JsonProperty(value = "servers")
    private final List<TestServerResponse> servers;

    @JsonProperty(value = "servers_ws")
    private final List<TestServerResponse> serverWSResponseList;

    @JsonProperty(value = "servers_qos")
    private final List<TestServerResponse> serverQoSResponseList;

    private final HistoryResponse history;

    private final UUID uuid;

    @JsonProperty(value = "map_server")
    private final MapServerResponse mapServerResponse;
}
