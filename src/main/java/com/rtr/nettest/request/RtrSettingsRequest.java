package com.rtr.nettest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private final String type;

    private final String name;

    private final String language;

    @JsonProperty(value = "plattform")
    private final String platform;

    @JsonProperty(value = "os_version")
    private final String osVersion;

    @JsonProperty(value = "api_level")
    private final Long apiLevel;

    private final String device;

    private final String model;

    private final String product;

    private final String timezone;

    private final String softwareRevision;

    private final Long softwareRevisionCode;

    private final String softwareVersionName;

    @JsonProperty(value = "version_code")
    private final Long versionCode;

    @JsonProperty(value = "version_name")
    private final String versionName;

    private final UUID uuid;

    @JsonProperty(value = "user_server_selection")
    private final boolean userServerSelection;

    @JsonProperty(value = "terms_and_conditions_accepted_version")
    private final Long termsAndConditionsAcceptedVersion;

    @JsonProperty(value = "terms_and_conditions_accepted")
    private final boolean isTermsAndConditionsAccepted;

    private final CapabilitiesRequest capabilities;

    @Builder
    public RtrSettingsRequest(String type, String name, String language, String platform, String osVersion, Long apiLevel, String device, String model, String product, String timezone, String softwareRevision, Long softwareRevisionCode, String softwareVersionName, Long versionCode, String versionName, UUID uuid, boolean userServerSelection, Long termsAndConditionsAcceptedVersion, boolean isTermsAndConditionsAccepted, CapabilitiesRequest capabilities) {
        this.type = Optional.ofNullable(type).orElse(StringUtils.EMPTY);
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
