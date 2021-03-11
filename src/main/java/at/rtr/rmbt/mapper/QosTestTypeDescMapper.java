package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.QosTestTypeDesc;
import at.rtr.rmbt.response.QosTestTypeDescResponse;

public interface QosTestTypeDescMapper {

    QosTestTypeDescResponse qosTestTypeDescToQosTestTypeDescResponse(QosTestTypeDesc qosTestTypeDesc);
}
