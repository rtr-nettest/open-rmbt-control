package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.TestFixtures;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.enums.ClientType;
import at.rtr.rmbt.exception.NotSupportedClientVersionException;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.SettingsRepository;
import at.rtr.rmbt.request.AdminSettingsBodyRequest;
import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.request.settings.admin.update.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.response.settings.admin.update.*;
import at.rtr.rmbt.service.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static at.rtr.rmbt.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RtrSettingsServiceImplTest {

    private static List<Long> uidList = List.of(TestConstants.DEFAULT_UID);

    private RtrSettingsService rtrSettingsService;

    @MockBean
    private ClientTypeService clientTypeService;
    @MockBean
    private ClientService clientService;
    @MockBean
    private SettingsRepository settingsRepository;
    @MockBean
    private QosTestTypeDescService qosTestTypeDescService;
    @MockBean
    private TestService testService;
    @MockBean
    private TestServerService testServerService;
    @MockBean
    private UUIDGenerator uuidGenerator;

    @Mock
    private RtrSettingsRequest rtrSettingsRequest;
    @Mock
    private RtrClient rtrClient;
    @Mock
    private RtrClient savedRtrClient;
    @Mock
    private Clock clock;
    @Captor
    private ArgumentCaptor<RtrClient> clientArgumentCaptor;
    @Mock
    private AdminSettingsRequest adminSettingsRequest;
    @Mock
    private AdminSettingsBodyRequest adminSettingsBodyRequest;
    @Mock
    private Settings settings;
    @Captor
    private ArgumentCaptor<List<Settings>> settingsArgumentCaptor;
    @Mock
    private AdminUpdateSettingsRequest adminUpdateSettingsRequest;
    @Mock
    private AdminUpdateSettingsTermsAndConditionsRequest adminUpdateSettingsTermsAndConditionsRequest;
    @Mock
    private AdminUpdateSettingsUrlsRequest adminUpdateSettingsUrlsRequest;
    @Mock
    private AdminUpdateSettingsTestRequest adminUpdateSettingsTestRequest;
    @Mock
    private AdminUpdateSettingsSignalTestRequest adminUpdateSettingsSignalTestRequest;
    @Mock
    private AdminUpdateSettingsMapServerRequest adminUpdateSettingsMapServerRequest;

    private final ApplicationProperties applicationProperties = new ApplicationProperties(
            new ApplicationProperties.LanguageProperties(Set.of("en", "de"), "en"),
            Set.of("RMBT", "RMBTjs", "Open-RMBT", "RMBTws", "HW-PROBE"),
            "0.1.0 || 0.3.0 || ^1.0.0",
            1,
            2,
            3,
            10000,
            2000
    );

    @Before
    public void setUp() {
        clock = Clock.fixed(Instant.parse(TestConstants.DEFAULT_INSTANT_EXPECTED), ZoneId.of("UTC"));
        rtrSettingsService = new RtrSettingsServiceImpl(clientTypeService,
                clientService,
                settingsRepository,
                qosTestTypeDescService,
                testService,
                testServerService,
                uuidGenerator,
                clock,
                applicationProperties);
        ReflectionTestUtils.setField(rtrSettingsService, "branch", TestConstants.DEFAULT_GIT_BRANCH);
        ReflectionTestUtils.setField(rtrSettingsService, "describe", TestConstants.DEFAULT_GIT_COMMIT_ID_DESCRIBE);
    }

    @Test(expected = NotSupportedClientVersionException.class)
    public void getSettings_whenNotSupportedName_expectNotSupportedClientVersionException() {
        when(rtrSettingsRequest.getName()).thenReturn(TestConstants.DEFAULT_TEXT);
        rtrSettingsService.getSettings(rtrSettingsRequest);
    }

    @Test
    public void getSettings_whenAndroidPlatform_expectSettingsResponse() {
        var historyDevices = List.of(TestConstants.DEFAULT_HISTORY_DEVICE);
        var historyNetworks = List.of(TestConstants.DEFAULT_HISTORY_NETWORK);
        when(rtrSettingsRequest.getName()).thenReturn(TestConstants.DEFAULT_CLIENT_NAME);
        when(rtrSettingsRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(rtrSettingsRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_ANDROID_PLATFORM);
        when(rtrSettingsRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(rtrSettingsRequest.getTermsAndConditionsAcceptedVersion()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION);
        when(rtrClient.getTermsAndConditionsAcceptedVersion()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION - 1);
        when(clientService.getClientByUUID(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(rtrClient);
        when(clientService.saveClient(rtrClient)).thenReturn(savedRtrClient);
        when(savedRtrClient.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(savedRtrClient.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(TestConstants.DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(getDefaultSettings());
        when(qosTestTypeDescService.getAll(TestConstants.DEFAULT_LANGUAGE)).thenReturn(getQosTestTypeDescResponses());
        when(clientService.listSyncedClientsByClientUid(DEFAULT_UID)).thenReturn(List.of(TestFixtures.client));
        when(testService.getDeviceHistory(argThat(t -> t.containsAll(uidList)))).thenReturn(historyDevices);
        when(testService.getGroupNameByClientIds(argThat(t -> t.containsAll(uidList)))).thenReturn(historyNetworks);
        when(testServerService.getServers()).thenReturn(getServerResponseList());
        when(testServerService.getServersWs()).thenReturn(getServerWsResponseList());
        when(testServerService.getServersQos()).thenReturn(getServerQosResponseList());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        assertEquals(TestConstants.DEFAULT_CLIENT_UUID, response.getSettings().get(0).getUuid());
        assertEquals(getAndroidTermAndConditionsResponse(), response.getSettings().get(0).getTermAndConditionsResponse());
        assertEquals(getQosTestTypeDescResponses(), response.getSettings().get(0).getQosTestTypeDescResponse());
        assertEquals(historyDevices, response.getSettings().get(0).getHistory().getDevices());
        assertEquals(historyNetworks, response.getSettings().get(0).getHistory().getNetworks());
        assertEquals(getMapServerResponse(), response.getSettings().get(0).getMapServerResponse());
        assertEquals(getServerResponseList(), response.getSettings().get(0).getServers());
        assertEquals(getServerWsResponseList(), response.getSettings().get(0).getServerWSResponseList());
        assertEquals(getServerQosResponseList(), response.getSettings().get(0).getServerQosResponseList());
        assertEquals(getUrlsResponse(), response.getSettings().get(0).getUrls());
    }

    @Test
    public void getSettings_whenClientNotExist_expectSettingsResponse() {
        var historyDevices = List.of(TestConstants.DEFAULT_HISTORY_DEVICE);
        var historyNetworks = List.of(TestConstants.DEFAULT_HISTORY_NETWORK);
        when(uuidGenerator.generateUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID_GENERATED);
        when(rtrSettingsRequest.getName()).thenReturn(TestConstants.DEFAULT_CLIENT_NAME);
        when(rtrSettingsRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(rtrSettingsRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_ANDROID_PLATFORM);
        when(rtrSettingsRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(rtrSettingsRequest.getType()).thenReturn(ClientType.DESKTOP);
        when(rtrSettingsRequest.getTermsAndConditionsAcceptedVersion()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION);
        when(rtrClient.getTermsAndConditionsAcceptedVersion()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION - 1);
        when(clientService.saveClient(any())).thenReturn(savedRtrClient);
        when(savedRtrClient.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID_GENERATED);
        when(savedRtrClient.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(TestConstants.DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(getDefaultSettings());
        when(qosTestTypeDescService.getAll(TestConstants.DEFAULT_LANGUAGE)).thenReturn(getQosTestTypeDescResponses());
        when(clientService.listSyncedClientsByClientUid(DEFAULT_UID)).thenReturn(List.of(TestFixtures.client));
        when(testService.getDeviceHistory(argThat(t -> t.containsAll(uidList)))).thenReturn(historyDevices);
        when(testService.getGroupNameByClientIds(argThat(t -> t.containsAll(uidList)))).thenReturn(historyNetworks);
        when(testServerService.getServers()).thenReturn(getServerResponseList());
        when(testServerService.getServersWs()).thenReturn(getServerWsResponseList());
        when(testServerService.getServersQos()).thenReturn(getServerQosResponseList());
        when(clientTypeService.findByClientType(ClientType.DESKTOP)).thenReturn(Optional.empty());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        verify(clientService).saveClient(clientArgumentCaptor.capture());
        Assert.assertEquals(TestConstants.DEFAULT_CLIENT_UUID_GENERATED, clientArgumentCaptor.getValue().getUuid());
        assertEquals(TestConstants.DEFAULT_CLIENT_UUID_GENERATED, response.getSettings().get(0).getUuid());
        assertEquals(getAndroidTermAndConditionsResponse(), response.getSettings().get(0).getTermAndConditionsResponse());
        assertEquals(getQosTestTypeDescResponses(), response.getSettings().get(0).getQosTestTypeDescResponse());
        assertEquals(historyDevices, response.getSettings().get(0).getHistory().getDevices());
        assertEquals(historyNetworks, response.getSettings().get(0).getHistory().getNetworks());
        assertEquals(getMapServerResponse(), response.getSettings().get(0).getMapServerResponse());
        assertEquals(getServerResponseList(), response.getSettings().get(0).getServers());
        assertEquals(getServerWsResponseList(), response.getSettings().get(0).getServerWSResponseList());
        assertEquals(getServerQosResponseList(), response.getSettings().get(0).getServerQosResponseList());
        assertEquals(getUrlsResponse(), response.getSettings().get(0).getUrls());
    }

    @Test
    public void getSettings_whenIOSPlatform_expectSettingsResponse() {
        when(rtrSettingsRequest.getName()).thenReturn(TestConstants.DEFAULT_CLIENT_NAME);
        when(rtrSettingsRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(rtrSettingsRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_IOS_PLATFORM);
        when(rtrSettingsRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(rtrSettingsRequest.getTermsAndConditionsAcceptedVersion()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION);
        when(rtrClient.getTermsAndConditionsAcceptedVersion()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION - 1);
        when(clientService.getClientByUUID(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(rtrClient);
        when(clientService.saveClient(rtrClient)).thenReturn(savedRtrClient);
        when(savedRtrClient.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(savedRtrClient.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(TestConstants.DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(getDefaultSettings());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        assertEquals(TestConstants.DEFAULT_CLIENT_UUID, response.getSettings().get(0).getUuid());
        assertEquals(getIOSTermAndConditionsResponse(), response.getSettings().get(0).getTermAndConditionsResponse());
    }

    @Test
    public void getSettings_whenNotStandardPlatform_expectSettingsResponse() {
        when(rtrSettingsRequest.getName()).thenReturn(TestConstants.DEFAULT_CLIENT_NAME);
        when(rtrSettingsRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(rtrSettingsRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_PLATFORM);
        when(rtrSettingsRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(rtrSettingsRequest.getTermsAndConditionsAcceptedVersion()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION);
        when(rtrClient.getTermsAndConditionsAcceptedVersion()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION - 1);
        when(clientService.getClientByUUID(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(rtrClient);
        when(clientService.saveClient(rtrClient)).thenReturn(savedRtrClient);
        when(savedRtrClient.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(savedRtrClient.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(TestConstants.DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(getDefaultSettings());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        assertEquals(TestConstants.DEFAULT_CLIENT_UUID, response.getSettings().get(0).getUuid());
        assertEquals(getAndroidTermAndConditionsResponse(), response.getSettings().get(0).getTermAndConditionsResponse());
    }

    @Test
    public void createSettings_whenSettingsExist_expectSettingsUpdated() {
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(TestConstants.DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(List.of(settings));
        when(settings.getKey()).thenReturn(TestConstants.DEFAULT_SETTINGS_KEY);
        when(adminSettingsRequest.getSettings()).thenReturn(getAdminSettingsBodyRequest());
        when(adminSettingsBodyRequest.getTcUrl()).thenReturn(TestConstants.DEFAULT_TC_URL_VALUE);
        when(adminSettingsRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);

        rtrSettingsService.createSettings(adminSettingsRequest);

        verify(settings).setValue(TestConstants.DEFAULT_TC_URL_VALUE);
        verify(settingsRepository).saveAll(settingsArgumentCaptor.capture());
        List<Settings> actual = settingsArgumentCaptor.getValue();
        assertEquals(settings, actual.get(0));
        assertEquals("port_map_server", actual.get(1).getKey());
        assertEquals("123", actual.get(1).getValue());
    }

    @Test
    public void createSettings_whenSettingsLongExist_expectSettingsUpdated() {
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(TestConstants.DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(List.of(settings));
        when(settings.getKey()).thenReturn(TestConstants.DEFAULT_SETTINGS_KEY);
        when(adminSettingsRequest.getSettings()).thenReturn(getAdminSettingsBodyRequest());
        when(adminSettingsBodyRequest.getTcUrl()).thenReturn(TestConstants.DEFAULT_TC_URL_VALUE);
        when(adminSettingsRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);

        rtrSettingsService.createSettings(adminSettingsRequest);

        verify(settings).setValue(TestConstants.DEFAULT_TC_URL_VALUE);
        verify(settingsRepository).saveAll(settingsArgumentCaptor.capture());
        List<Settings> actual = settingsArgumentCaptor.getValue();
        assertEquals(settings, actual.get(0));
        assertEquals("port_map_server", actual.get(1).getKey());
        assertEquals("123", actual.get(1).getValue());
    }

    @Test
    public void createSettings_whenSettingsNotExist_expectSettingsCreated() {
        when(settings.getKey()).thenReturn(TestConstants.DEFAULT_SETTINGS_KEY);
        when(adminSettingsRequest.getSettings()).thenReturn(getAdminSettingsBodyRequest());
        when(adminSettingsBodyRequest.getTcUrl()).thenReturn(TestConstants.DEFAULT_TC_URL_VALUE);
        when(adminSettingsRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);

        rtrSettingsService.createSettings(adminSettingsRequest);

        verify(settingsRepository).saveAll(settingsArgumentCaptor.capture());
        List<Settings> actual = settingsArgumentCaptor.getValue();
        assertEquals("port_map_server", actual.get(1).getKey());
        assertEquals("123", actual.get(1).getValue());
        assertEquals(2, settingsArgumentCaptor.getValue().size());
        Assert.assertEquals(TestConstants.DEFAULT_SETTINGS_KEY, settingsArgumentCaptor.getValue().get(0).getKey());
        Assert.assertEquals(TestConstants.DEFAULT_TC_URL_VALUE, settingsArgumentCaptor.getValue().get(0).getValue());
        Assert.assertEquals(TestConstants.DEFAULT_LANGUAGE, settingsArgumentCaptor.getValue().get(0).getLang());
    }

    @Test
    public void updateSettings_whenSettingsNotExist_expectUpdated() {
        when(adminUpdateSettingsRequest.getAdminUpdateSettingsTermsAndConditionsRequest()).thenReturn(adminUpdateSettingsTermsAndConditionsRequest);
        when(adminUpdateSettingsRequest.getAdminUpdateSettingsUrlsRequest()).thenReturn(adminUpdateSettingsUrlsRequest);
        when(adminUpdateSettingsRequest.getAdminUpdateSettingsTestRequest()).thenReturn(adminUpdateSettingsTestRequest);
        when(adminUpdateSettingsRequest.getAdminUpdateSettingsSignalTestRequest()).thenReturn(adminUpdateSettingsSignalTestRequest);
        when(adminUpdateSettingsRequest.getAdminUpdateSettingsMapServerRequest()).thenReturn(adminUpdateSettingsMapServerRequest);

        when(adminUpdateSettingsTermsAndConditionsRequest.getTcUrl()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_URL);
        when(adminUpdateSettingsTermsAndConditionsRequest.getTcUrlIOS()).thenReturn(DEFAULT_TERM_AND_CONDITION_URL_IOS);
        when(adminUpdateSettingsTermsAndConditionsRequest.getTcUrlAndroid()).thenReturn(DEFAULT_TERM_AND_CONDITION_URL_ANDROID);
        when(adminUpdateSettingsTermsAndConditionsRequest.getTcVersion()).thenReturn(String.valueOf(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION));
        when(adminUpdateSettingsTermsAndConditionsRequest.getTcVersionIOS()).thenReturn(String.valueOf(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION_IOS));
        when(adminUpdateSettingsTermsAndConditionsRequest.getTcVersionAndroid()).thenReturn(String.valueOf(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION_ANDROID));
        when(adminUpdateSettingsTermsAndConditionsRequest.getTcNdtUrlAndroid()).thenReturn(TestConstants.DEFAULT_TERM_AND_CONDITION_NDT_URL);


        when(adminUpdateSettingsUrlsRequest.getControlIpV4Only()).thenReturn(TestConstants.DEFAULT_URLS_CONTROL_IPV4_ONLY);
        when(adminUpdateSettingsUrlsRequest.getControlIpV6Only()).thenReturn(TestConstants.DEFAULT_URLS_CONTROL_IPV6_ONLY);
        when(adminUpdateSettingsUrlsRequest.getUrlShare()).thenReturn(TestConstants.DEFAULT_URLS_URL_SHARE);
        when(adminUpdateSettingsUrlsRequest.getUrlMapServer()).thenReturn(TestConstants.DEFAULT_URLS_URL_MAP_SERVER);
        when(adminUpdateSettingsUrlsRequest.getStatistics()).thenReturn(TestConstants.DEFAULT_URLS_STATISTICS);
        when(adminUpdateSettingsUrlsRequest.getOpenDataPrefix()).thenReturn(TestConstants.DEFAULT_URLS_OPEN_DATA_PREFIX);
        when(adminUpdateSettingsUrlsRequest.getUrlIpV4Check()).thenReturn(TestConstants.DEFAULT_URLS_URL_IPV4_CHECK);
        when(adminUpdateSettingsUrlsRequest.getUrlIpV6Check()).thenReturn(TestConstants.DEFAULT_URLS_URL_IPV6_CHECK);

        when(adminUpdateSettingsTestRequest.getResultQosUrl()).thenReturn(TestConstants.DEFAULT_TEST_REQUEST_RESULT_QOS_URL);
        when(adminUpdateSettingsTestRequest.getResultUrl()).thenReturn(TestConstants.DEFAULT_TEST_REQUEST_RESULT_URL);
        when(adminUpdateSettingsTestRequest.getTestDuration()).thenReturn(TestConstants.DEFAULT_TEST_REQUEST_TEST_DURATION);
        when(adminUpdateSettingsTestRequest.getTestNumThreads()).thenReturn(TestConstants.DEFAULT_TEST_REQUEST_TEST_NUM_THREADS);
        when(adminUpdateSettingsTestRequest.getTestNumPings()).thenReturn(TestConstants.DEFAULT_TEST_REQUEST_TEST_NUM_PINGS);

        when(adminUpdateSettingsSignalTestRequest.getResultUrl()).thenReturn(TestConstants.DEFAULT_SIGNAL_TEST_REQUEST_RESULT_URL);

        when(adminUpdateSettingsMapServerRequest.getHost()).thenReturn(TestConstants.DEFAULT_MAP_SERVER_HOST);
        when(adminUpdateSettingsMapServerRequest.getPort()).thenReturn(String.valueOf(TestConstants.DEFAULT_MAP_SERVER_PORT));
        when(adminUpdateSettingsMapServerRequest.getSsl()).thenReturn(String.valueOf(TestConstants.DEFAULT_FLAG_TRUE));

        rtrSettingsService.updateSettings(adminUpdateSettingsRequest);

        verify(settingsRepository).saveAll(settingsArgumentCaptor.capture());
        assertEquals(getAdminSettingsList(), settingsArgumentCaptor.getValue());
    }

    @Test
    public void getAllSettings_whenCommonRequest_expectAdminSettingsResponse() {
        var expectedResponse = getAdminSettingsResponse();

        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn("en", Config.ADMIN_SETTINGS_KEYS)).thenReturn(getAdminSettingsList());

        var actualResponse = rtrSettingsService.getAllSettings();

        assertEquals(expectedResponse, actualResponse);
    }

    private AdminSettingsResponse getAdminSettingsResponse() {
        var adminSettingsTermAndConditionsResponse = AdminSettingsTermAndConditionsResponse.builder()
                .tcUrl(DEFAULT_TERM_AND_CONDITION_URL)
                .tcUrlIOS(DEFAULT_TERM_AND_CONDITION_URL_IOS)
                .tcUrlAndroid(DEFAULT_TERM_AND_CONDITION_URL_ANDROID)
                .tcVersion(DEFAULT_TERM_AND_CONDITION_VERSION.toString())
                .tcVersionIOS(DEFAULT_TERM_AND_CONDITION_VERSION_IOS.toString())
                .tcVersionAndroid(DEFAULT_TERM_AND_CONDITION_VERSION_ANDROID.toString())
                .tcNdtUrlAndroid(DEFAULT_TERM_AND_CONDITION_NDT_URL)
                .build();
        var urls = AdminSettingsUrlsResponse.builder()
                .urlShare(DEFAULT_URLS_URL_SHARE)
                .controlIpV4Only(DEFAULT_URLS_CONTROL_IPV4_ONLY)
                .controlIpV6Only(DEFAULT_URLS_CONTROL_IPV6_ONLY)
                .openDataPrefix(DEFAULT_URLS_OPEN_DATA_PREFIX)
                .statistics(DEFAULT_URLS_STATISTICS)
                .urlIpV4Check(DEFAULT_URLS_URL_IPV4_CHECK)
                .urlIpV6Check(DEFAULT_URLS_URL_IPV6_CHECK)
                .urlMapServer(DEFAULT_URLS_URL_MAP_SERVER)
                .build();
        var adminTestResponse = AdminSettingsTestResponse.builder()
                .resultQosUrl(DEFAULT_TEST_REQUEST_RESULT_QOS_URL)
                .resultUrl(DEFAULT_TEST_REQUEST_RESULT_URL)
                .testDuration(DEFAULT_TEST_REQUEST_TEST_DURATION)
                .testNumPings(DEFAULT_TEST_REQUEST_TEST_NUM_PINGS)
                .testNumThreads(DEFAULT_TEST_REQUEST_TEST_NUM_THREADS)
                .build();
        var adminSettingsSignalTestResponse = AdminSettingsSignalTestResponse.builder()
                .resultUrl(DEFAULT_SIGNAL_TEST_REQUEST_RESULT_URL)
                .build();
        var mapServerResponse = AdminSettingsMapServerResponse.builder()
                .host(DEFAULT_MAP_SERVER_HOST)
                .ssl(String.valueOf(DEFAULT_FLAG_TRUE))
                .port(String.valueOf(DEFAULT_MAP_SERVER_PORT))
                .build();
        var versions = AdminSettingsVersionResponse.builder()
                .controlServerVersion(DEFAULT_CONTROL_SERVER_VERSION)
                .build();

        return AdminSettingsResponse.builder()
                .termAndConditionsResponse(adminSettingsTermAndConditionsResponse)
                .urls(urls)
                .adminTestResponse(adminTestResponse)
                .adminSettingsSignalTestResponse(adminSettingsSignalTestResponse)
                .mapServerResponse(mapServerResponse)
                .versions(versions)
                .build();
    }

    private List<Settings> getAdminSettingsList() {
        List<Settings> settings = new ArrayList<>();
        addSettingToList(Config.TERM_AND_CONDITION_URL_KEY, TestConstants.DEFAULT_TERM_AND_CONDITION_URL, settings);
        addSettingToList(Config.TERM_AND_CONDITION_URL_IOS_KEY, TestConstants.DEFAULT_TERM_AND_CONDITION_URL_IOS, settings);
        addSettingToList(Config.TERM_AND_CONDITION_URL_ANDROID_KEY, TestConstants.DEFAULT_TERM_AND_CONDITION_URL_ANDROID, settings);
        addSettingToList(Config.TERM_AND_CONDITION_VERSION_KEY, String.valueOf(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION), settings);
        addSettingToList(Config.TERM_AND_CONDITION_VERSION_IOS_KEY, String.valueOf(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION_IOS), settings);
        addSettingToList(Config.TERM_AND_CONDITION_VERSION_ANDROID_KEY, String.valueOf(TestConstants.DEFAULT_TERM_AND_CONDITION_VERSION_ANDROID), settings);
        addSettingToList(Config.TERM_AND_CONDITION_NDT_URL_KEY, TestConstants.DEFAULT_TERM_AND_CONDITION_NDT_URL, settings);
        addSettingToList(Config.URL_OPEN_DATA_PREFIX_KEY, TestConstants.DEFAULT_URLS_OPEN_DATA_PREFIX, settings);
        addSettingToList(Config.URL_SHARE_KEY, TestConstants.DEFAULT_URLS_URL_SHARE, settings);
        addSettingToList(Config.URL_STATISTIC_KEY, TestConstants.DEFAULT_URLS_STATISTICS, settings);
        addSettingToList(Config.URL_CONTROL_IPV4_ONLY_KEY, TestConstants.DEFAULT_URLS_CONTROL_IPV4_ONLY, settings);
        addSettingToList(Config.URL_CONTROL_IPV6_ONLY_KEY, TestConstants.DEFAULT_URLS_CONTROL_IPV6_ONLY, settings);
        addSettingToList(Config.URL_IPV4_CHECK_KEY, TestConstants.DEFAULT_URLS_URL_IPV4_CHECK, settings);
        addSettingToList(Config.URL_IPV6_CHECK_KEY, TestConstants.DEFAULT_URLS_URL_IPV6_CHECK, settings);
        addSettingToList(Config.URL_MAP_SERVER_KEY, TestConstants.DEFAULT_URLS_URL_MAP_SERVER, settings);
        addSettingToList(Config.MAP_SERVER_HOST_KEY, TestConstants.DEFAULT_MAP_SERVER_HOST, settings);
        addSettingToList(Config.MAP_SERVER_SSL_KEY, String.valueOf(TestConstants.DEFAULT_FLAG_TRUE), settings);
        addSettingToList(Config.MAP_SERVER_PORT_KEY, String.valueOf(TestConstants.DEFAULT_MAP_SERVER_PORT), settings);
        addSettingToList(Config.TEST_RESULT_URL_KEY, TestConstants.DEFAULT_TEST_REQUEST_RESULT_URL, settings);
        addSettingToList(Config.TEST_RESULT_QOS_URL_KEY, TestConstants.DEFAULT_TEST_REQUEST_RESULT_QOS_URL, settings);
        addSettingToList(Config.TEST_DURATION_KEY, TestConstants.DEFAULT_TEST_REQUEST_TEST_DURATION, settings);
        addSettingToList(Config.TEST_NUM_THREADS_KEY, TestConstants.DEFAULT_TEST_REQUEST_TEST_NUM_THREADS, settings);
        addSettingToList(Config.TEST_NUM_PINGS_KEY, TestConstants.DEFAULT_TEST_REQUEST_TEST_NUM_PINGS, settings);
        addSettingToList(Config.SIGNAL_RESULT_URL_KEY, TestConstants.DEFAULT_SIGNAL_TEST_REQUEST_RESULT_URL, settings);

        return settings;
    }

    private void addSettingToList(String key, String value, List<Settings> settings) {
        var setting = Settings.builder()
                .key(key)
                .value(value)
                .lang("en")
                .build();
        settings.add(setting);
    }

    private AdminSettingsBodyRequest getAdminSettingsBodyRequest() {
        return AdminSettingsBodyRequest.builder()
                .tcUrl(TestConstants.DEFAULT_TC_URL_VALUE)
                .port(123L)
                .build();
    }

    private TermAndConditionsResponse getDefaultTermAndConditionsResponse() {
        return TermAndConditionsResponse.builder()
                .version(Long.valueOf(TestConstants.DEFAULT_TC_VERSION_VALUE))
                .url(TestConstants.DEFAULT_TC_URL_VALUE)
                .build();
    }

    private TermAndConditionsResponse getIOSTermAndConditionsResponse() {
        return TermAndConditionsResponse.builder()
                .url(TestConstants.DEFAULT_TC_URL_IOS_VALUE)
                .version(Long.valueOf(TestConstants.DEFAULT_TC_VERSION_IOS_VALUE))
                .build();
    }

    private UrlsResponse getUrlsResponse() {
        return UrlsResponse.builder()
                .urlShare(TestConstants.DEFAULT_URLS_URL_SHARE)
                .urlIPV6Check(TestConstants.DEFAULT_URLS_URL_IPV6_CHECK)
                .controlIPV4Only(TestConstants.DEFAULT_URLS_CONTROL_IPV4_ONLY)
                .openDataPrefix(TestConstants.DEFAULT_URLS_OPEN_DATA_PREFIX)
                .urlMapServer(TestConstants.DEFAULT_URLS_URL_MAP_SERVER)
                .urlIPV4Check(TestConstants.DEFAULT_URLS_URL_IPV4_CHECK)
                .controlIPV6Only(TestConstants.DEFAULT_URLS_CONTROL_IPV6_ONLY)
                .statistics(TestConstants.DEFAULT_URLS_STATISTICS)
                .build();
    }

    private TermAndConditionsResponse getAndroidTermAndConditionsResponse() {
        return TermAndConditionsResponse.builder()
                .url(TestConstants.DEFAULT_TC_URL_ANDROID_VALUE)
                .ndtUrl(TestConstants.DEFAULT_TC_NDT_URL_ANDROID_VALUE)
                .version(Long.valueOf(TestConstants.DEFAULT_TC_VERSION_ANDROID_VALUE))
                .build();
    }

    private MapServerResponse getMapServerResponse() {
        return MapServerResponse.builder()
                .host(TestConstants.DEFAULT_MAP_SERVER_HOST)
                .ssl(TestConstants.DEFAULT_FLAG_TRUE)
                .port(TestConstants.DEFAULT_MAP_SERVER_PORT)
                .build();
    }

    private List<TestServerResponseForSettings> getServerResponseList() {
        var testServerResponse = TestServerResponseForSettings.builder()
                .name(TestConstants.DEFAULT_TEST_SERVER_NAME)
                .uuid(TestConstants.DEFAULT_SERVER_UUID)
                .build();
        return List.of(testServerResponse);
    }

    private List<TestServerResponseForSettings> getServerWsResponseList() {
        var testServerResponse = TestServerResponseForSettings.builder()
                .name(TestConstants.DEFAULT_TEST_SERVER_WS_NAME)
                .uuid(TestConstants.DEFAULT_SERVER_WS_UUID)
                .build();
        return List.of(testServerResponse);
    }

    private List<TestServerResponseForSettings> getServerQosResponseList() {
        var testServerResponse = TestServerResponseForSettings.builder()
                .name(TestConstants.DEFAULT_TEST_SERVER_WS_NAME)
                .uuid(TestConstants.DEFAULT_SERVER_WS_UUID)
                .build();
        return List.of(testServerResponse);
    }


    private List<QosTestTypeDescResponse> getQosTestTypeDescResponses() {
        var qosTestTypeDescResponse = QosTestTypeDescResponse.builder()
                .name(TestConstants.DEFAULT_QOS_TEST_TYPE_DESC_NAME)
                .testType(TestConstants.DEFAULT_TEST_TYPE.toString())
                .build();
        return List.of(qosTestTypeDescResponse);
    }

    private List<Settings> getDefaultSettings() {
        var tcUrlAndroid = new Settings(null, "tc_url_android", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_TC_URL_ANDROID_VALUE);
        var tcNdtUrlAndroid = new Settings(null, "tc_ndt_url_android", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_TC_NDT_URL_ANDROID_VALUE);
        var tcVersionAndroid = new Settings(null, "tc_version_android", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_TC_VERSION_ANDROID_VALUE);
        var tcUrlAndroidV4 = new Settings(null, "tc_url_android_v4", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_TC_URL_ANDROID_V4_VALUE);
        var tcUrlIOS = new Settings(null, "tc_url_ios", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_TC_URL_IOS_VALUE);
        var tcVersionIOS = new Settings(null, "tc_version_ios", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_TC_VERSION_IOS_VALUE);
        var tcVersion = new Settings(null, "tc_version", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_TC_VERSION_VALUE);
        var tcUrl = new Settings(null, "tc_url", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_TC_URL_VALUE);
        var hostMapServer = new Settings(null, "host_map_server", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_MAP_SERVER_HOST);
        var sslMapServer = new Settings(null, "ssl_map_server", TestConstants.DEFAULT_LANGUAGE, String.valueOf(TestConstants.DEFAULT_FLAG_TRUE));
        var portMapServer = new Settings(null, "port_map_server", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_MAP_SERVER_PORT.toString());
        var urlOpenDataPrefix = new Settings(null, "url_open_data_prefix", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_OPEN_DATA_PREFIX);
        var urlShare = new Settings(null, "url_share", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_URL_SHARE);
        var urlStatistics = new Settings(null, "url_statistics", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_STATISTICS);
        var controlIpv4Only = new Settings(null, "control_ipv4_only", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_CONTROL_IPV4_ONLY);
        var controlIpv6Only = new Settings(null, "control_ipv6_only", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_CONTROL_IPV6_ONLY);
        var urlIpv4Check = new Settings(null, "url_ipv4_check", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_URL_IPV4_CHECK);
        var urlIpv6Check = new Settings(null, "url_ipv6_check", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_URL_IPV6_CHECK);
        var urlMapServer = new Settings(null, "url_map_server", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_URL_MAP_SERVER);
        return List.of(tcUrlAndroid, tcNdtUrlAndroid, tcVersionAndroid,
                tcUrlAndroidV4, tcUrlIOS, tcVersionIOS,
                tcVersion, tcUrl, hostMapServer,
                sslMapServer, portMapServer, urlOpenDataPrefix, urlShare,
                urlStatistics, controlIpv4Only, controlIpv6Only,
                urlIpv4Check, urlIpv6Check, urlMapServer);
    }
}
