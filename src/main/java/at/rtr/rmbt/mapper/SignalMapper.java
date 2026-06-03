package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Signal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.response.SignalMeasurementResponse;

/**
 * Signal mapper interface.
 */
public interface SignalMapper {
    /**
     * Signal to signal measurement response.
     *
     * @param test the Test
     * @return the result
     */
    SignalMeasurementResponse signalToSignalMeasurementResponse(Test test);

    /**
     * Signal request to signal.
     *
     * @param signalRequest the Signal request
     * @param test the Test
     * @return the result
     */
    Signal signalRequestToSignal(SignalRequest signalRequest, Test test);
}
