package at.rtr.rmbt.controller;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.DataCollectorResponse;
import at.rtr.rmbt.response.IpResponse;
import at.rtr.rmbt.service.RequestDataCollectorService;
import at.rtr.rmbt.request.IpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;

// import java.io.IOException;
// import java.io.StringReader;
import java.util.Map;

@Tag(name = "Request data collector")
@RestController
@RequiredArgsConstructor
public class RequestDataCollectorController {

    private final RequestDataCollectorService requestDataCollectorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Request data collector")
    @GetMapping(URIConstants.REQUEST_DATA_COLLECTOR)
    public DataCollectorResponse getDataCollectorResponse(HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return requestDataCollectorService.getDataCollectorResponse(httpServletRequest, headers);
    }

    @Operation(summary = "Get ip from request")
    @PostMapping(value=URIConstants.IP, consumes = MediaType.APPLICATION_JSON_VALUE)
    public IpResponse getClientIpVersionJSON(@RequestBody IpRequest ipRequest, HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return requestDataCollectorService.getIpVersion(ipRequest, httpServletRequest, headers);
        }

    // work-around for obsolete clients
    /*
    @Operation(summary = "Get ip from request, needs to support plain/text ")
    @PostMapping(value = URIConstants.IP, consumes = MediaType.TEXT_PLAIN_VALUE)
    public IpResponse getClientIpVersionTXT(@RequestBody String ipRequestString, HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) throws IOException {

        @SuppressWarnings("path-traversal-sink")
        IpRequest ipRequest = objectMapper.readValue(new StringReader(ipRequestString), IpRequest.class);
        return requestDataCollectorService.getIpVersion(ipRequest, httpServletRequest, headers);
    }
    */

}
