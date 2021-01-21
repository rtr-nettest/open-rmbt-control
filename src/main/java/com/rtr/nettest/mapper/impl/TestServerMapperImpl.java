package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.model.TestServer;
import com.rtr.nettest.mapper.TestServerMapper;
import com.rtr.nettest.response.TestServerResponse;
import org.springframework.stereotype.Service;

@Service
public class TestServerMapperImpl implements TestServerMapper {

    @Override
    public TestServerResponse testServerToTestServerResponse(TestServer testServer) {
        return TestServerResponse.builder()
                .uuid(testServer.getUuid())
                .name(testServer.getName())
                .build();
    }
}
