package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Signal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CoverageRegisterRequest;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.response.SignalMeasurementResponse;

public interface CoverageMapper {
    SignalMeasurementResponse signalToSignalMeasurementResponse(Test test);

    Signal coverageRequestToSignal(CoverageRegisterRequest coverageRegisterRequest, Test test);
}
