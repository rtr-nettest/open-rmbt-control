package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface SignalService {

    Page<SignalMeasurementResponse> getSignalsHistory(Pageable pageable);

    SignalSettingsResponse registerSignal(SignalRegisterRequest signalRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers);

    SignalResultResponse processSignalResult(SignalResultRequest signalResultRequest);

    SignalDetailsResponse getSignalStrength(UUID testUUID);

    void processSignalRequests(Collection<SignalRequest> signalRequests, Test test);

   CoverageSettingsResponse registerSignal(CoverageRegisterRequest coverageRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers);

    CoverageResultResponse processCoverageResult(CoverageResultRequest coverageResultRequest);
}
