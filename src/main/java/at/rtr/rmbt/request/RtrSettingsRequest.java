package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.ClientType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class RtrSettingsRequest {

    @Schema(description = "Client type name", example = "MOBILE")
    @JsonProperty(value = "type")
    private final ClientType type;

    @Schema(description = "RMBT client name", example = "RMBT")
    @JsonProperty(value = "name")
    private final String name;

    @Schema(description = "2 letters language code or language code with region", example = "en")
    @JsonProperty(value = "language")
    private final String language;

    @Schema(description = "Platform of device", example = "Android")
    @JsonProperty(value = "plattform")
    private final String platform;

    @Schema(description = "Obtained by android.os.Build.VERSION.RELEASE + \"(\" + android.os.Build.VERSION.INCREMENTAL + \")\"", example = "9(G950FXXU5DSFB)")
    @JsonProperty(value = "os_version")
    private final String osVersion;

    @Schema(description = "Api level of the device String.valueOf(android.os.Build.VERSION.SDK_INT)", example = "28")
    @JsonProperty(value = "api_level")
    private final Long apiLevel;

    @Schema(description = "Obtained by android.os.Build.DEVICE", example = "dreamlte")
    @JsonProperty(value = "device")
    private final String device;

    @Schema(description = "Obtained by android.os.Build.MODEL", example = "SM-G950F")
    @JsonProperty(value = "model")
    private final String model;

    @Schema(description = "Obtained by android.os.Build.PRODUCT", example = "dreamltexxx")
    @JsonProperty(value = "product")
    private final String product;

    @Schema(description = "Human readable timezone", example = "Europe/Bratislava")
    @JsonProperty(value = "timezone")
    private final String timezone;

    @Schema(description = "Revision code of source code", example = "master_64bc39c-dirty")
    @JsonProperty(value = "softwareRevision")
    private final String softwareRevision;

    @Schema(description = "Version code from build gradle for Android devices", example = "33201")
    @JsonProperty(value = "softwareRevisionCode")
    private final Long softwareRevisionCode;

    @Schema(description = "Version of the app", example = "3.2.1")
    @JsonProperty(value = "softwareVersionName")
    private final String softwareVersionName;

    @Schema(description = "Same as softwareVersionCode", example = "30605")
    @JsonProperty(value = "version_code")
    private final Long versionCode;

    @Schema(description = "same as softwareVersionName", example = "3.2.1")
    @JsonProperty(value = "version_name")
    private final String versionName;

    @Schema(description = "UUID of the client if client is already registered, to register client leave this as empty string or do not send at all", example = "68796996-5f40-11eb-ae93-0242ac130002")
    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @Schema(description = "If true - list of the measurement servers will be sent, hidden settings", example = "true")
    @JsonProperty(value = "user_server_selection")
    private final boolean userServerSelection;

    @Schema(description = "Version of accepted terms and conditions", example = "6")
    @JsonProperty(value = "terms_and_conditions_accepted_version")
    private final Long termsAndConditionsAcceptedVersion;

    @Schema(description = "True if TaC was accepted", example = "true")
    @JsonProperty(value = "terms_and_conditions_accepted")
    private final boolean isTermsAndConditionsAccepted;

    @JsonProperty(value = "capabilities")
    private final CapabilitiesRequest capabilities;

    @Builder
    public RtrSettingsRequest(ClientType type, String name, String language, String platform, String osVersion, Long apiLevel, String device, String model, String product, String timezone, String softwareRevision, Long softwareRevisionCode, String softwareVersionName, Long versionCode, String versionName, UUID uuid, boolean userServerSelection, Long termsAndConditionsAcceptedVersion, boolean isTermsAndConditionsAccepted, CapabilitiesRequest capabilities) {
        this.type = type;
        this.name = Optional.ofNullable(name).orElse(StringUtils.EMPTY);
        this.language = Optional.ofNullable(language).orElse(StringUtils.EMPTY);
        this.platform = Optional.ofNullable(platform).orElse(StringUtils.EMPTY);
        this.osVersion = Optional.ofNullable(osVersion).orElse(StringUtils.EMPTY);
        this.apiLevel = apiLevel;
        this.device = device;
        this.model = model;
        this.product = product;
        this.timezone = timezone;
        this.softwareRevision = softwareRevision;
        this.softwareRevisionCode = softwareRevisionCode;
        this.softwareVersionName = softwareVersionName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.uuid = uuid;
        this.userServerSelection = userServerSelection;
        this.termsAndConditionsAcceptedVersion = Optional.ofNullable(termsAndConditionsAcceptedVersion).orElse(NumberUtils.LONG_ZERO);
        this.isTermsAndConditionsAccepted = isTermsAndConditionsAccepted;
        this.capabilities = capabilities;
    }

    public RtrSettingsRequest() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, null, false, null);
    }
}
