package at.rtr.rmbt.controller;


import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.CoverageRegisterRequest;
import at.rtr.rmbt.request.SignalRegisterRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.request.CoverageResultRequest;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.SignalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Signal controller class.
 */
@Tag(name = "Signal")
@Slf4j
@RestController
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    /**
     * Process signal request.
     *
     * @param httpServletRequest the Http servlet request
     * @param headers the Headers
     * @param signalRegisterRequest the Signal register request
     * @return the result
     */
    @PostMapping(URIConstants.SIGNAL_REQUEST)
    @Operation(summary = "Register signal", description = "Request to obtain configuration for signal monitoring")
    @ResponseStatus(HttpStatus.CREATED)
    public SignalSettingsResponse processSignalRequest(HttpServletRequest httpServletRequest,
                                                       @RequestHeader Map<String, String> headers,
                                                       @RequestBody SignalRegisterRequest signalRegisterRequest) {
        return signalService.processSignalRequest(signalRegisterRequest, httpServletRequest, headers);
    }

    /**
     * Process coverage request.
     *
     * @param httpServletRequest the Http servlet request
     * @param headers the Headers
     * @param coverageRegisterRequest the Coverage register request
     * @return the result
     */
    @PostMapping(URIConstants.COVERAGE_REQUEST)
    @Operation(summary = "Register coverage", description = "Request to obtain configuration for coverage monitoring")
    @ResponseStatus(HttpStatus.CREATED)
    public CoverageSettingsResponse processCoverageRequest(HttpServletRequest httpServletRequest,
                                                           @RequestHeader Map<String, String> headers,
                                                           @RequestBody CoverageRegisterRequest coverageRegisterRequest) {
        return signalService.processCoverageRequest(coverageRegisterRequest, httpServletRequest, headers);
    }

    /**
     * Get signal history.
     *
     * @param pageable the Pageable
     * @return the Signal history
     */
    @GetMapping(URIConstants.ADMIN_SIGNAL)
    @Operation(summary = "Get list of signal measurements")
    @ResponseStatus(HttpStatus.OK)
    public Page<SignalMeasurementResponse> getSignalHistory(@PageableDefault Pageable pageable) {
        return signalService.getSignalsHistory(pageable);
    }

    /**
     * Process signal result.
     *
     * @param signalResultRequest the Signal result request
     * @return the result
     */
    @PostMapping(URIConstants.SIGNAL_RESULT)
    @Operation(summary = "Process signal result")
    @ResponseStatus(HttpStatus.OK)
    public SignalResultResponse processSignalResult(@RequestBody SignalResultRequest signalResultRequest) {
        return signalService.processSignalResult(signalResultRequest);
    }

    /**
     * Get signal strength.
     *
     * @param testUUID the Test UUID
     * @return the Signal strength
     */
    @GetMapping(URIConstants.SIGNAL_STRENGTH_BY_UUID)
    @Operation(summary = "Get signal details")
    @ResponseStatus(HttpStatus.OK)
    public SignalDetailsResponse getSignalStrength(@PathVariable UUID testUUID) {
        return signalService.getSignalStrength(testUUID);
    }

    /**
     * Process coverage result.
     *
     * @param httpServletRequest the Http servlet request
     * @param headers the Headers
     * @param coverageResultRequest the Coverage result request
     * @return the result
     */
    @PostMapping(URIConstants.COVERAGE_RESULT)
    @Operation(summary = "Process coverage result")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> processCoverageResult(HttpServletRequest httpServletRequest,
                                                     @RequestHeader Map<String, String> headers,
                                                     @RequestBody CoverageResultRequest coverageResultRequest) {
        signalService.processCoverageResult(coverageResultRequest, httpServletRequest, headers);
        return Collections.emptyMap(); // Returns "{}" as JSON
    }



}
