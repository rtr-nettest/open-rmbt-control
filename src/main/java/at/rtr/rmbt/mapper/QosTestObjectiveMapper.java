package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.QosTestObjective;
import at.rtr.rmbt.response.QosParamsResponse;

import java.net.InetAddress;

public interface QosTestObjectiveMapper {

    QosParamsResponse qosTestObjectiveToQosParamsResponse(QosTestObjective qosTestObjective, InetAddress clientAddress);
}
