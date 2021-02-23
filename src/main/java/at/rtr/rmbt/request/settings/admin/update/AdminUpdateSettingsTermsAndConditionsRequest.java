package at.rtr.rmbt.request.settings.admin.update;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsTermsAndConditionsRequest {

    @ApiModelProperty(value = "Default URL of terms and conditions for app", example = "https://www.netztest.at/en/tc.html")
    private final String url;

    @ApiModelProperty(value = "Terms and conditions version number", example = "6")
    private final String version;

    @ApiModelProperty(value = "URL of terms and conditions for the usage of NDT tests for Android", example = "https://www.netztest.at/en/tc_android.html")
    private final String ndtUrl;

}
