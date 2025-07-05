package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.MeasurementType;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class CoverageRegisterRequest {

    @NotNull
    @Schema(description = "UUID of client", example = "68796996-5f40-11eb-ae93-0242ac130002")
    @JsonProperty(value = "client_uuid")
    private final UUID clientUuid;

    @Schema(description = "Language of client", example = "de")
    @JsonProperty(value = "client_language")
    private final String clientLanguage;

    @Schema(description = "Git branch and hash of software revision of client", example = "master-632-8bc288a")
    @JsonProperty(value = "softwareRevision")
    private final String softwareRevision;

    @Schema(description = "Model of client", example = "iPhone17,1")
    @JsonProperty(value = "model")
    private final String model;

    @Schema(description = "Operating system version of client", example = "18.5")
    @JsonProperty(value = "os_version")
    private final String osVersion;

    @Schema(description = "Name of the client", example = "RMBT")
    @JsonProperty(value = "client_name")
    private final String client_name;

    @Schema(description = "Software version number of client", example = "4.1")
    @JsonProperty(value = "client_software_version")
    private final String clientSoftwareVersion;

    @Schema(description = "Device type of the client", example = "iPhone")
    @JsonProperty(value = "device")
    private final String device;

    @Schema(description = "Software platform client", example = "iOS")
    @JsonProperty(value = "platform")
    private final String platform;

    @Schema(description = "Time zone of client", example = "Europe/Prague")
    @JsonProperty(value = "timezone")
    private final String timezone;

    @Schema(description = "Unix time in ms of client", example = "1571665024591")
    @JsonProperty(value = "time")
    private final Long time;

    @Schema(description = "Measurement type", example = "dedicated")
    @JsonProperty(value = "measurement_type_flag")
    private final MeasurementType measurementType;

    @Schema(description = "Signal information support by the client", example = "true")
    @JsonProperty(value = "signal", defaultValue = "false")
    private final Boolean signal;

    @JsonProperty(value = "capabilities")
    private final CapabilitiesRequest capabilities;

    @Schema(description = "Version code from the build gradle for Android devices", example = "33201")
    @JsonProperty("softwareVersionCode")
    private Integer softwareVersionCode;

    @Schema(description = "Just another historic version", example = "0.3")
    @JsonProperty(value = "version")
    private final String version;




}
