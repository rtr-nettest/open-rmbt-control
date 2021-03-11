package at.rtr.rmbt.service;

import at.rtr.rmbt.response.MeasurementQosResponse;

import javax.servlet.http.HttpServletRequest;

public interface QosMeasurementService {

    MeasurementQosResponse getQosParameters(HttpServletRequest httpServletRequest);
}
