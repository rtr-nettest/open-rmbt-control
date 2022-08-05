package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsRequest {

    @NotNull
    @JsonProperty(value = "settings")
    private final AdminSettingsBodyRequest settings;

    @Schema(description = "2 letters language code or language code with region", example = "en")
    @JsonProperty(value = "language")
    private final String language;
}
