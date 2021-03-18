package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.TestResultDetailRequest;
import at.rtr.rmbt.request.TestResultRequest;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.response.TestResultContainerResponse;
import at.rtr.rmbt.response.TestResultDetailResponse;
import at.rtr.rmbt.service.TestService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(URIConstants.TEST)
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping(URIConstants.BY_TEST_UUID)
    @ApiOperation(value = "Get list of signal measurements")
    @ResponseStatus(HttpStatus.OK)
    public TestResponse getTestByUUID(@PathVariable UUID testUUID) {
        return testService.getTestByUUID(testUUID);
    }

    @PostMapping(URIConstants.TEST_RESULT_DETAIL)
    @ApiOperation(value = "Get test result detail")
    @ResponseStatus(HttpStatus.OK)
    public TestResultDetailResponse getTestResultDetailByTestUUID(@RequestBody TestResultDetailRequest testResultDetailRequestl) {
        return testService.getTestResultDetailByTestUUID(testResultDetailRequestl);
    }

    @PostMapping(URIConstants.TEST_RESULT)
    @ApiOperation(value = "Get test result")
    @ResponseStatus(HttpStatus.OK)
    public TestResultContainerResponse getTestResultByTestUUID(@RequestBody TestResultRequest testResultRequest) {
        return testService.getTestResult(testResultRequest);
    }
}
