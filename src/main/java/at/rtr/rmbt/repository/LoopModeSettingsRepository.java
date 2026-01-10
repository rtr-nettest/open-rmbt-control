package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.LoopModeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoopModeSettingsRepository extends JpaRepository<LoopModeSettings, Long> {

    // Spring Data JPA will implement it automatically
    boolean existsByLoopUuid(UUID loopUuid);

    // Spring will generate query: WHERE loop_uuid = ? AND client_uuid = ?
    boolean existsByLoopUuidAndClientUuid(UUID loopUuid, UUID clientUuid);

    // custom query method
    @Query("SELECT MAX(lms.testCounter) FROM LoopModeSettings lms WHERE lms.loopUuid = :loopUuid")
    Optional<Integer> findMaxTestCounterByLoopUuid(@Param("loopUuid") UUID loopUuid);

}