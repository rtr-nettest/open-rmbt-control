package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.SyncGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Sync group repository interface.
 */
public interface SyncGroupRepository extends JpaRepository<SyncGroup, Integer> {

    /**
     * Delete by sync group id.
     *
     * @param syncGroup2 the Sync group 2
     * @return the result
     */
    @Modifying
    @Query(value = "DELETE FROM sync_group WHERE uid = :syncGroup2", nativeQuery = true)
    int deleteBySyncGroupId(Integer syncGroup2);
}
