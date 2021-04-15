package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.repository.SettingsRepository;
import at.rtr.rmbt.service.ApplicationVersionService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ApplicationVersionServiceImplTest {
    private ApplicationVersionService applicationVersionService;

    @MockBean
    private SettingsRepository settingsRepository;

    @MockBean
    private Settings settings;

    @Before
    public void setUp() {
        applicationVersionService = new ApplicationVersionServiceImpl(settingsRepository);
        ReflectionTestUtils.setField(applicationVersionService, "branch", TestConstants.DEFAULT_GIT_BRANCH);
        ReflectionTestUtils.setField(applicationVersionService, "describe", TestConstants.DEFAULT_GIT_COMMIT_ID_DESCRIBE);
        ReflectionTestUtils.setField(applicationVersionService, "applicationHost", TestConstants.DEFAULT_APPLICATION_HOST);
    }

    @Test
    public void getApplicationVersion_whenCommonData_expectApplicationVersionResponse() {
        when(settingsRepository.findFirstByKeyAndLangIsNullOrKeyAndLangOrderByLang(Config.SYSTEM_UUID_KEY, Config.SYSTEM_UUID_KEY, StringUtils.EMPTY)).thenReturn(Optional.of(settings));
        when(settings.getValue()).thenReturn(TestConstants.DEFAULT_SYSTEM_UUID_VALUE);

        var response = applicationVersionService.getApplicationVersion();

        assertEquals(TestConstants.DEFAULT_APPLICATION_HOST, response.getHost());
        assertEquals(TestConstants.DEFAULT_CONTROL_SERVER_VERSION, response.getVersion());
        assertEquals(TestConstants.DEFAULT_SYSTEM_UUID_VALUE, response.getSystemUUID());
    }
}
