package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class SyncItemResponse {

    @Schema(description = "Sync code")
    @JsonProperty(value = "sync_code")
    private final String syncCode;

    @Schema(description = "Message title")
    @JsonProperty(value = "msg_title")
    private final String msgTitle;

    @Schema(description = "Message text")
    @JsonProperty(value = "msg_text")
    private final String msgText;

    @Schema(description = "True if synchronization successful")
    @JsonProperty(value = "success")
    private final boolean success;
}
