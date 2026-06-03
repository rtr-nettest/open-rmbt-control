package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.LoopModeSettings;
import at.rtr.rmbt.repository.LoopModeSettingsRepository;
import at.rtr.rmbt.service.LoopModeSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Loop mode settings service impl class.
 */
@Service
public class LoopModeSettingsServiceImpl implements LoopModeSettingsService {

    final private LoopModeSettingsRepository loopModeSettingsRepository;

    /**
     * Creates a new LoopModeSettingsServiceImpl instance.
     *
     * @param loopModeSettingsRepository the Loop mode settings repository
     */
    @Autowired
    public LoopModeSettingsServiceImpl(LoopModeSettingsRepository loopModeSettingsRepository) {
        this.loopModeSettingsRepository = loopModeSettingsRepository;
    }

    /**
     * Save.
     *
     * @param loopModeSettings the Loop mode settings
     * @return the result
     */
    @Override
    public LoopModeSettings save(LoopModeSettings loopModeSettings) {
        return loopModeSettingsRepository.save(loopModeSettings);
    }

    /**
     * Exists by loop uuid.
     *
     * @param loopUuid the Loop uuid
     * @return the result
     */
    @Override
    public boolean existsByLoopUuid(UUID loopUuid) {
        // Simple implementation: check if any record with this loopUuid exists
        return loopModeSettingsRepository.existsByLoopUuid(loopUuid);
    }

    /**
     * Exists by loop uuid and client uuid.
     *
     * @param loopUuid the Loop uuid
     * @param clientUuid the Client uuid
     * @return the result
     */
    @Override
    public boolean existsByLoopUuidAndClientUuid(UUID loopUuid, UUID clientUuid) {
        // Simple implementation: check if any record with this loopUuid exists
        return loopModeSettingsRepository.existsByLoopUuidAndClientUuid(loopUuid, clientUuid);
    }

    /**
     * Find max test counter by loop uuid.
     *
     * @param loopUuid the Loop uuid
     * @return the result
     */
    @Override
    public Optional<Integer> findMaxTestCounterByLoopUuid(UUID loopUuid) {
        return loopModeSettingsRepository.findMaxTestCounterByLoopUuid(loopUuid);
    }


}
