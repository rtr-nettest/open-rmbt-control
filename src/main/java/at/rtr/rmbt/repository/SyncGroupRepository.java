package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.SyncGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SyncGroupRepository extends JpaRepository<SyncGroup, Integer> {

    @Modifying
    @Query(value = "DELETE FROM sync_group WHERE uid = :syncGroup2", nativeQuery = true)
    int deleteBySyncGroupId(Integer syncGroup2);
}
