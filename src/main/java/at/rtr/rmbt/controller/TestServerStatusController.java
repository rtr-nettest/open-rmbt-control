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
            + "with 24h aggregates. Optionally filtered to a single test_server uuid and/or protocol (4/6).")
    @GetMapping(URIConstants.TEST_SERVER_STATUS)
    public List<TestServerStatusResponse> getTestServerStatus(
            @RequestParam(value = "test_server", required = false) final UUID testServer,
            @RequestParam(value = "protocol", required = false) final Integer protocol) {
        return testServerStatusService.getStatus(testServer, protocol);
    }
}
