package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;

import java.sql.Timestamp;

public interface TestServerMapper {
    TestServerResponseForSettings testServerToTestServerResponseForSettings(TestServer testServer);

    TestServerResponse testServerToTestServerResponse(TestServer testServer, Timestamp lastTestTimestamp, Timestamp lastSuccessfulTestTimestamp, boolean isLastMeasurementSuccess);

    TestServer testServerRequestToTestServer(TestServerRequest testServerRequest);
}
