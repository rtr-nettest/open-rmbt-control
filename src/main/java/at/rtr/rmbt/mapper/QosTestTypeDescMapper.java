package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.QosTestTypeDesc;
import at.rtr.rmbt.response.QosTestTypeDescResponse;

/**
 * Qos test type desc mapper interface.
 */
public interface QosTestTypeDescMapper {

    /**
     * Qos test type desc to qos test type desc response.
     *
     * @param qosTestTypeDesc the Qos test type desc
     * @return the result
     */
    QosTestTypeDescResponse qosTestTypeDescToQosTestTypeDescResponse(QosTestTypeDesc qosTestTypeDesc);
}
