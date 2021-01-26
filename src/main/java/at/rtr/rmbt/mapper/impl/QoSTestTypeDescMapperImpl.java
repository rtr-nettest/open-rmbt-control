package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.QoSTestTypeDescMapper;
import at.rtr.rmbt.model.QoSTestTypeDesc;
import at.rtr.rmbt.response.QoSTestTypeDescResponse;
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
