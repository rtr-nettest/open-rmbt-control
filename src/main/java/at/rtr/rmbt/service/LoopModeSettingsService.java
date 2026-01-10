package at.rtr.rmbt.service;

import at.rtr.rmbt.model.LoopModeSettings;

import java.util.Optional;
import java.util.UUID;

public interface LoopModeSettingsService {
    LoopModeSettings save(LoopModeSettings loopModeSettings);

    // returns true if loop UUID exists, false otherwise
    @SuppressWarnings("unused")
    boolean existsByLoopUuid(UUID loopUuid);

    // returns true if combination of loop UUID and client UUID exists, false otherwise
    boolean existsByLoopUuidAndClientUuid(UUID loopUuid, UUID clientUuid);

    // gets the highest test_counter for a loop_uuid
    Optional<Integer> findMaxTestCounterByLoopUuid(UUID loopUuid);
}
