package at.rtr.rmbt.service;

import at.rtr.rmbt.response.QosTestTypeDescResponse;

import java.util.List;

/**
 * Qos test type desc service interface.
 */
public interface QosTestTypeDescService {

    List<QosTestTypeDescResponse> getAll(String language);
}
