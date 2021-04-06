package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.LoopModeSettings;
import at.rtr.rmbt.repository.LoopModeSettingsRepository;
import at.rtr.rmbt.service.LoopModeSettingsService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoopModeSettingsServiceImplTest {
    private final LoopModeSettingsRepository loopModeSettingsRepository = mock(LoopModeSettingsRepository.class);
    private final LoopModeSettingsService loopModeSettingsService = new LoopModeSettingsServiceImpl(loopModeSettingsRepository);

    @Test
    void save_whenCommonData_expectInvokeRepository() {
        LoopModeSettings loopModeSettings = new LoopModeSettings();
        loopModeSettingsService.save(loopModeSettings);
        verify(loopModeSettingsRepository).save(loopModeSettings);
    }
}
