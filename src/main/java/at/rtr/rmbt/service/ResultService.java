package at.rtr.rmbt.service;

import at.rtr.rmbt.request.ResultRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ResultService {

    void processResultRequest(HttpServletRequest httpServletRequest, ResultRequest resultRequest, Map<String, String> headers);
}
