package at.rtr.rmbt.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@EqualsAndHashCode
public class AdminSettingsRequest {

    @NotNull
    private final AdminSettingsBodyRequest settings;

    @ApiModelProperty(notes = "2 letters language code or language code with region", example = "en")
    private final String language;
}
