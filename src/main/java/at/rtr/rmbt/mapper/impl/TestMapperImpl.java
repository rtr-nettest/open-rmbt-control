package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.response.TestResponse;
import org.springframework.stereotype.Service;

@Service
public class TestMapperImpl implements TestMapper {

    @Override
    public TestResponse testToTestResponse(Test test) {
        return TestResponse.builder()
                .testUUID(test.getUuid())
                .time(test.getTime())
                .build();
    }
}
