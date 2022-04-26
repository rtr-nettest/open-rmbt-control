package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode(callSuper = true)
public class HistoryResponse extends ErrorResponse {

    @Schema(description = "Client test history")
    @JsonProperty(value = "history")
    private final List<HistoryItemResponse> history;
}
