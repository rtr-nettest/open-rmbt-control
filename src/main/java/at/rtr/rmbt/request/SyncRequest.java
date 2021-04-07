package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class SyncRequest {

    @ApiModelProperty(notes = "2 letters language code or language code with region", example = "en")
    @JsonProperty(value = "language")
    private final String language;

    @ApiModelProperty(notes = "Client UUID")
    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @ApiModelProperty(notes = "Sync code")
    @JsonProperty(value = "sync_code")
    private final String syncCode;
}
