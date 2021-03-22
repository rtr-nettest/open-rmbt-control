package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.DataCollectorResponse;
import at.rtr.rmbt.response.IpResponse;
import at.rtr.rmbt.service.RequestDataCollectorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api("Request data collector")
@RestController
@RequiredArgsConstructor
public class RequestDataCollectorController {

    private final RequestDataCollectorService requestDataCollectorService;

    @ApiOperation(value = "Request data collector")
    @GetMapping(URIConstants.REQUEST_DATA_COLLECTOR)
    public DataCollectorResponse getDataCollectorResponse(HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return requestDataCollectorService.getDataCollectorResponse(httpServletRequest, headers);
    }

    @ApiOperation(value = "Get ip from request")
    @PostMapping(URIConstants.IP)
    public IpResponse getClientIpVersion(HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return requestDataCollectorService.getIpVersion(httpServletRequest, headers);
    }
}
