package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.RtrClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Client repository interface.
 */
@Repository(ClientRepository.NAME)
public interface ClientRepository extends JpaRepository<RtrClient, Long> {
    String NAME = "RtrClientRepository";

    /**
     * Find by uuid.
     *
     * @param uuid the Uuid
     * @return the result
     */
    Optional<RtrClient> findByUuid(UUID uuid);

    @Query(value = "SELECT rmbt_get_sync_code(:uuid) AS code", nativeQuery = true)
    Optional<String> getSyncCode(UUID uuid);

    Optional<RtrClient> getClientBySyncCode(String syncCode);

    Optional<RtrClient> getRtrClientByUuid(UUID uuid);

    /**
     * List synced clients by client uid and sync group id.
     *
     * @param clientUid the Client uid
     * @param syncGroupId the Sync group id
     * @return the result
     */
    @Query(value = "SELECT :clientUid UNION SELECT uid FROM client WHERE sync_group_id = :syncGroupId ", nativeQuery = true)
    List<Long> listSyncedClientsByClientUidAndSyncGroupId(Long clientUid, Integer syncGroupId);

    /**
     * Update sync group id by two uids.
     *
     * @param syncGroupId the Sync group id
     * @param clientUidBySyncCode the Client uid by sync code
     * @param clientUidByUUID the Client uid by UUID
     * @return the result
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE RtrClient c SET c.syncGroupId = :syncGroupId WHERE c.uid = :clientUidBySyncCode OR c.uid = :clientUidByUUID")
    int updateSyncGroupIdByTwoUids(Integer syncGroupId, Long clientUidBySyncCode, Long clientUidByUUID);

    /**
     * Update sync group id by uid.
     *
     * @param syncGroupId the Sync group id
     * @param clientUidByUUID the Client uid by UUID
     * @return the result
     */
    @Modifying
    @Query(value = "UPDATE client SET sync_group_id = :syncGroupId WHERE uid = :clientUidByUUID", nativeQuery = true)
    int updateSyncGroupIdByUid(Integer syncGroupId, Long clientUidByUUID);

    /**
     * Update sync group id by sync group id.
     *
     * @param syncGroup1 the Sync group 1
     * @param syncGroup2 the Sync group 2
     * @return the result
     */
    @Modifying
    @Query(value = "UPDATE client SET sync_group_id = :syncGroup1 WHERE sync_group_id = :syncGroup2", nativeQuery = true)
    int updateSyncGroupIdBySyncGroupId(Integer syncGroup1, Integer syncGroup2);
}
