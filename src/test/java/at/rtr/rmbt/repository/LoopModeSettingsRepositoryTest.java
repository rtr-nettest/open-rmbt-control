package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.LoopModeSettings;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

import static at.rtr.rmbt.TestConstants.Database.CLIENT1_UUID;
import static at.rtr.rmbt.TestConstants.Database.TEST1_UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Ignore
public class LoopModeSettingsRepositoryTest extends AbstractRepositoryTest<LoopModeSettingsRepository> {

    @Test
    public void save_whenLoopModeSettingsNotExists_expectSaved() {
        LoopModeSettings loopModeSettings = new LoopModeSettings(
            null,
            TEST1_UUID,
            CLIENT1_UUID,
            4,
            0,
            10,
            1,
            UUID.randomUUID()
        );

        LoopModeSettings result = dao.save(loopModeSettings);

        assertNotNull(result.getUid());
    }
}
