package at.rtr.rmbt.service;

import at.rtr.rmbt.model.LoopModeSettings;

import java.util.Optional;
import java.util.UUID;

/**
 * Loop mode settings service interface.
 */
public interface LoopModeSettingsService {
    /**
     * Save.
     *
     * @param loopModeSettings the Loop mode settings
     * @return the result
     */
    LoopModeSettings save(LoopModeSettings loopModeSettings);

    // returns true if loop UUID exists, false otherwise
    /**
     * Exists by loop uuid.
     *
     * @param loopUuid the Loop uuid
     * @return the result
     */
    @SuppressWarnings("unused")
    boolean existsByLoopUuid(UUID loopUuid);

    // returns true if combination of loop UUID and client UUID exists, false otherwise
    /**
     * Exists by loop uuid and client uuid.
     *
     * @param loopUuid the Loop uuid
     * @param clientUuid the Client uuid
     * @return the result
     */
    boolean existsByLoopUuidAndClientUuid(UUID loopUuid, UUID clientUuid);

    // gets the highest test_counter for a loop_uuid
    /**
     * Find max test counter by loop uuid.
     *
     * @param loopUuid the Loop uuid
     * @return the result
     */
    Optional<Integer> findMaxTestCounterByLoopUuid(UUID loopUuid);
}
