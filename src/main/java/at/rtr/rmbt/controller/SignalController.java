package at.rtr.rmbt.controller;


import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.SignalRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @PostMapping(URIConstants.SIGNAL_REQUEST)
    @ApiOperation(value = "Register signal", notes = "Request to obtain configuration for signal monitoring")
    @ResponseStatus(HttpStatus.CREATED)
    public SignalSettingsResponse registerSignal(HttpServletRequest httpServletRequest, @RequestBody SignalRequest signalRequest) {
        return signalService.registerSignal(signalRequest, httpServletRequest);
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
        log.info("--------------- UUID -------------");

        log.info(signalResultRequest.getClientUUID() == null ? "null" : signalResultRequest.getClientUUID().toString());
        log.info("--------------- RadioInfo  Cells  -------------");

        signalResultRequest.getRadioInfo().getCells()
                .forEach(l -> {
                    log.info("UUID " + (l.getUuid() == null ? "null" : l.getUuid().toString()));
                    log.info("Technology " + (l.getTechnology() == null ? "null" : l.getTechnology().toString()));
                });

        log.info("--------------- RadioInfo  Signals -------------");
        signalResultRequest.getRadioInfo().getSignals()
                .forEach(l -> {
                    log.info("CellUUID " + (l.getCellUUID() == null ? "null" : l.getCellUUID().toString()));
                    log.info("NetworkTypeId " + (l.getNetworkTypeId() == null ? "null" : l.getNetworkTypeId().toString()));
                    log.info("LteRSRP " + (l.getLteRSRP() == null ? "null" : l.getLteRSRP().toString()));
                    log.info("Signal " + (l.getSignal() == null ? "null" : l.getSignal().toString()));
                    log.info("TimeNS " + (l.getTimeNs() == null ? "null" : l.getTimeNs().toString()));
                });
        log.info("--------------- End -------------");

        return signalService.processSignalResult(signalResultRequest);
    }

    @GetMapping(URIConstants.SIGNAL_STRENGTH_BY_UUID)
    @ApiOperation(value = "Get signal details")
    @ResponseStatus(HttpStatus.OK)
    public SignalDetailsResponse getSignalStrength(@PathVariable UUID testUUID) {
        return signalService.getSignalStrength(testUUID);
    }
}
