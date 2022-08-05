package at.rtr.rmbt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AndroidPermission {

    @Schema(description = "Whole name of the android permission", example = "android.permission.ACCESS_FINE_LOCATION")
    @JsonProperty(value = "permission")
    private String permission;

    @Schema(description = "True if it is granted, false otherwise")
    @JsonProperty(value = "status")
    private Boolean status;
}
