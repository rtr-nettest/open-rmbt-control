package com.rtr.nettest.service;

import com.rtr.nettest.request.SignalRequest;
import com.rtr.nettest.response.SignalResponse;

import javax.servlet.http.HttpServletRequest;

public interface SignalService {

    SignalResponse registerSignal(SignalRequest signalRequest, HttpServletRequest httpServletRequest);
}
