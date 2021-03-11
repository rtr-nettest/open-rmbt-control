package at.rtr.rmbt.request.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsTermsAndConditionsRequest {

    @ApiModelProperty(value = "Default URL of terms and conditions for app", example = "https://www.netztest.at/en/tc.html")
    @JsonProperty(value = "tcUrl")
    private final String tcUrl;

    @ApiModelProperty(value = "Terms and conditions version number" , example = "6")
    @JsonProperty(value = "tcVersion")
    private final String tcVersion;

    @ApiModelProperty(value = "URL of terms and conditions for Android" , example = "https://www.netztest.at/en/tc_android.html")
    @JsonProperty(value = "tcUrlAndroid")
    private final String tcUrlAndroid;

    @ApiModelProperty(value = "URL of terms and conditions for the usage of NDT tests for Android" , example = "https://www.netztest.at/en/tc_android.html")
    @JsonProperty(value = "tcNdtUrlAndroid")
    private final String tcNdtUrlAndroid;

    @ApiModelProperty(value = "Terms and conditions version number for Android" , example = "6")
    @JsonProperty(value = "tcVersionAndroid")
    private final String tcVersionAndroid;

    @ApiModelProperty(value = "URL of terms and conditions for IOS" , example = "https://www.netztest.at/en/tc_ios.html")
    @JsonProperty(value = "tcUrlIOS")
    private final String tcUrlIOS;

    @ApiModelProperty(value = "Terms and conditions version number for IOS" , example = "6")
    @JsonProperty(value = "tcVersionIOS")
    private final String tcVersionIOS;
}
