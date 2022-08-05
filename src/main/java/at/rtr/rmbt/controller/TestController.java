package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Test")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping(URIConstants.TEST + URIConstants.BY_TEST_UUID)
    @Operation(summary = "Get list of signal measurements")
    @ResponseStatus(HttpStatus.OK)
    public TestResponse getTestByUUID(@PathVariable UUID testUUID) {
        return testService.getTestByUUID(testUUID);
    }

    @PostMapping(URIConstants.TEST_RESULT_DETAIL)
    @Operation(summary = "Get test result detail")
    @ResponseStatus(HttpStatus.OK)
    public TestResultDetailResponse getTestResultDetailByTestUUID(@RequestBody TestResultDetailRequest testResultDetailRequestl) {
        return testService.getTestResultDetailByTestUUID(testResultDetailRequestl);
    }

    @PostMapping(URIConstants.TEST_RESULT)
    @Operation(summary = "Get test result")
    @ResponseStatus(HttpStatus.OK)
    public TestResultContainerResponse getTestResultByTestUUID(@RequestBody TestResultRequest testResultRequest) {
        return testService.getTestResult(testResultRequest);
    }

    @PostMapping(URIConstants.HISTORY)
    @Operation(summary = "Gets test history for the client device and synchronized devices")
    public HistoryResponse getHistory(@RequestBody HistoryRequest historyRequest) {
        return testService.getHistory(historyRequest);
    }

    @PostMapping(URIConstants.RESULT_UPDATE)
    @Operation(summary = "Gets test history for the client device and synchronized devices")
    public ResultUpdateResponse updateTestResult(@RequestBody ResultUpdateRequest resultUpdateRequest) {
        return testService.updateTestResult(resultUpdateRequest);
    }

    @PostMapping(URIConstants.ADMIN_SET_IMPLAUSIBLE)
    @Operation(summary = "Set implausible")
    public ImplausibleResponse setImplausible(@RequestBody ImplausibleRequest implausibleRequest) {
        return testService.setImplausible(implausibleRequest);
    }
}
