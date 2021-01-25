package com.rtr.nettest.service.impl;

import com.rtr.nettest.model.LoopModeSettings;
import com.rtr.nettest.repository.LoopModeSettingsRepository;
import com.rtr.nettest.service.LoopModeSettingsService;
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
