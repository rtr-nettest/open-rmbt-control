package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Test controller class.
 */
@Tag(name = "Test")
@RestController
@RequiredArgsConstructor
public class TestController {

    @Autowired
    private ObjectMapper objectMapper;

    private final TestService testService;

    /**
     * Get test by UUID.
     *
     * @param testUUID the Test UUID
     * @return the Test by UUID
     */
    @GetMapping(URIConstants.TEST + URIConstants.BY_TEST_UUID)
    @Operation(summary = "Get list of signal measurements")
    @ResponseStatus(HttpStatus.OK)
    public TestResponse getTestByUUID(@PathVariable UUID testUUID) {
        return testService.getTestByUUID(testUUID);
    }

    @PostMapping(URIConstants.TEST_RESULT_DETAIL)
    @Deprecated
    @Operation(
            summary = "Get test result details",
            description = "This endpoint is deprecated. Use the endpoint '/testresult' and Open Data instead.",
            deprecated = true
    )
    /**
     * Get test result detail by test UUID.
     *
     * @param testResultDetailRequestl the Test result detail requestl
     * @return the Test result detail by test UUID
     */
    @ResponseStatus(HttpStatus.OK)
    public TestResultDetailResponse getTestResultDetailByTestUUID(@RequestBody TestResultDetailRequest testResultDetailRequestl) {
        return testService.getTestResultDetailByTestUUID(testResultDetailRequestl);
    }

    // this is the starting point for processing of the "/testresult" request by the Spring framework
    /**
     * Get test result by test UUID.
     *
     * @param testResultRequest the Test result request
     * @return the Test result by test UUID
     */
    @PostMapping(URIConstants.TEST_RESULT)
    @Operation(summary = "Get test result")
    @ResponseStatus(HttpStatus.OK)
    public TestResultContainerResponse getTestResultByTestUUID(@RequestBody TestResultRequest testResultRequest) {
        return testService.getTestResult(testResultRequest);
    }

    /**
     * Get history.
     *
     * @param historyRequest the History request
     * @return the History
     */
    @PostMapping(URIConstants.HISTORY)
    @Operation(summary = "Gets test history for the client device and synchronized devices")
    public HistoryResponse getHistory(@RequestBody HistoryRequest historyRequest) {
        return testService.getHistory(historyRequest);
    }

    /**
     * Update test result.
     *
     * @param resultUpdateRequest the Result update request
     * @return the result
     */
    @PostMapping(URIConstants.RESULT_UPDATE)
    @Operation(summary = "Update a test result (e.g. mark as aborted).")
    public ResultUpdateResponse updateTestResult(@RequestBody ResultUpdateRequest resultUpdateRequest) {
        return testService.updateTestResult(resultUpdateRequest);
    }

    /**
     * Update test result text.
     *
     * @param resultUpdateRequest the Result update request
     * @return the result
     * @throws JsonProcessingException if an error occurs
     */
    @PostMapping(value = URIConstants.RESULT_UPDATE, consumes = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Update a test result (e.g. mark as aborted). Needs to support plain/text because of navigator.sendBeacon sending such")
    public ResultUpdateResponse updateTestResultText(@RequestBody String resultUpdateRequest) throws JsonProcessingException {

        ResultUpdateRequest jsonRequest = objectMapper.readValue(resultUpdateRequest, ResultUpdateRequest.class);
        return this.updateTestResult(jsonRequest);
    }

    /**
     * Sets the Implausible.
     *
     * @param implausibleRequest the Implausible request
     * @return the result
     */
    @PostMapping(URIConstants.ADMIN_SET_IMPLAUSIBLE)
    @Operation(summary = "Set implausible")
    public ImplausibleResponse setImplausible(@RequestBody ImplausibleRequest implausibleRequest) {
        return testService.setImplausible(implausibleRequest);
    }
}
