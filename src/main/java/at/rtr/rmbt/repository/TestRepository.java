package at.rtr.rmbt.repository;

import at.rtr.rmbt.dto.LteFrequencyDto;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.model.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT * FROM test WHERE uuid = :testUUID AND (status is null or status in (:testStatuses)) for update", nativeQuery = true)
    Optional<Test> findByUuidAndStatusesInLocked(UUID testUUID, Collection<String> testStatuses);

    @Query("SELECT t FROM Test t WHERE t.deleted = false AND t.implausible = false AND t.uuid = :testUUID AND (t.status IS NULL OR t.status IN :testStatuses)")
    Optional<Test> findByUuidAndStatusesInAndActive(UUID testUUID, Collection<TestStatus> testStatuses);

    @Query(
            value = "SELECT * " +
                    "FROM test t " +
                    "INNER JOIN (SELECT server_id, MAX(time) AS MaxDateTime\n" +
                    "    FROM test " +
                    "    WHERE server_id in (:serverIds) " +
                    "    GROUP BY server_id " +
                    "    ) groupedTest " +
                    "ON t.server_id = groupedTest.server_id " +
                    "AND t.time = groupedTest.MaxDateTime",
            nativeQuery = true
    )
    List<Test> findLastTestByServerIdIn(Collection<Long> serverIds);

    @Query(
            value = "SELECT * " +
                    "FROM test t " +
                    "INNER JOIN (SELECT server_id, MAX(time) AS MaxDateTime\n" +
                    "    FROM test " +
                    "    WHERE server_id in (:serverIds) and status in (:testStatuses)" +
                    "    GROUP BY server_id " +
                    "    ) groupedTest " +
                    "ON t.server_id = groupedTest.server_id " +
                    "AND t.time = groupedTest.MaxDateTime",
            nativeQuery = true
    )
    List<Test> findLastSuccessTestByServerIdInAndStatusIn(Collection<Long> serverIds, Collection<String> testStatuses);

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
