package at.rtr.rmbt.request.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsRequest {

    @JsonProperty(value = "termsAndConditions")
    private final AdminUpdateSettingsTermsAndConditionsRequest adminUpdateSettingsTermsAndConditionsRequest;

    @JsonProperty(value = "urls")
    private final AdminUpdateSettingsUrlsRequest adminUpdateSettingsUrlsRequest;

    @JsonProperty(value = "testRequest")
    private final AdminUpdateSettingsTestRequest adminUpdateSettingsTestRequest;

    @JsonProperty(value = "signalTestRequest")
    private final AdminUpdateSettingsSignalTestRequest adminUpdateSettingsSignalTestRequest;

    @JsonProperty(value = "mapServer")
    private final AdminUpdateSettingsMapServerRequest adminUpdateSettingsMapServerRequest;
}