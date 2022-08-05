package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class SyncResponse extends ErrorResponse {

    @Schema(description = "Sync item response")
    @JsonProperty(value = "sync")
    private final List<SyncItemResponse> sync;
}
