package at.rtr.rmbt.response.settings.admin.update;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsTermAndConditionsResponse {

    @ApiModelProperty(value = "Default URL of terms and conditions for app", example = "https://www.netztest.at/en/tc.html")
    private final String tcUrl;

    @ApiModelProperty(value = "URL of terms and conditions for IOS" , example = "https://www.netztest.at/en/tc_ios.html")
    private final String tcUrlIOS;

    @ApiModelProperty(value = "URL of terms and conditions for Android" , example = "https://www.netztest.at/en/tc_android.html")
    private final String tcUrlAndroid;

    @ApiModelProperty(value = "Terms and conditions version number" , example = "6")
    private final String tcVersion;

    @ApiModelProperty(value = "Terms and conditions version number for IOS" , example = "6")
    private final String tcVersionIOS;

    @ApiModelProperty(value = "Terms and conditions version number for Android" , example = "6")
    private final String tcVersionAndroid;

    @ApiModelProperty(value = "URL of terms and conditions for the usage of NDT tests for Android" , example = "https://www.netztest.at/en/tc_android.html")
    private final String tcNdtUrlAndroid;
}
