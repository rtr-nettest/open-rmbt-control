package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.response.QosMeasurementsResponse;

public interface QosTestResultMapper {
    QosMeasurementsResponse.QosTestResultItem toQosTestResultItem(QosTestResult qosTestResult, boolean isOpenTestUuid);
}
