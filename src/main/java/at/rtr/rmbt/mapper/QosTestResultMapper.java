package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.response.QosMeasurementsResponse;

/**
 * Qos test result mapper interface.
 */
public interface QosTestResultMapper {
    /**
     * To qos test result item.
     *
     * @param qosTestResult the Qos test result
     * @param isOpenTestUuid the Is open test uuid
     * @return the result
     */
    QosMeasurementsResponse.QosTestResultItem toQosTestResultItem(QosTestResult qosTestResult, boolean isOpenTestUuid);
}
