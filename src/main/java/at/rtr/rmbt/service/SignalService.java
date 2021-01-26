package at.rtr.rmbt.service;

import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.response.SignalResponse;

import javax.servlet.http.HttpServletRequest;

public interface SignalService {

    SignalResponse registerSignal(SignalRequest signalRequest, HttpServletRequest httpServletRequest);
}
