package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.response.SignalSettingsResponse;

public interface TestMapper {

    SignalSettingsResponse testToSignalResponse(Test test);
}
