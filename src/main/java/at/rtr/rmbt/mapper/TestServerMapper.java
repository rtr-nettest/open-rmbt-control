package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.response.TestServerResponse;

public interface TestServerMapper {
    TestServerResponse testServerToTestServerResponse(TestServer testServer);
}
