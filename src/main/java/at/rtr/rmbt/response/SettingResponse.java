package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Setting response class.
 */
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

    @JsonProperty("classification_thresholds")
    @Schema(description = "Thresholds for traffic light classification",
            example =
                    "{\"download_kbit\":{\"2\":5000,\"3\":10000,\"4\":100000},\"upload_kbit\":{\"2\":10000,\"3\":20000,\"4\":30000},\"ping_ms\":{\"2\":75,\"3\":25,\"4\":10},\"signal_mobile\":{\"2\":-101,\"3\":-85,\"4\":-75},\"signal_mobile_rsrp\":{\"2\":-111,\"3\":-95,\"4\":-85},\"signal_wifi\":{\"2\":-76,\"3\":-61,\"4\":-51}}"
    )
    private JsonNode classificationThresholds;
}
