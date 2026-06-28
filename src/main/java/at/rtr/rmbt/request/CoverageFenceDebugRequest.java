package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Debug-only payload: the internal data the client uses to compile a single coverage fence's summary,
 * sent right after that fence completes. Not persisted - logged for inspection.
 */
@Getter
@Builder
@ToString
@Schema(description = "Debug dump of the internal data used to compile a single coverage fence")
public class CoverageFenceDebugRequest {

    @Schema(description = "Test/measurement UUID this fence belongs to")
    @JsonProperty("test_uuid")
    private final String testUuid;

    @Schema(description = "Sequence number of the fence within the measurement")
    @JsonProperty("sequence_number")
    private final Integer sequenceNumber;

    @Schema(description = "Per-technology minimum signal samples kept for the fence")
    @JsonProperty("signals")
    private final List<CoverageFenceDebugSignalRequest> signals;

    @Schema(description = "Technologies (with band) seen during the fence")
    @JsonProperty("technologies")
    private final List<CoverageFenceDebugTechnologyRequest> technologies;

    @Schema(description = "Raw UDP pings collected during the fence")
    @JsonProperty("pings")
    private final List<CoverageFenceDebugPingRequest> pings;
}
