package at.rtr.rmbt.repository;

import at.rtr.rmbt.dto.LteFrequencyDto;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.model.Test;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestRepository extends PagingAndSortingRepository<Test, Long>, CustomRepository<Test, Long> {
    @Query(value = "SELECT rmbt_set_provider_from_as(:testUid)", nativeQuery = true)
    String getRmbtSetProviderFromAs(Long testUid);

    @Query(value = "SELECT rmbt_get_next_test_slot(:testUid)", nativeQuery = true)
    Integer getRmbtNextTestSlot(Long testUid);

    @Query(value = "SELECT DISTINCT group_name" +
            " FROM test t" +
            " JOIN network_type nt ON t.network_type=nt.uid " +
            "WHERE t.deleted = false" +
            " AND t.status = 'FINISHED' " +
            " AND t.client_id IN :clientIds" +
            " AND group_name IS NOT NULL " +
            "ORDER BY group_name;", nativeQuery = true)
    List<String> getDistinctGroupNameByClientIdIn(List<Long> clientIds);


    @Query(value = "SELECT DISTINCT COALESCE(adm.fullname, t.model) model"
            + " FROM test t"
            + " LEFT JOIN device_map adm ON adm.codename=t.model " +
            "WHERE t.client_id IN :clientIds" +
            " AND t.deleted = false" +
            " AND t.implausible = false" +
            " AND t.status = 'FINISHED' " +
            "ORDER BY model ASC", nativeQuery = true)
    List<String> getDistinctModelByClientIdIn(List<Long> clientIds);

    Page<Test> findAllByRadioCellIsNotEmptyAndNetworkTypeNotIn(Pageable pageable, List<Integer> networkTypes);

    // JPQL (not native): lets Hibernate alias columns and join-fetch the eager TestLocation
    // association by position. A native "SELECT * FROM test" forces Hibernate to read the
    // associated test_location columns by name from the test result set, which fails.
    @Query("SELECT t FROM Test t WHERE t.uuid = :testUUID AND (t.status IS NULL OR t.status IN :testStatuses)")
    Optional<Test> findByUuidAndStatusesIn(UUID testUUID, Collection<TestStatus> testStatuses);

    // JPQL + @Lock instead of native "... for update": same reason as above (avoid the
    // by-name inline read of eager TestLocation/TestNdt columns from the test result set).
    // Hibernate emits "for update of <root>" so the lock applies only to the test row.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Test t WHERE t.uuid = :testUUID AND (t.status IS NULL OR t.status IN :testStatuses)")
    Optional<Test> findByUuidAndStatusesInLocked(UUID testUUID, Collection<TestStatus> testStatuses);

    @Query("SELECT t FROM Test t WHERE t.deleted = false AND t.implausible = false AND t.uuid = :testUUID AND (t.status IS NULL OR t.status IN :testStatuses)")
    Optional<Test> findByUuidAndStatusesInAndActive(UUID testUUID, Collection<TestStatus> testStatuses);

    // JPQL (was native "SELECT * FROM test"): returns, per server, the test(s) with the latest time.
    @Query("SELECT t FROM Test t WHERE t.testServer.uid IN :serverIds " +
            "AND t.time = (SELECT MAX(t2.time) FROM Test t2 WHERE t2.testServer = t.testServer)")
    List<Test> findLastTestByServerIdIn(Collection<Long> serverIds);

    @Query("SELECT t FROM Test t WHERE t.testServer.uid IN :serverIds AND t.status IN :testStatuses " +
            "AND t.time = (SELECT MAX(t2.time) FROM Test t2 " +
            "              WHERE t2.testServer = t.testServer AND t2.status IN :testStatuses)")
    List<Test> findLastSuccessTestByServerIdInAndStatusIn(Collection<Long> serverIds, Collection<TestStatus> testStatuses);

    Optional<Test> findByUuid(UUID testUUID);

    @Query("select t from Test t where t.uuid = :uuid or t.openTestUuid = :uuid")
    Optional<Test> findByUuidOrOpenTestUuid(UUID uuid);

    Optional<Test> findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(UUID uuid);

    Optional<Test> findByUuidAndImplausibleIsFalseAndDeletedIsFalse(UUID uuid);

    Optional<Test> findByOpenTestUuidAndClientUuidAndImplausibleIsFalseAndDeletedIsFalse(UUID uuid, UUID clientId);

    Optional<Test> findByTokenAndImplausibleIsFalseAndDeletedIsFalse(String token);

    @Query(value = "SELECT DISTINCT new at.rtr.rmbt.dto.LteFrequencyDto(r.channelNumber, r.technology) " +
            "  FROM RadioCell r" +
            "  WHERE r.test.openTestUuid = :openTestUUID AND r.active = true AND NOT r.technology = 'WLAN'")
    List<LteFrequencyDto> findLteFrequencyByOpenTestUUID(UUID openTestUUID);
}
