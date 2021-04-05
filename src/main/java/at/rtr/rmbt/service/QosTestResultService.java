package at.rtr.rmbt.service;

import at.rtr.rmbt.model.QosTestResult;

import java.util.List;

public interface QosTestResultService {
    void save(QosTestResult testResult);

    List<QosTestResult> listByTestUid(Long uid);
}
