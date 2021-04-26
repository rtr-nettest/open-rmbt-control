package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.RtrClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository(ClientRepository.NAME)
public interface ClientRepository extends JpaRepository<RtrClient, Long> {
    String NAME = "RtrClientRepository";

    Optional<RtrClient> findByUuid(UUID uuid);

    @Query(value = "SELECT rmbt_get_sync_code(:uuid) AS code", nativeQuery = true)
    Optional<String> getSyncCode(UUID uuid);

    Optional<RtrClient> getClientBySyncCode(String syncCode);

    Optional<RtrClient> getRtrClientByUuid(UUID uuid);

    @Query(
        value = "SELECT cc.* FROM client c " +
            "INNER JOIN client cc ON c.sync_group_id = cc.sync_group_id " +
            "WHERE c.uid = :clientUid",
        nativeQuery = true
    )
    List<RtrClient> listSyncedClientsByClientUid(Long clientUid);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE RtrClient c SET c.syncGroupId = :syncGroupId WHERE c.uid = :clientUidBySyncCode OR c.uid = :clientUidByUUID")
    int updateSyncGroupIdByTwoUids(Integer syncGroupId, Long clientUidBySyncCode, Long clientUidByUUID);

    @Modifying
    @Query(value = "UPDATE client SET sync_group_id = :syncGroupId WHERE uid = :clientUidByUUID", nativeQuery = true)
    int updateSyncGroupIdByUid(Integer syncGroupId, Long clientUidByUUID);

    @Modifying
    @Query(value = "UPDATE client SET sync_group_id = :syncGroup1 WHERE sync_group_id = :syncGroup2", nativeQuery = true)
    int updateSyncGroupIdBySyncGroupId(Integer syncGroup1, Integer syncGroup2);
}
