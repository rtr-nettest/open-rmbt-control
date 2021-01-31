package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;

public interface TestServerMapper {
    TestServerResponseForSettings testServerToTestServerResponseForSettings(TestServer testServer);

    TestServerResponse testServerToTestServerResponse(TestServer testServer);

    TestServer testServerRequestToTestServer(TestServerRequest testServerRequest);
}
