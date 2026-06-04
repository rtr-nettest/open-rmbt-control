package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Service
public interface SignalService {

    Page<SignalMeasurementResponse> getSignalsHistory(Pageable pageable);

    SignalDetailsResponse getSignalStrength(UUID testUUID);

    void processSignalRequests(Collection<SignalRequest> signalRequests, Test test);

    SignalMeasurementSettingsResponse processSignalMeasurementRequest(SignalMeasurementRegisterRequest signalMeasurementRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers);

    void processSignalMeasurementResult(SignalMeasurementResultRequest signalMeasurementResultRequest, HttpServletRequest httpServletRequest, Map<String, String> headers);
}
