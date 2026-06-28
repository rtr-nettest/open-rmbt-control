package at.rtr.rmbt.controller;


import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.CoverageFenceDebugRequest;
import at.rtr.rmbt.request.SignalMeasurementRegisterRequest;
import at.rtr.rmbt.request.SignalMeasurementResultRequest;
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

@Tag(name = "Signal")
@Slf4j
@RestController
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @PostMapping(URIConstants.COVERAGE_REQUEST)
    @Operation(summary = "Register signal measurement", description = "Request to obtain configuration for signal measurement monitoring")
    @ResponseStatus(HttpStatus.CREATED)
    public SignalMeasurementSettingsResponse processSignalMeasurementRequest(HttpServletRequest httpServletRequest,
                                                           @RequestHeader Map<String, String> headers,
                                                           @RequestBody SignalMeasurementRegisterRequest signalMeasurementRegisterRequest) {
        return signalService.processSignalMeasurementRequest(signalMeasurementRegisterRequest, httpServletRequest, headers);
    }

    @GetMapping(URIConstants.ADMIN_SIGNAL)
    @Operation(summary = "Get list of signal measurements")
    @ResponseStatus(HttpStatus.OK)
    public Page<SignalMeasurementResponse> getSignalHistory(@PageableDefault Pageable pageable) {
        return signalService.getSignalsHistory(pageable);
    }

    @GetMapping(URIConstants.SIGNAL_STRENGTH_BY_UUID)
    @Operation(summary = "Get signal details")
    @ResponseStatus(HttpStatus.OK)
    public SignalDetailsResponse getSignalStrength(@PathVariable UUID testUUID) {
        return signalService.getSignalStrength(testUUID);
    }

    @PostMapping(URIConstants.COVERAGE_RESULT)
    @Operation(summary = "Process signal measurement result")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> processSignalMeasurementResult(HttpServletRequest httpServletRequest,
                                                     @RequestHeader Map<String, String> headers,
                                                     @RequestBody SignalMeasurementResultRequest signalMeasurementResultRequest) {
        signalService.processSignalMeasurementResult(signalMeasurementResultRequest, httpServletRequest, headers);
        return Collections.emptyMap(); // Returns "{}" as JSON
    }

    @PostMapping(URIConstants.COVERAGE_FENCE_DEBUG)
    @Operation(summary = "Debug: dump the internal per-fence data (signals/pings/technologies) used to compile the coverage submission")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> processCoverageFenceDebug(@RequestBody CoverageFenceDebugRequest request) {
        log.info("Coverage fence debug [test_uuid={} seq={}] signals={} technologies={} pings={} :: {}",
                request.getTestUuid(),
                request.getSequenceNumber(),
                request.getSignals() == null ? 0 : request.getSignals().size(),
                request.getTechnologies() == null ? 0 : request.getTechnologies().size(),
                request.getPings() == null ? 0 : request.getPings().size(),
                request);
        return Collections.emptyMap(); // Returns "{}" as JSON
    }

}
