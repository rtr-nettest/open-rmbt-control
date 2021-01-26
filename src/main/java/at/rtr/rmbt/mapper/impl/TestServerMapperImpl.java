package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.response.TestServerResponse;
import org.springframework.stereotype.Service;

@Service
public class TestServerMapperImpl implements TestServerMapper {
    @Override
    public TestServerResponse testServerToTestServerResponse(TestServer testServer) {
        return TestServerResponse.builder()
                .uuid(testServer.getUuid().toString())
                .name(testServer.getName())
                .build();
    }
}
