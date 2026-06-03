package at.rtr.rmbt.service;

import at.rtr.rmbt.request.CapabilitiesRequest;
import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosMeasurementsResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

/**
 * Qos measurement service interface.
 */
public interface QosMeasurementService {

    MeasurementQosResponse getQosParameters(HttpServletRequest httpServletRequest, Map<String, String> headers);

    /**
     * Save qos measurement result.
     *
     * @param qosResultRequest the Qos result request
     * @return the result
     */
    ErrorResponse saveQosMeasurementResult(QosResultRequest qosResultRequest);

    QosMeasurementsResponse getQosResult(UUID qosTestUuid, String language, CapabilitiesRequest capabilitiesRequest);

    /**
     * Evaluate qos by open test UUID.
     *
     * @param openTestUUID the Open test UUID
     * @param lang the Lang
     * @return the result
     */
    QosMeasurementsResponse evaluateQosByOpenTestUUID(UUID openTestUUID, String lang);
}
