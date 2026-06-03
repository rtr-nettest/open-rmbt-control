package at.rtr.rmbt.response.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Admin settings signal test response class.
 */
@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsSignalTestResponse {

    @JsonProperty(value = "resultUrl")
    private final String resultUrl;
}
