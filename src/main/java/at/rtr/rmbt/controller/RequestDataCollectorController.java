package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.DataCollectorResponse;
import at.rtr.rmbt.response.IpResponse;
import at.rtr.rmbt.service.RequestDataCollectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Tag(name = "Request data collector")
@RestController
@RequiredArgsConstructor
public class RequestDataCollectorController {

    private final RequestDataCollectorService requestDataCollectorService;

    @Operation(summary = "Request data collector")
    @GetMapping(URIConstants.REQUEST_DATA_COLLECTOR)
    public DataCollectorResponse getDataCollectorResponse(HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return requestDataCollectorService.getDataCollectorResponse(httpServletRequest, headers);
    }

    @Operation(summary = "Get ip from request")
    @PostMapping(URIConstants.IP)
    public IpResponse getClientIpVersion(HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return requestDataCollectorService.getIpVersion(httpServletRequest, headers);
    }
}
