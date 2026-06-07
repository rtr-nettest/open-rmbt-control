package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.TestServerStatusResponse;
import at.rtr.rmbt.service.TestServerStatusService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TestServerStatusController {

    private final TestServerStatusService testServerStatusService;

    @Operation(summary = "Latest reachability/latency status of the measurement servers (per IP protocol), "
            + "with 24h aggregates. Optionally filtered to a single server uuid (query param 'server_uuid', "
            + "quotes tolerated) and/or protocol (4/6).")
    @GetMapping(URIConstants.TEST_SERVER_STATUS)
    public List<TestServerStatusResponse> getTestServerStatus(
            @RequestParam(value = "server_uuid", required = false) final String serverUuid,
            @RequestParam(value = "protocol", required = false) final Integer protocol) {
        return testServerStatusService.getStatus(parseUuid(serverUuid), protocol);
    }

    /**
     * Parses a server-uuid query value, tolerating surrounding single/double quotes and whitespace
     * (e.g. a value pasted as {@code "<uuid>"}). Returns {@code null} when blank; throws
     * {@link IllegalArgumentException} (→ HTTP 400) for a non-blank, non-UUID value.
     */
    static UUID parseUuid(final String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        while (s.length() >= 2
                && ((s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"')
                || (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\''))) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s.isEmpty() ? null : UUID.fromString(s);
    }
}
