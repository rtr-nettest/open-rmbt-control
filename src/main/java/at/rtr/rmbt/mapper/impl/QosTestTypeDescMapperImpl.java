package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.QosTestTypeDescMapper;
import at.rtr.rmbt.model.QosTestTypeDesc;
import at.rtr.rmbt.response.QosTestTypeDescResponse;
import org.springframework.stereotype.Service;

@Service
public class QosTestTypeDescMapperImpl implements QosTestTypeDescMapper {

    @Override
    public QosTestTypeDescResponse qosTestTypeDescToQosTestTypeDescResponse(QosTestTypeDesc qosTestTypeDesc) {
        return QosTestTypeDescResponse.builder()
                .name(qosTestTypeDesc.getName())
                .testType(qosTestTypeDesc.getTest().toString())
                .build();
    }
}
