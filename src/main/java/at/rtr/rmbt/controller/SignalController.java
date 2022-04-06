package at.rtr.rmbt.controller;


import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.SignalRegisterRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.SignalDetailsResponse;
import at.rtr.rmbt.response.SignalMeasurementResponse;
import at.rtr.rmbt.response.SignalResultResponse;
import at.rtr.rmbt.response.SignalSettingsResponse;
import at.rtr.rmbt.service.SignalService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @PostMapping(URIConstants.SIGNAL_REQUEST)
    @ApiOperation(value = "Register signal", notes = "Request to obtain configuration for signal monitoring")
    @ResponseStatus(HttpStatus.CREATED)
    public SignalSettingsResponse registerSignal(HttpServletRequest httpServletRequest,
                                                 @RequestHeader Map<String, String> headers,
                                                 @RequestBody SignalRegisterRequest signalRegisterRequest) {
        return signalService.registerSignal(signalRegisterRequest, httpServletRequest, headers);
    }

    @GetMapping(URIConstants.ADMIN_SIGNAL)
    @ApiOperation(value = "Get list of signal measurements")
    @ResponseStatus(HttpStatus.OK)
    public Page<SignalMeasurementResponse> getSignalHistory(@PageableDefault Pageable pageable) {
        return signalService.getSignalsHistory(pageable);
    }

    @PostMapping(URIConstants.SIGNAL_RESULT)
    @ApiOperation(value = "Process signal result")
    @ResponseStatus(HttpStatus.OK)
    public SignalResultResponse processSignalResult(@RequestBody SignalResultRequest signalResultRequest) {
        return signalService.processSignalResult(signalResultRequest);
    }

    @GetMapping(URIConstants.SIGNAL_STRENGTH_BY_UUID)
    @ApiOperation(value = "Get signal details")
    @ResponseStatus(HttpStatus.OK)
    public SignalDetailsResponse getSignalStrength(@PathVariable UUID testUUID) {
        return signalService.getSignalStrength(testUUID);
    }
}
