package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.LoopModeSettings;
import at.rtr.rmbt.repository.LoopModeSettingsRepository;
import at.rtr.rmbt.service.LoopModeSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LoopModeSettingsServiceImpl implements LoopModeSettingsService {

    final private LoopModeSettingsRepository loopModeSettingsRepository;

    @Autowired
    public LoopModeSettingsServiceImpl(LoopModeSettingsRepository loopModeSettingsRepository) {
        this.loopModeSettingsRepository = loopModeSettingsRepository;
    }

    @Override
    public LoopModeSettings save(LoopModeSettings loopModeSettings) {
        return loopModeSettingsRepository.save(loopModeSettings);
    }

    @Override
    public boolean existsByLoopUuid(UUID loopUuid) {
        // Simple implementation: check if any record with this loopUuid exists
        return loopModeSettingsRepository.existsByLoopUuid(loopUuid);
    }

    @Override
    public boolean existsByLoopUuidAndClientUuid(UUID loopUuid, UUID clientUuid) {
        // Simple implementation: check if any record with this loopUuid exists
        return loopModeSettingsRepository.existsByLoopUuidAndClientUuid(loopUuid, clientUuid);
    }

    @Override
    public Optional<Integer> findMaxTestCounterByLoopUuid(UUID loopUuid) {
        return loopModeSettingsRepository.findMaxTestCounterByLoopUuid(loopUuid);
    }


}
