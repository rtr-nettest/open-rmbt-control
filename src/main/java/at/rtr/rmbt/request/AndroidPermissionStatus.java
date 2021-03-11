package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AndroidPermissionStatus {

    @JsonProperty(value = "permission")
    private final String permission;

    @JsonProperty(value = "status")
    private final boolean status;
}
