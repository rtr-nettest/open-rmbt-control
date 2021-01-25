package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.mapper.TestMapper;
import com.rtr.nettest.model.Test;
import com.rtr.nettest.response.SignalResponse;
import org.springframework.stereotype.Service;

@Service
public class TestMapperImpl implements TestMapper {

    @Override
    public SignalResponse testToSignalResponse(Test test) {
        return SignalResponse.builder()
                .build();
    }
}
