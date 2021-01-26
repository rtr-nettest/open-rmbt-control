package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.response.SignalResponse;
import org.springframework.stereotype.Service;

@Service
public class TestMapperImpl implements TestMapper {

    @Override
    public SignalResponse testToSignalResponse(Test test) {
        return SignalResponse.builder()
                .build();
    }
}
