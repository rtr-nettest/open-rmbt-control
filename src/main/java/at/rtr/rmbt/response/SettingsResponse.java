package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SettingsResponse {

    @JsonProperty(value = "settings")
    private final List<SettingResponse> settings;
}
