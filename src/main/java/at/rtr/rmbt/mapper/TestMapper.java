package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CoverageResultRequest;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.TestResponse;

/**
 * Test mapper interface.
 */
public interface TestMapper {

    /**
     * Test to test response.
     *
     * @param test the Test
     * @return the result
     */
    TestResponse testToTestResponse(Test test);

    /**
     * Update test with signal result request.
     *
     * @param signalResultRequest the Signal result request
     * @param test the Test
     */
    void updateTestWithSignalResultRequest(SignalResultRequest signalResultRequest, Test test);

    /**
     * Update test with result request.
     *
     * @param resultRequest the Result request
     * @param test the Test
     */
    void updateTestWithResultRequest(ResultRequest resultRequest, Test test);

    /**
     * Update test location.
     *
     * @param test the Test
     * @return the result
     */
    Test updateTestLocation(Test test);

    /**
     * Update test with coverage result request.
     *
     * @param coverageResultRequest the Coverage result request
     * @param updatedTest the Updated test
     */
    void updateTestWithCoverageResultRequest(CoverageResultRequest coverageResultRequest, Test updatedTest);
}
