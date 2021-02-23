package at.rtr.rmbt.response.settings.admin.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsVersionResponse {

    private final String controlServerVersion;
}
