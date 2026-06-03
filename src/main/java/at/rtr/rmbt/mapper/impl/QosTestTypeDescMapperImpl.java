package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.QosTestTypeDescMapper;
import at.rtr.rmbt.model.QosTestTypeDesc;
import at.rtr.rmbt.response.QosTestTypeDescResponse;
import org.springframework.stereotype.Service;

/**
 * Qos test type desc mapper impl class.
 */
@Service
public class QosTestTypeDescMapperImpl implements QosTestTypeDescMapper {

    /**
     * Qos test type desc to qos test type desc response.
     *
     * @param qosTestTypeDesc the Qos test type desc
     * @return the result
     */
    @Override
    public QosTestTypeDescResponse qosTestTypeDescToQosTestTypeDescResponse(QosTestTypeDesc qosTestTypeDesc) {
        return QosTestTypeDescResponse.builder()
                .name(qosTestTypeDesc.getName())
                .testType(qosTestTypeDesc.getTest().toString())
                .build();
    }
}
