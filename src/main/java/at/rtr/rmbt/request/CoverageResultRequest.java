package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.TestPlatform;
import at.rtr.rmbt.model.Fences;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
public class CoverageResultRequest {

    // TODO - still old stuff (from SignalResultRequest), partly ok

    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @JsonProperty(value = "sequence_number")
    @NotNull
    @Min(0)
    private final Long sequenceNumber;

    @JsonProperty(value = "time_ns")
    private final Long timeNanos;

    @JsonProperty(value = "client_uuid")
    private final UUID clientUUID;

    @JsonProperty(value = "client_version")
    private final String clientVersion;

    @JsonProperty(value = "client_language")
    private final String clientLanguage;

    @Schema(description = "Time zone of client", example = "Europe/Prague")
    @JsonProperty(value = "timezone")
    private final String timezone;

    @JsonProperty(value = "platform")
    private final TestPlatform platform;

    @JsonProperty(value = "product")
    private final String product;

    @JsonProperty(value = "api_level")
    private final String apiLevel;

    @JsonProperty(value = "os_version")
    private final String osVersion;

    @JsonProperty(value = "model")
    private final String model;

    @JsonProperty(value = "device")
    private final String device;

    @JsonProperty(value = "client_software_version")
    private final String clientSoftwareVersion;

    @JsonProperty(value = "network_type")
    private final Integer networkType;

    @JsonProperty(value = "wifi_supplicant_state")
    private final String wifiSupplicantState;

    @JsonProperty(value = "wifi_supplicant_state_detail")
    private final String wifiSupplicantStateDetail;

    @JsonProperty(value = "wifi_ssid")
    private final String wifiSSID;

    @JsonProperty(value = "wifi_network_id")
    private final String wifiNetworkId;

    @JsonProperty(value = "wifi_bssid")
    private final String wifiBSSID;

    @JsonProperty(value = "telephony_network_operator")
    private final String telephonyNetworkOperator;

    @JsonProperty(value = "telephony_network_is_roaming")
    private final Boolean telephonyNetworkIsRoaming;

    @JsonProperty(value = "telephony_network_country")
    private final String telephonyNetworkCountry;

    @JsonProperty(value = "telephony_network_operator_name")
    private final String telephonyNetworkOperatorName;

    @JsonProperty(value = "telephony_network_sim_operator_name")
    private final String telephonyNetworkSimOperatorName;

    @JsonProperty(value = "telephony_network_sim_operator")
    private final String telephonyNetworkSimOperator;

    @JsonProperty(value = "telephony_phone_type")
    private final Integer telephonyPhoneType;

    @JsonProperty(value = "telephony_data_state")
    private final Integer telephonyDataState;

    @JsonProperty(value = "telephony_apn")
    private final String telephonyAPN;

    @JsonProperty(value = "telephony_network_sim_country")
    private final String telephonyNetworkSimCountry;

    @JsonProperty(value = "submission_retry_count")
    private final Long submissionRetryCount;

    @JsonProperty(value = "test_status")
    private final String testStatus;

    @JsonProperty(value = "test_error_cause")
    private final String testErrorCause;

    @JsonProperty(value = "capabilitiesRequest")
    private final CapabilitiesRequest capabilitiesRequest;

    @JsonProperty(value = "radioInfo")
    private final RadioInfoRequest radioInfo;

    @JsonProperty(value = "android_permission_status")
    private final List<AndroidPermissionStatus> permissionStatuses;

    @JsonProperty(value = "cellLocations")
    private final List<CellLocationRequest> cellLocations;

    @JsonProperty(value = "geoLocations")
    private final List<GeoLocationRequest> geoLocations;

    @JsonProperty(value = "test_ip_local")
    private final String testIpLocal;

    @JsonProperty(value = "fences")
    private final List<FencesRequest> fences;
}
