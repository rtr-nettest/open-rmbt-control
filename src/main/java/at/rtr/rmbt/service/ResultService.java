package at.rtr.rmbt.service;

import at.rtr.rmbt.request.ResultRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Result service interface.
 */
public interface ResultService {

    /**
     * Process result request.
     *
     * @param httpServletRequest the Http servlet request
     * @param resultRequest the Result request
     * @param headers the Headers
     */
    void processResultRequest(HttpServletRequest httpServletRequest, ResultRequest resultRequest, Map<String, String> headers);
}
