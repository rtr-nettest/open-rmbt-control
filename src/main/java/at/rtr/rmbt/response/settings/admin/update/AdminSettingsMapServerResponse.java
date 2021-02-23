package at.rtr.rmbt.response.settings.admin.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsMapServerResponse {

    private final String port;

    private final String host;

    private final String ssl;
}
