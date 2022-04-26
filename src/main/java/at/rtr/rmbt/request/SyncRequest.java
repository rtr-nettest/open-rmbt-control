package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class SyncRequest {

    @Schema(description = "2 letters language code or language code with region", example = "en")
    @JsonProperty(value = "language")
    private final String language;

    @Schema(description = "Client UUID")
    @JsonProperty(value = "uuid")
    private final UUID uuid;

    @Schema(description = "Sync code")
    @JsonProperty(value = "sync_code")
    private final String syncCode;
}
