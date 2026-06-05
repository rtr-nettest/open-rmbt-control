package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.TestPlatform;
import at.rtr.rmbt.model.AndroidPermission;
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
public class SignalMeasurementResultRequest {

    @Schema(description = "UUID of the signal measurement test")
    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @Schema(description = "Sequence number of the measurement result within the measurement session", example = "0")
    @JsonProperty(value = "sequence_number")
    @NotNull
    @Min(0)
    private final Long sequenceNumber;

    @Schema(description = "Client uuid")
    @JsonProperty(value = "client_uuid")
    private final UUID clientUUID;

    @Schema(description = "Client version", example = "1.2.1")
    @JsonProperty(value = "client_version")
    private final String clientVersion;

    @Schema(description = "Language code of the current locale (ISO 639)")
    @JsonProperty(value = "client_language")
    private final String clientLanguage;

    @Schema(description = "Time zone of client", example = "Europe/Prague")
    @JsonProperty(value = "timezone")
    private final String timezone;

    @Schema(description = "Platform of device", example = "Android")
    @JsonProperty(value = "platform")
    private final TestPlatform platform;

    @Schema(description = "Product")
    @JsonProperty(value = "product")
    private final String product;

    @Schema(description = "Api level")
    @JsonProperty(value = "api_level")
    private final String apiLevel;

    @Schema(description = "OS version")
    @JsonProperty(value = "os_version")
    private final String osVersion;

    @Schema(description = "Model")
    @JsonProperty(value = "model")
    private final String model;

    @Schema(description = "Device")
    @JsonProperty(value = "device")
    private final String device;

    @Schema(description = "Client software version")
    @JsonProperty(value = "client_software_version")
    private final String clientSoftwareVersion;

    @Schema(description = "Server id for the network")
    @JsonProperty(value = "network_type")
    private final Integer networkType;

    @Schema(description = "Wifi supplicant state", example = "COMPLETED")
    @JsonProperty(value = "wifi_supplicant_state")
    private final String wifiSupplicantState;

    @Schema(description = "Wifi supplicant state detail", example = "OBTAINING_IPADDR")
    @JsonProperty(value = "wifi_supplicant_state_detail")
    private final String wifiSupplicantStateDetail;

    @Schema(description = "SSID of the wifi network")
    @JsonProperty(value = "wifi_ssid")
    private final String wifiSSID;

    @Schema(description = "Id of the wifi network")
    @JsonProperty(value = "wifi_network_id")
    private final String wifiNetworkId;

    @Schema(description = "BSSID of the wifi network")
    @JsonProperty(value = "wifi_bssid")
    private final String wifiBSSID;

    @Schema(description = "mcc-mnc of the operator network, mobile networks only", example = "231-06")
    @JsonProperty(value = "telephony_network_operator")
    private final String telephonyNetworkOperator;

    @Schema(description = "True if the network is roaming, mobile networks only")
    @JsonProperty(value = "telephony_network_is_roaming")
    private final Boolean telephonyNetworkIsRoaming;

    @Schema(description = "Country code for network, mobile networks only")
    @JsonProperty(value = "telephony_network_country")
    private final String telephonyNetworkCountry;

    @Schema(description = "Name of the network operator, mobile networks only", example = "O2 - SK")
    @JsonProperty(value = "telephony_network_operator_name")
    private final String telephonyNetworkOperatorName;

    @Schema(description = "Name of the sim operator, mobile networks only", example = "O2 - SK")
    @JsonProperty(value = "telephony_network_sim_operator_name")
    private final String telephonyNetworkSimOperatorName;

    @Schema(description = "Name of the sim operator, mobile networks only", example = "O2 - SK")
    @JsonProperty(value = "telephony_network_sim_operator")
    private final String telephonyNetworkSimOperator;

    @Schema(description = "Phone type, mobile networks only", example = "1")
    @JsonProperty(value = "telephony_phone_type")
    private final Integer telephonyPhoneType;

    @Schema(description = "Data state, mobile networks", example = "2")
    @JsonProperty(value = "telephony_data_state")
    private final Integer telephonyDataState;

    @Schema(description = "Name of the APN, mobile networks only", example = "6g4all")
    @JsonProperty(value = "telephony_apn")
    private final String telephonyAPN;

    @Schema(description = "Country code of the sim card issuer, mobile networks only", example = "sk")
    @JsonProperty(value = "telephony_network_sim_country")
    private final String telephonyNetworkSimCountry;

    @Schema(description = "Count of unsuccessful submissions")
    @JsonProperty(value = "submission_retry_count")
    private final Long submissionRetryCount;

    @Schema(description = "Status at test end as int; 0 (SUCCESS), 1 (ERROR), 2 (ABORTED)", example = "0")
    @JsonProperty(value = "test_status")
    private final String testStatus;

    @Schema(description = "Information on error cause")
    @JsonProperty(value = "test_error_cause")
    private final String testErrorCause;

    @Schema(description = "Capabilities")
    @JsonProperty(value = "capabilitiesRequest")
    private final CapabilitiesRequest capabilitiesRequest;

    @Schema(description = "RadioInfo array")
    @JsonProperty(value = "radioInfo")
    private final RadioInfoRequest radioInfo;

    @Schema(description = "Android permission array")
    @JsonProperty(value = "android_permission_status")
    private final List<AndroidPermission> permissionStatuses;

    @Schema(description = "CellLocation array")
    @JsonProperty(value = "cellLocations")
    private final List<CellLocationRequest> cellLocations;

    @Schema(description = "GeoLocation array")
    @JsonProperty(value = "geoLocations")
    private final List<GeoLocationRequest> geoLocations;

    @Schema(description = "Client public ip address, sent by control server", example = "192.168.1.100")
    @JsonProperty(value = "test_ip_local")
    private final String testIpLocal;

    @Schema(description = "Fence observations collected during the signal measurement session")
    @JsonProperty(value = "fences")
    private final List<FencesRequest> fences;

    @Schema(description = "Device temperature measured during the signal measurement")
    @JsonProperty(value = "temperature")
    private final Double temperature;

    @Schema(description = "Access point related measurement value")
    @JsonProperty(value = "apn")
    private final Double apn;
}
