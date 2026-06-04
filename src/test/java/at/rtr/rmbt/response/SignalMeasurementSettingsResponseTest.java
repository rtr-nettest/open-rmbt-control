package at.rtr.rmbt.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignalMeasurementSettingsResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serialization_emitsBothSessionKeys_withSameValue() {
        var response = SignalMeasurementSettingsResponse.builder()
                .maxSignalMeasurementSessionSeconds(3600L)
                .maxSignalMeasurementSeconds(86400L)
                .build();

        JsonNode node = objectMapper.valueToTree(response);

        // New snake_case key (canonical).
        assertTrue(node.has("max_coverage_session_seconds"));
        assertEquals(3600L, node.get("max_coverage_session_seconds").asLong());

        // Legacy camelCase alias (deprecated) - same value, kept for not-yet-migrated clients.
        assertTrue(node.has("maxCoverageSessionSeconds"));
        assertEquals(3600L, node.get("maxCoverageSessionSeconds").asLong());

        // The measurement field was always snake_case only - no legacy alias.
        assertTrue(node.has("max_coverage_measurement_seconds"));
        assertEquals(86400L, node.get("max_coverage_measurement_seconds").asLong());
    }
}
