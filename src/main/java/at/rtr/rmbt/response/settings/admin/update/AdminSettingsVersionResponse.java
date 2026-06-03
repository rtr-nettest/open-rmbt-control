package at.rtr.rmbt.response.settings.admin.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Admin settings version response class.
 */
@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsVersionResponse {

    @JsonProperty(value = "controlServerVersion")
    private final String controlServerVersion;
}
