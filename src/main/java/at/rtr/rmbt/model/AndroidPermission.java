package at.rtr.rmbt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AndroidPermission {

    @ApiModelProperty(notes = "Whole name of the android permission", example = "android.permission.ACCESS_FINE_LOCATION")
    @JsonProperty(value = "permission")
    private String permission;

    @ApiModelProperty(notes = "True if it is granted, false otherwise")
    @JsonProperty(value = "status")
    private Boolean status;
}
