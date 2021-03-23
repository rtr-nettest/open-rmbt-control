package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api("Qos measurement")
@RestController
@RequiredArgsConstructor
public class QosMeasurementController {

    private final QosMeasurementService qosMeasurementService;

    @ApiOperation("Provide parameters for qos measurements.")
    @PostMapping(URIConstants.MEASUREMENT_QOS_REQUEST)
    public MeasurementQosResponse provideMeasurementQosParameters(HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return qosMeasurementService.getQosParameters(httpServletRequest, headers);
    }
}
