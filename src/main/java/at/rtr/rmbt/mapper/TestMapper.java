package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.response.SignalSettingsResponse;
import at.rtr.rmbt.response.TestResponse;

public interface TestMapper {

    TestResponse testToTestResponse(Test test);
}
