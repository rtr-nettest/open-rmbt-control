package at.rtr.rmbt.request.settings.admin.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsSignalTestRequest {

    private final String resultUrl;
}
