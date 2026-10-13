package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.repository.SettingsRepository;
import at.rtr.rmbt.service.ApplicationVersionService;
import at.rtr.rmbt.service.SettingsCache;

//no inspection
import at.rtr.rmbt.Version;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ApplicationVersionServiceImplTest {
    private ApplicationVersionService applicationVersionService;

    @MockitoBean
    private SettingsRepository settingsRepository;

    private SettingsCache settingsCache;

    @SuppressWarnings("unchecked")
    private final ObjectProvider<RedisConnectionFactory> redisConnectionFactory = mock(ObjectProvider.class);

    @Before
    public void setUp() {
        settingsCache = new SettingsCache(settingsRepository);
        applicationVersionService = new ApplicationVersionServiceImpl(settingsCache, redisConnectionFactory);
        ReflectionTestUtils.setField(applicationVersionService, "applicationHost", TestConstants.DEFAULT_APPLICATION_HOST);
        ReflectionTestUtils.setField(applicationVersionService, "activeProfile", "default"); // Set default profile if needed
    }

    @Test
    public void getApplicationVersion_whenCommonData_expectApplicationVersionResponse() {
        mockSystemUuidSetting();

        var response = applicationVersionService.getApplicationVersion();

        assertEquals(TestConstants.DEFAULT_APPLICATION_HOST, response.getHost());
        assertEquals(TestConstants.DEFAULT_APPLICATION_HOST, response.getHost());

        String expectedVersion = ApplicationVersionServiceImpl.formatVersion(
                Version.DESCRIBE, Version.BRANCH, Version.BUILD_TIME);
        assertEquals(expectedVersion, response.getVersion());

        assertEquals(TestConstants.DEFAULT_SYSTEM_UUID_VALUE, response.getSystemUUID());
    }

    @Test
    public void getApplicationVersion_withCustomProfile_expectProfileInResponse() {
        // Set a custom active profile
        ReflectionTestUtils.setField(applicationVersionService, "activeProfile", "test-profile");

        mockSystemUuidSetting();

        var response = applicationVersionService.getApplicationVersion();

        assertEquals("test-profile", response.getProfile());
    }

    @Test
    public void getApplicationVersion_whenRedisReachable_expectCacheRedis() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisConnection connection = mock(RedisConnection.class);
        when(redisConnectionFactory.getIfAvailable()).thenReturn(factory);
        when(factory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenReturn("PONG");

        assertEquals("redis", applicationVersionService.getApplicationVersion().getCache());
    }

    @Test
    public void getApplicationVersion_whenRedisNotConfigured_expectCacheNone() {
        // getIfAvailable() returns null for the unstubbed provider -> no Redis configured.
        assertEquals("none", applicationVersionService.getApplicationVersion().getCache());
    }

    @Test
    public void getApplicationVersion_whenRedisUnreachable_expectCacheNone() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        when(redisConnectionFactory.getIfAvailable()).thenReturn(factory);
        when(factory.getConnection()).thenThrow(new RuntimeException("connection refused"));

        assertEquals("none", applicationVersionService.getApplicationVersion().getCache());
    }

    private void mockSystemUuidSetting() {
        when(settingsRepository.findAll()).thenReturn(List.of(
                Settings.builder().id(1L).key(Config.SYSTEM_UUID_KEY).value(TestConstants.DEFAULT_SYSTEM_UUID_VALUE).build()));
        settingsCache.refresh();
    }
}