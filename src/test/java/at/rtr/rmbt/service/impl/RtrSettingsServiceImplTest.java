package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.exception.NotSupportedClientVersionException;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.enums.ClientType;
import at.rtr.rmbt.repository.SettingsRepository;
import at.rtr.rmbt.request.AdminSettingsBodyRequest;
import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.response.*;
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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RtrSettingsServiceImplTest {
    private RtrSettingsService rtrSettingsService;

    @MockBean
    private ClientTypeService clientTypeService;
    @MockBean
    private ClientService clientService;
    @MockBean
    private SettingsRepository settingsRepository;
    @MockBean
    private QoSTestTypeDescService qosTestTypeDescService;
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
                clock);
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
        when(qosTestTypeDescService.getAll(TestConstants.DEFAULT_LANGUAGE)).thenReturn(getQoSTestTypeDescResponses());
        when(testService.getDeviceHistory(TestConstants.DEFAULT_UID)).thenReturn(historyDevices);
        when(testService.getGroupNameByClientId(TestConstants.DEFAULT_UID)).thenReturn(historyNetworks);
        when(testServerService.getServers()).thenReturn(getServerResponseList());
        when(testServerService.getServersWs()).thenReturn(getServerWsResponseList());
        when(testServerService.getServersQos()).thenReturn(getServerQoSResponseList());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        assertEquals(TestConstants.DEFAULT_CLIENT_UUID, response.getSettings().get(0).getUuid());
        assertEquals(getAndroidTermAndConditionsResponse(), response.getSettings().get(0).getTermAndConditionsResponse());
        assertEquals(getQoSTestTypeDescResponses(), response.getSettings().get(0).getQosTestTypeDescResponse());
        assertEquals(historyDevices, response.getSettings().get(0).getHistory().getDevices());
        assertEquals(historyNetworks, response.getSettings().get(0).getHistory().getNetworks());
        assertEquals(getMapServerResponse(), response.getSettings().get(0).getMapServerResponse());
        assertEquals(getServerResponseList(), response.getSettings().get(0).getServers());
        assertEquals(getServerWsResponseList(), response.getSettings().get(0).getServerWSResponseList());
        assertEquals(getServerQoSResponseList(), response.getSettings().get(0).getServerQoSResponseList());
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
        when(qosTestTypeDescService.getAll(TestConstants.DEFAULT_LANGUAGE)).thenReturn(getQoSTestTypeDescResponses());
        when(testService.getDeviceHistory(TestConstants.DEFAULT_UID)).thenReturn(historyDevices);
        when(testService.getGroupNameByClientId(TestConstants.DEFAULT_UID)).thenReturn(historyNetworks);
        when(testServerService.getServers()).thenReturn(getServerResponseList());
        when(testServerService.getServersWs()).thenReturn(getServerWsResponseList());
        when(testServerService.getServersQos()).thenReturn(getServerQoSResponseList());
        when(clientTypeService.findByClientType(ClientType.DESKTOP)).thenReturn(Optional.empty());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        verify(clientService).saveClient(clientArgumentCaptor.capture());
        Assert.assertEquals(TestConstants.DEFAULT_CLIENT_UUID_GENERATED, clientArgumentCaptor.getValue().getUuid());
        assertEquals(TestConstants.DEFAULT_CLIENT_UUID_GENERATED, response.getSettings().get(0).getUuid());
        assertEquals(getAndroidTermAndConditionsResponse(), response.getSettings().get(0).getTermAndConditionsResponse());
        assertEquals(getQoSTestTypeDescResponses(), response.getSettings().get(0).getQosTestTypeDescResponse());
        assertEquals(historyDevices, response.getSettings().get(0).getHistory().getDevices());
        assertEquals(historyNetworks, response.getSettings().get(0).getHistory().getNetworks());
        assertEquals(getMapServerResponse(), response.getSettings().get(0).getMapServerResponse());
        assertEquals(getServerResponseList(), response.getSettings().get(0).getServers());
        assertEquals(getServerWsResponseList(), response.getSettings().get(0).getServerWSResponseList());
        assertEquals(getServerQoSResponseList(), response.getSettings().get(0).getServerQoSResponseList());
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
                .urlIPV6Check(TestConstants.DEFAULT_URLS_IPV6_CHECK)
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

    private List<TestServerResponseForSettings> getServerQoSResponseList() {
        var testServerResponse = TestServerResponseForSettings.builder()
                .name(TestConstants.DEFAULT_TEST_SERVER_WS_NAME)
                .uuid(TestConstants.DEFAULT_SERVER_WS_UUID)
                .build();
        return List.of(testServerResponse);
    }


    private List<QoSTestTypeDescResponse> getQoSTestTypeDescResponses() {
        var qosTestTypeDescResponse = QoSTestTypeDescResponse.builder()
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
        var urlIpv6Check = new Settings(null, "url_ipv6_check", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_IPV6_CHECK);
        var urlMapServer = new Settings(null, "url_map_server", TestConstants.DEFAULT_LANGUAGE, TestConstants.DEFAULT_URLS_URL_MAP_SERVER);
        return List.of(tcUrlAndroid, tcNdtUrlAndroid, tcVersionAndroid,
                tcUrlAndroidV4, tcUrlIOS, tcVersionIOS,
                tcVersion, tcUrl, hostMapServer,
                sslMapServer, portMapServer, urlOpenDataPrefix, urlShare,
                urlStatistics, controlIpv4Only, controlIpv6Only,
                urlIpv4Check, urlIpv6Check, urlMapServer);
    }
}
