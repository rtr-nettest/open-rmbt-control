package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.QosTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QosTestResultRepository extends JpaRepository<QosTestResult, Long> {

    @Query(value = "SELECT qtr FROM QosTestResult qtr "
            + " JOIN fetch qtr.qosTestObjective WHERE qtr.testUid = :qosTestObjectiveUid AND qtr.deleted = false and qtr.implausible = false")
    List<QosTestResult> findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(Long qosTestObjectiveUid);
}
