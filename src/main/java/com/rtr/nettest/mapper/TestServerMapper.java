package com.rtr.nettest.mapper;

import com.rtr.nettest.model.TestServer;
import com.rtr.nettest.response.TestServerResponse;

public interface TestServerMapper {

    TestServerResponse testServerToTestServerResponse(TestServer testServer);
}
