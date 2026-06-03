package at.rtr.rmbt.service;

import at.rtr.rmbt.model.QosTestResult;

import java.util.List;

/**
 * Qos test result service interface.
 */
public interface QosTestResultService {
    /**
     * Save.
     *
     * @param testResult the Test result
     */
    void save(QosTestResult testResult);

    /**
     * List by test uid.
     *
     * @param uid the Uid
     * @return the result
     */
    List<QosTestResult> listByTestUid(Long uid);
}
