package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.repository.QosTestResultRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class QosTestResultServiceImplTest {
    private final QosTestResultRepository qosTestResultRepository = mock(QosTestResultRepository.class);
    private final QosTestResultServiceImpl service = new QosTestResultServiceImpl(qosTestResultRepository);

    @Test
    void save_whenCommonData_expectCallRepository() {
        QosTestResult qosTestResult = new QosTestResult();
        qosTestResult.setUid(1L);
        service.save(qosTestResult);

        verify(qosTestResultRepository).save(qosTestResult);
    }

    @Test
    void listByTestUid_whenCommonData_expectNoErrors() {
        QosTestResult qosTestResult = new QosTestResult();
        qosTestResult.setTestUid(1L);
        List<QosTestResult> expected = List.of(qosTestResult);

        when(qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(1L)).thenReturn(expected);

        List<QosTestResult> result = service.listByTestUid(qosTestResult.getTestUid());

        assertEquals(expected, result);
    }
}
