package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.QosTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface QosTestResultRepository extends JpaRepository<QosTestResult, Long> {
    List<QosTestResult> findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(Long qosTestObjectiveUid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE qos_test_result SET success_count = :successCount, failure_count = :failureCount WHERE uid = :uid", nativeQuery = true)
    Integer updateCounters(Integer successCount, Integer failureCount, Long uid);
}
