package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.mapper.QosTestResultMapper;
import at.rtr.rmbt.response.QosMeasurementsResponse.QosTestResultItem;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import static at.rtr.rmbt.TestFixtures.qosTestResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class QosTestResultMapperImplTest {
    private final QosTestResultMapper mapper = new QosTestResultMapperImpl(TestUtils.mapper);

    @Test
    void toQosTestResultItem_whenOpenTestUuid_expectExcludePrivateData() throws Exception {
        QosTestResultItem result = mapper.toQosTestResultItem(qosTestResult, true);

        assertEquals(qosTestResult.getUid(), result.getUid());
        assertEquals(qosTestResult.getQosTestObjective().getTestType(), result.getTestType());
        assertEquals(TestUtils.mapper.readValue(qosTestResult.getResult(), new TypeReference<>() {}), result.getResult());
        assertEquals(qosTestResult.getTestDescription(), result.getTestDesc());
        assertEquals(qosTestResult.getSuccessCount(), result.getSuccessCount());
        assertEquals(qosTestResult.getFailureCount(), result.getFailureCount());
        assertEquals(qosTestResult.getTestSummary(), result.getTestSummary());
        assertEquals(qosTestResult.getResultKeyMap().keySet(), result.getTestResultKeys());
        assertEquals(qosTestResult.getResultKeyMap(), result.getTestResultKeyMap());
        assertNull(result.getNnTestUid());
        assertNull(result.getQosTestUid());
        assertNull(result.getTestUid());
    }

    @Test
    void toQosTestResultItem_whenNotOpenTestUuid_expectIncludePrivateData() throws Exception {
        QosTestResultItem result = mapper.toQosTestResultItem(qosTestResult, false);

        assertEquals(qosTestResult.getUid(), result.getUid());
        assertEquals(qosTestResult.getQosTestObjective().getTestType(), result.getTestType());
        assertEquals(TestUtils.mapper.readValue(qosTestResult.getResult(), new TypeReference<>() {}), result.getResult());
        assertEquals(qosTestResult.getTestDescription(), result.getTestDesc());
        assertEquals(qosTestResult.getSuccessCount(), result.getSuccessCount());
        assertEquals(qosTestResult.getFailureCount(), result.getFailureCount());
        assertEquals(qosTestResult.getTestSummary(), result.getTestSummary());
        assertEquals(qosTestResult.getResultKeyMap().keySet(), result.getTestResultKeys());
        assertEquals(qosTestResult.getResultKeyMap(), result.getTestResultKeyMap());
        assertEquals(qosTestResult.getQosTestObjective().getUid(), result.getNnTestUid());
        assertEquals(qosTestResult.getQosTestObjective().getUid(), result.getQosTestUid());
        assertEquals(qosTestResult.getTestUid(), result.getTestUid());
    }
}
