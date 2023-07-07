package at.rtr.rmbt.service;

import at.rtr.rmbt.response.DataCollectorResponse;
import at.rtr.rmbt.response.IpResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface RequestDataCollectorService {
    DataCollectorResponse getDataCollectorResponse(HttpServletRequest request, Map<String, String> headers);

    IpResponse getIpVersion(HttpServletRequest request, Map<String, String> headers);
}
