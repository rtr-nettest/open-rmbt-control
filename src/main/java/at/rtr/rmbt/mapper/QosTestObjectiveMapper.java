package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.QosTestObjective;
import at.rtr.rmbt.response.QosParamsResponse;

import java.net.InetAddress;

/**
 * Qos test objective mapper interface.
 */
public interface QosTestObjectiveMapper {

    /**
     * Qos test objective to qos params response.
     *
     * @param qosTestObjective the Qos test objective
     * @param clientAddress the Client address
     * @return the result
     */
    QosParamsResponse qosTestObjectiveToQosParamsResponse(QosTestObjective qosTestObjective, InetAddress clientAddress);
}
