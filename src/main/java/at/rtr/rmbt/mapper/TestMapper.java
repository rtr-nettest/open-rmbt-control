package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.response.SignalResponse;

public interface TestMapper {

    SignalResponse testToSignalResponse(Test test);
}
