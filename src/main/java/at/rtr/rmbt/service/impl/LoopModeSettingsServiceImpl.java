package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.LoopModeSettings;
import at.rtr.rmbt.repository.LoopModeSettingsRepository;
import at.rtr.rmbt.service.LoopModeSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoopModeSettingsServiceImpl implements LoopModeSettingsService {

    private LoopModeSettingsRepository loopModeSettingsRepository;

    @Autowired
    public LoopModeSettingsServiceImpl(LoopModeSettingsRepository loopModeSettingsRepository) {
        this.loopModeSettingsRepository = loopModeSettingsRepository;
    }

    @Override
    public LoopModeSettings save(LoopModeSettings loopModeSettings) {
        return loopModeSettingsRepository.save(loopModeSettings);
    }
}
