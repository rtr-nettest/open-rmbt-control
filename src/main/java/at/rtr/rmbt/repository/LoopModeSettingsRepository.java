package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.LoopModeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Loop mode settings repository interface.
 */
@Repository
public interface LoopModeSettingsRepository extends JpaRepository<LoopModeSettings, Long> {

    // Spring Data JPA will implement it automatically
    /**
     * Exists by loop uuid.
     *
     * @param loopUuid the Loop uuid
     * @return the result
     */
    boolean existsByLoopUuid(UUID loopUuid);

    // Spring will generate query: WHERE loop_uuid = ? AND client_uuid = ?
    /**
     * Exists by loop uuid and client uuid.
     *
     * @param loopUuid the Loop uuid
     * @param clientUuid the Client uuid
     * @return the result
     */
    boolean existsByLoopUuidAndClientUuid(UUID loopUuid, UUID clientUuid);

    // custom query method
    /**
     * Find max test counter by loop uuid.
     *
     * @param loopUuid the Loop uuid
     * @return the result
     */
    @Query("SELECT MAX(lms.testCounter) FROM LoopModeSettings lms WHERE lms.loopUuid = :loopUuid")
    Optional<Integer> findMaxTestCounterByLoopUuid(@Param("loopUuid") UUID loopUuid);

}