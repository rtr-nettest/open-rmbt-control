package at.rtr.rmbt.service;

import at.rtr.rmbt.request.CapabilitiesRequest;
import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosMeasurementsResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

public interface QosMeasurementService {

    MeasurementQosResponse getQosParameters(HttpServletRequest httpServletRequest, Map<String, String> headers);

    ErrorResponse saveQosMeasurementResult(QosResultRequest qosResultRequest);

    QosMeasurementsResponse getQosResult(UUID qosTestUuid, String language, CapabilitiesRequest capabilitiesRequest);

    QosMeasurementsResponse evaluateQosByOpenTestUUID(UUID openTestUUID, String lang);
}
