package at.rtr.rmbt.service;

import at.rtr.rmbt.request.ResultRequest;

import javax.servlet.http.HttpServletRequest;

public interface ResultService {

    void processResultRequest(HttpServletRequest httpServletRequest, ResultRequest resultRequest);
}
