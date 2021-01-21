package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.model.QoSTestTypeDesc;
import com.rtr.nettest.mapper.QoSTestTypeDescMapper;
import com.rtr.nettest.response.QoSTestTypeDescResponse;
import org.springframework.stereotype.Service;

@Service
public class QoSTestTypeDescMapperImpl implements QoSTestTypeDescMapper {

    @Override
    public QoSTestTypeDescResponse qosTestTypeDescToQoSTestTypeDescResponse(QoSTestTypeDesc qoSTestTypeDesc) {
        return QoSTestTypeDescResponse.builder()
                .name(qoSTestTypeDesc.getName())
                .testType(qoSTestTypeDesc.getTest().toString())
                .build();
    }
}
