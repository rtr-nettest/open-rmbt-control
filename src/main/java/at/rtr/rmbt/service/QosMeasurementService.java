package at.rtr.rmbt.service;

import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.MeasurementQosResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface QosMeasurementService {

    MeasurementQosResponse getQosParameters(HttpServletRequest httpServletRequest, Map<String, String> headers);

    ErrorResponse saveQosMeasurementResult(QosResultRequest qosResultRequest);
}
