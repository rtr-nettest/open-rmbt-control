package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassificationRequest {

    @ApiModelProperty(value = "Amount of classification items supported by client", example = "5")
    @JsonProperty(value = "count")
    private final Long count;

}
