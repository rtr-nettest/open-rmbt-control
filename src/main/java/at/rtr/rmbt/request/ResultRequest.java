package at.rtr.rmbt.request;

import at.rtr.rmbt.model.AndroidPermission;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Api
@Builder
@Getter
@EqualsAndHashCode
@ApiModel(description = "Request to save test data")
public class ResultRequest {

    @ApiModelProperty(notes = "Test token", example = "8628925b-eda5-4500-9bbc-365f592470ce_1614328561_Dggllgjl/4zMNl97cNab2wgUb8k=")
    @JsonProperty(value = "test_token")
    private final String testToken;

    @ApiModelProperty(notes = "Client version", example = "1.2.1")
    @JsonProperty(value = "client_version")
    private final String clientVersion;

    @ApiModelProperty(notes = "Client name", example = "RMBT")
    @JsonProperty(value = "client_name")
    private final String clientName;

    @ApiModelProperty(notes = "mcc-mnc of the operator network, mobile networks only", example = "231-06")
    @JsonProperty(value = "telephony_network_operator")
    private final String telephonyNetworkOperator;

    @ApiModelProperty(notes = "Name of the sim operator, mobile networks only", example = "O2 - SK")
    @JsonProperty(value = "telephony_network_sim_operator")
    private final String telephonyNetworkSimOperator;

    @ApiModelProperty(notes = "Client public ip address, sent by control server", example = "192.168.1.100")
    @JsonProperty(value = "test_ip_local")
    private final String testIpLocal;

    @ApiModelProperty(notes = "Server public ip address", example = "81.16.157.221")
    @JsonProperty(value = "test_ip_server")
    private final String testIpServer;

    @ApiModelProperty(notes = "User server selection", example = "true")
    @JsonProperty(value = "user_server_selection")
    private final boolean userServerSelection;

    @ApiModelProperty(notes = "Speed details array")
    @JsonProperty(value = "speed_detail")
    private final List<SpeedDetailsRequest> speedDetails;

    @ApiModelProperty(notes = "Pings array")
    @JsonProperty(value = "pings")
    private final List<PingRequest> pings;

    @ApiModelProperty(notes = "GeoLocation array")
    @JsonProperty(value = "geoLocations")
    private final List<GeoLocationRequest> geoLocations;

    @ApiModelProperty(notes = "RadioInfo array")
    @JsonProperty(value = "radioInfo")
    private final RadioInfoRequest radioInfo;

    @ApiModelProperty(notes = "CellLocation array")
    @JsonProperty(value = "cellLocations")
    private final List<CellLocationRequest> cellLocations;

    @ApiModelProperty(notes = "Signal array")
    @JsonProperty(value = "signals")
    private final List<SignalRequest> signals;

    @ApiModelProperty(notes = "Android permission array")
    @JsonProperty(value = "android_permission_status")
    private final List<AndroidPermission> androidPermissionStatuses;

    @ApiModelProperty(notes = "Download speed in kbs", example = "7170")
    @JsonProperty(value = "test_speed_download")
    private final Integer downloadSpeed;

    @ApiModelProperty(notes = "Upload speed in kbs", example = "15061")
    @JsonProperty(value = "test_speed_upload")
    private final Integer uploadSpeed;

    @ApiModelProperty(value = "Shortest ping in nanos", example = "29783021")
    @JsonProperty(value = "test_ping_shortest")
    private final Long pingShortest;

    @ApiModelProperty(value = "Reason of the test finishing, provided as int value", example = "0")
    @JsonProperty(value = "test_status")
    private final String testStatus;
}
