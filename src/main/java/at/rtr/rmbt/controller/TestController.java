package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.TestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TestController {

    @Autowired
    private ObjectMapper objectMapper;

    private final TestService testService;

    @GetMapping(URIConstants.TEST + URIConstants.BY_TEST_UUID)
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

    @PostMapping(URIConstants.HISTORY)
    @ApiOperation(value = "Gets test history for the client device and synchronized devices")
    public HistoryResponse getHistory(@RequestBody HistoryRequest historyRequest) {
        return testService.getHistory(historyRequest);
    }

    @PostMapping(URIConstants.RESULT_UPDATE)
    @ApiOperation(value = "Update a test result (e.g. mark as aborted).")
    public ResultUpdateResponse updateTestResult(@RequestBody ResultUpdateRequest resultUpdateRequest, @RequestHeader Map<String, String> headers) {
        return testService.updateTestResult(resultUpdateRequest);
    }

    @PostMapping(value = URIConstants.RESULT_UPDATE, consumes = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Update a test result (e.g. mark as aborted). Needs to support plain/text because of navigator.sendBeacon sending such")
    public ResultUpdateResponse updateTestResultText(@RequestBody String resultUpdateRequest, @RequestHeader Map<String, String> headers) throws JsonProcessingException {

        ResultUpdateRequest jsonRequest = objectMapper.readValue(resultUpdateRequest, ResultUpdateRequest.class);
        return this.updateTestResult(jsonRequest, headers);
    }

    @PostMapping(URIConstants.ADMIN_SET_IMPLAUSIBLE)
    @ApiOperation(value = "Set implausible")
    public ImplausibleResponse setImplausible(@RequestBody ImplausibleRequest implausibleRequest) {
        return testService.setImplausible(implausibleRequest);
    }
}
