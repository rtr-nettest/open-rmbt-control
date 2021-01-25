package com.rtr.nettest.mapper;

import com.rtr.nettest.model.Test;
import com.rtr.nettest.response.SignalResponse;

public interface TestMapper {

    SignalResponse testToSignalResponse(Test test);
}
