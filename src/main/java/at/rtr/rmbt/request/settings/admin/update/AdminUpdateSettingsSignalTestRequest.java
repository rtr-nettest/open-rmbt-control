package at.rtr.rmbt.request.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Admin update settings signal test request class.
 */
@Builder
@Getter
@EqualsAndHashCode
public class AdminUpdateSettingsSignalTestRequest {

    @JsonProperty(value = "resultUrl")
    private final String resultUrl;
}
