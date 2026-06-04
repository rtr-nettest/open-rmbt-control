package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.SignalMeasurementResultRequest;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.response.TestResponse;

public interface TestMapper {

    TestResponse testToTestResponse(Test test);

    void updateTestWithResultRequest(ResultRequest resultRequest, Test test);

    Test updateTestLocation(Test test);

    void updateTestWithSignalMeasurementResultRequest(SignalMeasurementResultRequest signalMeasurementResultRequest, Test updatedTest);
}
