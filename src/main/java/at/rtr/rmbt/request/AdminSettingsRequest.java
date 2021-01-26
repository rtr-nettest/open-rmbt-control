package at.rtr.rmbt.request;

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

    private final String language;
}
