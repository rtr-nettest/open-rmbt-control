package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.repository.QosTestResultRepository;
import at.rtr.rmbt.service.QosTestResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QosTestResultServiceImpl implements QosTestResultService {

    private final QosTestResultRepository qosTestResultRepository;

    @Override
    public void save(QosTestResult testResult) {
        qosTestResultRepository.save(testResult);
    }

    @Override
    public List<QosTestResult> listByTestUid(Long uid) {
        return qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(uid);
    }
}
