package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.response.SignalMeasurementResponse;

public interface SignalMapper {
    SignalMeasurementResponse signalToSignalMeasurementResponse(Test test);
}
