package at.rtr.rmbt.response.settings.admin.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsTermAndConditionsResponse {

    private final String version;

    private final String url;

    private final String ndtUrl;
}
