package at.rtr.rmbt.controller;

import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.service.TestServerService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static at.rtr.rmbt.constant.URIConstants.TEST_SERVER;

@RestController
@RequestMapping(TEST_SERVER)
@RequiredArgsConstructor
public class TestServerController {

    private final TestServerService testServerService;

    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create new test server.")
    @PostMapping
    public void createMeasurementServer(@Validated @RequestBody TestServerRequest testServerRequest) {
        testServerService.createTestServer(testServerRequest);
    }

    @ApiOperation("Get all test server.")
    @GetMapping
    public List<TestServerResponse> getAllTestServers() {
        return testServerService.getAllTestServer();
    }
}
