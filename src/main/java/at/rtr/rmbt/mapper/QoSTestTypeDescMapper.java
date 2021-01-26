package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.QoSTestTypeDesc;
import at.rtr.rmbt.response.QoSTestTypeDescResponse;

public interface QoSTestTypeDescMapper {

    QoSTestTypeDescResponse qosTestTypeDescToQoSTestTypeDescResponse(QoSTestTypeDesc qoSTestTypeDesc);
}
