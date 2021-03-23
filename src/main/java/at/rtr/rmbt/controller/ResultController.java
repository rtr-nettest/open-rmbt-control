package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.service.ResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api("Result")
@RestController
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;

    @ApiOperation("Update measurements")
    @PostMapping(URIConstants.RESULT_URL)
    public ErrorResponse processResult(HttpServletRequest httpServletRequest,
                                       @RequestHeader Map<String, String> headers,
                                       @RequestBody ResultRequest resultRequest) {
        resultService.processResultRequest(httpServletRequest, resultRequest, headers);
        return ErrorResponse.empty();
    }
}
