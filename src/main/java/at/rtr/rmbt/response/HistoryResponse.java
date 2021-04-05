package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode(callSuper = true)
public class HistoryResponse extends ErrorResponse {

    @ApiModelProperty(notes = "Client test history")
    @JsonProperty(value = "history")
    private final List<HistoryItemResponse> history;
}
