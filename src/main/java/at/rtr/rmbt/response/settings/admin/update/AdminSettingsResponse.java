package at.rtr.rmbt.response.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsResponse {

    @JsonProperty(value = "termsAndConditions")
    private final AdminSettingsTermAndConditionsResponse termAndConditionsResponse;

    private final AdminSettingsUrlsResponse urls;

    @JsonProperty(value = "testRequest")
    private final AdminSettingsTestResponse adminTestResponse;

    @JsonProperty(value = "signalTestRequest")
    private final AdminSettingsSignalTestResponse adminSettingsSignalTestResponse;

    @JsonProperty(value = "mapServer")
    private final AdminSettingsMapServerResponse mapServerResponse;

    private final AdminSettingsVersionResponse versions;

}
