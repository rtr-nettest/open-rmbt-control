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

/**
 * Signal service interface.
 */
@Service
public interface SignalService {

    Page<SignalMeasurementResponse> getSignalsHistory(Pageable pageable);

    /**
     * Process signal request.
     *
     * @param signalRegisterRequest the Signal register request
     * @param httpServletRequest the Http servlet request
     * @param headers the Headers
     * @return the result
     */
    SignalSettingsResponse processSignalRequest(SignalRegisterRequest signalRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers);

    /**
     * Process signal result.
     *
     * @param signalResultRequest the Signal result request
     * @return the result
     */
    SignalResultResponse processSignalResult(SignalResultRequest signalResultRequest);

    SignalDetailsResponse getSignalStrength(UUID testUUID);

    /**
     * Process signal requests.
     *
     * @param signalRequests the Signal requests
     * @param test the Test
     */
    void processSignalRequests(Collection<SignalRequest> signalRequests, Test test);

    /**
     * Process coverage request.
     *
     * @param coverageRegisterRequest the Coverage register request
     * @param httpServletRequest the Http servlet request
     * @param headers the Headers
     * @return the result
     */
    CoverageSettingsResponse processCoverageRequest(CoverageRegisterRequest coverageRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers);

    /**
     * Process coverage result.
     *
     * @param coverageResultRequest the Coverage result request
     * @param httpServletRequest the Http servlet request
     * @param headers the Headers
     */
    void processCoverageResult(CoverageResultRequest coverageResultRequest, HttpServletRequest httpServletRequest, Map<String, String> headers);
}
