package com.rtr.nettest.mapper;

import com.rtr.nettest.model.QoSTestTypeDesc;
import com.rtr.nettest.response.QoSTestTypeDescResponse;

public interface QoSTestTypeDescMapper {

    QoSTestTypeDescResponse qosTestTypeDescToQoSTestTypeDescResponse(QoSTestTypeDesc qoSTestTypeDesc);
}
