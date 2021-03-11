package at.rtr.rmbt.service;

import at.rtr.rmbt.response.QosTestTypeDescResponse;

import java.util.List;

public interface QosTestTypeDescService {

    List<QosTestTypeDescResponse> getAll(String language);
}
