package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.QosTestResultMapper;
import at.rtr.rmbt.model.QosTestResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static at.rtr.rmbt.response.QosMeasurementsResponse.QosTestResultItem;

@Component
@RequiredArgsConstructor
public class QosTestResultMapperImpl implements QosTestResultMapper {

    private final ObjectMapper objectMapper;

    @Override
    public QosTestResultItem toQosTestResultItem(QosTestResult qosTestResult, boolean isOpenTestUuid) {
        QosTestResultItem.QosTestResultItemBuilder builder = QosTestResultItem.builder()
            .uid(qosTestResult.getUid())
            .testType(qosTestResult.getQosTestObjective().getTestType())
            .result(getResult(qosTestResult))
            .testDesc(qosTestResult.getTestDescription())
            .successCount(qosTestResult.getSuccessCount())
            .failureCount(qosTestResult.getFailureCount())
            .testSummary(qosTestResult.getTestSummary())
            .testResultKeys(qosTestResult.getResultKeyMap().keySet())
            .testResultKeyMap(qosTestResult.getResultKeyMap());

        if (!isOpenTestUuid) {
            builder.nnTestUid(qosTestResult.getQosTestObjective().getUid())
                .qosTestUid(qosTestResult.getQosTestObjective().getUid())
                .testUid(qosTestResult.getTestUid());
        }

        return builder.build();
    }

    private Map<String, Object> getResult(QosTestResult qosTestResult) {
        try {
            return objectMapper.readValue(qosTestResult.getResult(), new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
