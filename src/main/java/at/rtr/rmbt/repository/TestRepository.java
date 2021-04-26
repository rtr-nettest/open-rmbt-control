package at.rtr.rmbt.repository;

import at.rtr.rmbt.dto.LteFrequencyDto;
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
    @Procedure("rmbt_set_provider_from_as")
    String getRmbtSetProviderFromAs(Long testUid);

    @Procedure("rmbt_get_next_test_slot")
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

    @Query(value = "SELECT * FROM test WHERE uuid = :testUUID AND (status is null or status in (:testStatuses))", nativeQuery = true)
    Optional<Test> findByUuidAndStatusesIn(UUID testUUID, Collection<String> testStatuses);

    @Query(value = "SELECT * FROM test WHERE deleted = false AND implausible = false AND uuid = :testUUID AND (status is null or status in (:testStatuses))", nativeQuery = true)
    Optional<Test> findByUuidAndStatusesInAndActive(UUID testUUID, Collection<String> testStatuses);

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

    @Query(value = "SELECT DISTINCT new at.rtr.rmbt.dto.LteFrequencyDto(r.channelNumber, r.technology) " +
            "  FROM RadioCell r" +
            "  WHERE r.test.openTestUuid = :openTestUUID AND r.active = true AND NOT r.technology = 'WLAN'")
    List<LteFrequencyDto> findLteFrequencyByOpenTestUUID(UUID openTestUUID);
}
