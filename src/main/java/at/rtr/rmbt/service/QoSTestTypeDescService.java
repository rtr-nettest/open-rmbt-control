package at.rtr.rmbt.service;

import at.rtr.rmbt.response.QoSTestTypeDescResponse;

import java.util.List;

public interface QoSTestTypeDescService {

    List<QoSTestTypeDescResponse> getAll(String language);
}
