package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CoverageResultRequest;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.TestResponse;

public interface TestMapper {

    TestResponse testToTestResponse(Test test);

    void updateTestWithSignalResultRequest(SignalResultRequest signalResultRequest, Test test);

    void updateTestWithResultRequest(ResultRequest resultRequest, Test test);

    Test updateTestLocation(Test test);

    void updateTestWithCoverageResultRequest(CoverageResultRequest coverageResultRequest, Test updatedTest);
}
