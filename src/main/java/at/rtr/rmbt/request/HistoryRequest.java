package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class HistoryRequest {

    @Schema(description = "Client UUID")
    @JsonProperty(value = "uuid")
    private final UUID clientUUID;

    @Schema(description = "2 letters language code or language code with region", example = "en")
    @JsonProperty(value = "language")
    private final String language;

    @Schema(description = "Result limit")
    @JsonProperty(value = "result_limit")
    private final Integer resultLimit;

    @Schema(description = "Result offset")
    @JsonProperty(value = "result_offset")
    private final Integer resultOffset;

    @Schema(description = "Devices")
    @JsonProperty(value = "devices")
    private final List<String> devices;

    @Schema(description = "Networks")
    @JsonProperty(value = "networks")
    private final List<String> networks;

    @Schema(description = "Include also failed tests")
    @JsonProperty(value="include_failed_tests", defaultValue = "false")
    private final boolean includeFailedTests;

    @Schema(description = "Include also coverage tests")
    @JsonProperty(value="include_coverage_fences", defaultValue = "false")
    private final boolean includeCoverageFences;

    @Schema(description = "Capabilities")
    @JsonProperty(value = "capabilities")
    private final CapabilitiesRequest capabilities;
}
