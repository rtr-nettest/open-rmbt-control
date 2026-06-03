package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Signal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CoverageRegisterRequest;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.response.SignalMeasurementResponse;

/**
 * Coverage mapper interface.
 */
public interface CoverageMapper {
    /**
     * Signal to signal measurement response.
     *
     * @param test the Test
     * @return the result
     */
    SignalMeasurementResponse signalToSignalMeasurementResponse(Test test);

    /**
     * Coverage request to signal.
     *
     * @param coverageRegisterRequest the Coverage register request
     * @param test the Test
     * @return the result
     */
    Signal coverageRequestToSignal(CoverageRegisterRequest coverageRegisterRequest, Test test);
}
