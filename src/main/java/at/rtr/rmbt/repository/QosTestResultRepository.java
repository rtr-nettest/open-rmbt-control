package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.QosTestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QosTestResultRepository extends JpaRepository<QosTestResult, Long> {
    List<QosTestResult> findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(Long qosTestObjectiveUid);
}
