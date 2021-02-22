package at.rtr.rmbt.service;

import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.SignalMeasurementResponse;
import at.rtr.rmbt.response.SignalResultResponse;
import at.rtr.rmbt.response.SignalSettingsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

public interface SignalService {

    Page<SignalMeasurementResponse> getSignalsHistory(Pageable pageable);

    SignalSettingsResponse registerSignal(SignalRequest signalRequest, HttpServletRequest httpServletRequest);

    SignalResultResponse processSignalResult(SignalResultRequest signalResultRequest);
}
