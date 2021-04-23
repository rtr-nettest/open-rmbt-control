package at.rtr.rmbt.controller;

import at.rtr.rmbt.request.QosMeasurementsRequest;
import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosMeasurementsResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static at.rtr.rmbt.constant.URIConstants.*;

@Api("Qos measurement")
@RestController
@RequiredArgsConstructor
public class QosMeasurementController {

    private final QosMeasurementService qosMeasurementService;

    @ApiOperation("Provide parameters for qos measurements.")
    @PostMapping(MEASUREMENT_QOS_REQUEST)
    public MeasurementQosResponse provideMeasurementQosParameters(HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return qosMeasurementService.getQosParameters(httpServletRequest, headers);
    }

    @ApiOperation("Get QoS test results")
    @PostMapping(MEASUREMENT_QOS_RESULT)
    public QosMeasurementsResponse getQosTestResults(@Validated @RequestBody QosMeasurementsRequest request) {
        return qosMeasurementService.getQosResult(request.getTestUuid(), request.getLanguage(), request.getCapabilities());
    }

    @ApiOperation("Save QoS test results")
    @PostMapping(RESULT_QOS_URL)
    public ErrorResponse saveQosMeasurementResult(@RequestBody QosResultRequest qosResultRequest) {
        return qosMeasurementService.saveQosMeasurementResult(qosResultRequest);
    }

    @ApiOperation("Save QoS test results")
    @GetMapping(RESULT_QOS_URL)
    public ErrorResponse saveQosMeasurementResultGet(@RequestBody QosResultRequest qosResultRequest) {
        return qosMeasurementService.saveQosMeasurementResult(qosResultRequest);
    }
}
