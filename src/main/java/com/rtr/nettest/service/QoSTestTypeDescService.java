package com.rtr.nettest.service;

import com.rtr.nettest.response.QoSTestTypeDescResponse;

import java.util.List;

public interface QoSTestTypeDescService {

    List<QoSTestTypeDescResponse> getAll(String language);
}
