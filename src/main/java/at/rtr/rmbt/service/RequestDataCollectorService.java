package at.rtr.rmbt.service;

import at.rtr.rmbt.response.DataCollectorResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface RequestDataCollectorService {
    DataCollectorResponse getDataCollectorResponse(HttpServletRequest request, Map<String, String> headers);
}
