package at.rtr.rmbt.controller;

import at.rtr.rmbt.request.QosMeasurementsRequest;
import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosMeasurementsResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

import static at.rtr.rmbt.constant.URIConstants.*;

@Tag(name = "Qos measurement")
@RestController
@RequiredArgsConstructor
public class QosMeasurementController {

    private final QosMeasurementService qosMeasurementService;

    @Operation(summary = "Provide parameters for qos measurements.")
    @PostMapping(MEASUREMENT_QOS_REQUEST)
    public MeasurementQosResponse provideMeasurementQosParameters(HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        return qosMeasurementService.getQosParameters(httpServletRequest, headers);
    }

    @Operation(summary = "Get QoS test results")
    @PostMapping(MEASUREMENT_QOS_RESULT)
    public QosMeasurementsResponse getQosTestResults(@Validated @RequestBody QosMeasurementsRequest request) {
        return qosMeasurementService.getQosResult(request.getTestUuid(), request.getLanguage(), request.getCapabilities());
    }

    @Operation(summary = "Save QoS test results")
    @PostMapping(RESULT_QOS_URL)
    public ErrorResponse saveQosMeasurementResult(@RequestBody QosResultRequest qosResultRequest) {
        return qosMeasurementService.saveQosMeasurementResult(qosResultRequest);
    }

    @PostMapping(value = {QOS_BY_OPEN_TEST_UUID_AND_LANGUAGE, QOS_BY_OPEN_TEST_UUID})
    public QosMeasurementsResponse evaluateQosByOpenTestUUID(@PathVariable(name = "open_test_uuid") UUID openTestUUID, @PathVariable(name = "lang", required = false) String lang) {
        return qosMeasurementService.evaluateQosByOpenTestUUID(openTestUUID, lang);
    }

    @GetMapping(value = {QOS_BY_OPEN_TEST_UUID_AND_LANGUAGE, QOS_BY_OPEN_TEST_UUID})
    public QosMeasurementsResponse getQosByOpenTestUUID(@PathVariable(name = "open_test_uuid") UUID openTestUUID, @PathVariable(name = "lang", required = false) String lang) {
        return qosMeasurementService.evaluateQosByOpenTestUUID(openTestUUID, lang);
    }

    @Operation(summary = "Save QoS test results")
    @GetMapping(RESULT_QOS_URL)
    public ErrorResponse saveQosMeasurementResultGet(@RequestBody QosResultRequest qosResultRequest) {
        return qosMeasurementService.saveQosMeasurementResult(qosResultRequest);
    }
}
