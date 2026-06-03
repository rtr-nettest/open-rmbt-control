package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.repository.QosTestResultRepository;
import at.rtr.rmbt.service.QosTestResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Qos test result service impl class.
 */
@Service
@RequiredArgsConstructor
public class QosTestResultServiceImpl implements QosTestResultService {

    private final QosTestResultRepository qosTestResultRepository;

    /**
     * Save.
     *
     * @param testResult the Test result
     */
    @Override
    public void save(QosTestResult testResult) {
        qosTestResultRepository.save(testResult);
    }

    /**
     * List by test uid.
     *
     * @param uid the Uid
     * @return the result
     */
    @Override
    public List<QosTestResult> listByTestUid(Long uid) {
        return qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(uid);
    }
}
