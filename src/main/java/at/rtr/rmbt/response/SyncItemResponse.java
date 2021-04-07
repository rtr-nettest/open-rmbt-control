package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class SyncItemResponse {

    @ApiModelProperty(notes = "Sync code")
    @JsonProperty(value = "sync_code")
    private final String syncCode;

    @ApiModelProperty(notes = "Message title")
    @JsonProperty(value = "msg_title")
    private final String msgTitle;

    @ApiModelProperty(notes = "Message text")
    @JsonProperty(value = "msg_text")
    private final String msgText;

    @ApiModelProperty(notes = "True if synchronization successful")
    @JsonProperty(value = "success")
    private final boolean success;
}
