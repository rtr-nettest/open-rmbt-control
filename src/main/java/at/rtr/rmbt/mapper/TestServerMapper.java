package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;

import java.sql.Timestamp;

/**
 * Test server mapper interface.
 */
public interface TestServerMapper {
    /**
     * Test server to test server response for settings.
     *
     * @param testServer the Test server
     * @return the result
     */
    TestServerResponseForSettings testServerToTestServerResponseForSettings(TestServer testServer);

    /**
     * Test server to test server response.
     *
     * @param testServer the Test server
     * @param lastTestTimestamp the Last test timestamp
     * @param lastSuccessfulTestTimestamp the Last successful test timestamp
     * @param isLastMeasurementSuccess the Is last measurement success
     * @return the result
     */
    TestServerResponse testServerToTestServerResponse(TestServer testServer, Timestamp lastTestTimestamp, Timestamp lastSuccessfulTestTimestamp, boolean isLastMeasurementSuccess);

    /**
     * Test server request to test server.
     *
     * @param testServerRequest the Test server request
     * @return the result
     */
    TestServer testServerRequestToTestServer(TestServerRequest testServerRequest);
}
