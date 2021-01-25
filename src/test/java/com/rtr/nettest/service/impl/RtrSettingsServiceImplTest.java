package com.rtr.nettest.service.impl;

import com.rtr.nettest.constant.Config;
import com.rtr.nettest.exception.NotSupportedClientVersionException;
import com.rtr.nettest.model.RtrClient;
import com.rtr.nettest.model.Settings;
import com.rtr.nettest.repository.SettingsRepository;
import com.rtr.nettest.request.AdminSettingsBodyRequest;
import com.rtr.nettest.request.AdminSettingsRequest;
import com.rtr.nettest.request.RtrSettingsRequest;
import com.rtr.nettest.response.*;
import com.rtr.nettest.service.*;
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

import static com.rtr.nettest.TestConstants.*;
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
        clock = Clock.fixed(Instant.parse(DEFAULT_INSTANT_EXPECTED), ZoneId.of("UTC"));
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
        when(rtrSettingsRequest.getName()).thenReturn(DEFAULT_TEXT);
        rtrSettingsService.getSettings(rtrSettingsRequest);
    }

    @Test
    public void getSettings_whenAndroidPlatform_expectSettingsResponse() {
        var historyDevices = List.of(DEFAULT_HISTORY_DEVICE);
        var historyNetworks = List.of(DEFAULT_HISTORY_NETWORK);
        when(rtrSettingsRequest.getName()).thenReturn(DEFAULT_CLIENT_NAME);
        when(rtrSettingsRequest.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        when(rtrSettingsRequest.getPlatform()).thenReturn(DEFAULT_ANDROID_PLATFORM);
        when(rtrSettingsRequest.getLanguage()).thenReturn(DEFAULT_LANGUAGE);
        when(rtrSettingsRequest.getTermsAndConditionsAcceptedVersion()).thenReturn(DEFAULT_TERM_AND_CONDITION_VERSION);
        when(rtrClient.getTermAndConditionsVersion()).thenReturn(DEFAULT_TERM_AND_CONDITION_VERSION - 1);
        when(clientService.getClientByUUID(DEFAULT_CLIENT_UUID)).thenReturn(rtrClient);
        when(clientService.saveClient(rtrClient)).thenReturn(savedRtrClient);
        when(savedRtrClient.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        when(savedRtrClient.getId()).thenReturn(DEFAULT_UID);
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(getDefaultSettings());
        when(qosTestTypeDescService.getAll(DEFAULT_LANGUAGE)).thenReturn(getQoSTestTypeDescResponses());
        when(testService.getDeviceHistory(DEFAULT_UID)).thenReturn(historyDevices);
        when(testService.getGroupNameByClientId(DEFAULT_UID)).thenReturn(historyNetworks);
        when(testServerService.getServers()).thenReturn(getServerResponseList());
        when(testServerService.getServersWs()).thenReturn(getServerWsResponseList());
        when(testServerService.getServersQos()).thenReturn(getServerQoSResponseList());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        assertEquals(DEFAULT_CLIENT_UUID, response.getSettings().get(0).getUuid());
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
        var historyDevices = List.of(DEFAULT_HISTORY_DEVICE);
        var historyNetworks = List.of(DEFAULT_HISTORY_NETWORK);
        when(uuidGenerator.generateUUID()).thenReturn(DEFAULT_CLIENT_UUID_GENERATED);
        when(rtrSettingsRequest.getName()).thenReturn(DEFAULT_CLIENT_NAME);
        when(rtrSettingsRequest.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        when(rtrSettingsRequest.getPlatform()).thenReturn(DEFAULT_ANDROID_PLATFORM);
        when(rtrSettingsRequest.getLanguage()).thenReturn(DEFAULT_LANGUAGE);
        when(rtrSettingsRequest.getTermsAndConditionsAcceptedVersion()).thenReturn(DEFAULT_TERM_AND_CONDITION_VERSION);
        when(rtrClient.getTermAndConditionsVersion()).thenReturn(DEFAULT_TERM_AND_CONDITION_VERSION - 1);
        when(clientService.saveClient(any())).thenReturn(savedRtrClient);
        when(savedRtrClient.getUuid()).thenReturn(DEFAULT_CLIENT_UUID_GENERATED);
        when(savedRtrClient.getId()).thenReturn(DEFAULT_UID);
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(getDefaultSettings());
        when(qosTestTypeDescService.getAll(DEFAULT_LANGUAGE)).thenReturn(getQoSTestTypeDescResponses());
        when(testService.getDeviceHistory(DEFAULT_UID)).thenReturn(historyDevices);
        when(testService.getGroupNameByClientId(DEFAULT_UID)).thenReturn(historyNetworks);
        when(testServerService.getServers()).thenReturn(getServerResponseList());
        when(testServerService.getServersWs()).thenReturn(getServerWsResponseList());
        when(testServerService.getServersQos()).thenReturn(getServerQoSResponseList());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        verify(clientService).saveClient(clientArgumentCaptor.capture());
        assertEquals(DEFAULT_CLIENT_UUID_GENERATED, clientArgumentCaptor.getValue().getUuid());
        assertEquals(DEFAULT_CLIENT_UUID_GENERATED, response.getSettings().get(0).getUuid());
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
        when(rtrSettingsRequest.getName()).thenReturn(DEFAULT_CLIENT_NAME);
        when(rtrSettingsRequest.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        when(rtrSettingsRequest.getPlatform()).thenReturn(DEFAULT_IOS_PLATFORM);
        when(rtrSettingsRequest.getLanguage()).thenReturn(DEFAULT_LANGUAGE);
        when(rtrSettingsRequest.getTermsAndConditionsAcceptedVersion()).thenReturn(DEFAULT_TERM_AND_CONDITION_VERSION);
        when(rtrClient.getTermAndConditionsVersion()).thenReturn(DEFAULT_TERM_AND_CONDITION_VERSION - 1);
        when(clientService.getClientByUUID(DEFAULT_CLIENT_UUID)).thenReturn(rtrClient);
        when(clientService.saveClient(rtrClient)).thenReturn(savedRtrClient);
        when(savedRtrClient.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        when(savedRtrClient.getId()).thenReturn(DEFAULT_UID);
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(getDefaultSettings());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        assertEquals(DEFAULT_CLIENT_UUID, response.getSettings().get(0).getUuid());
        assertEquals(getIOSTermAndConditionsResponse(), response.getSettings().get(0).getTermAndConditionsResponse());
    }

    @Test
    public void getSettings_whenNotStandardPlatform_expectSettingsResponse() {
        when(rtrSettingsRequest.getName()).thenReturn(DEFAULT_CLIENT_NAME);
        when(rtrSettingsRequest.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        when(rtrSettingsRequest.getPlatform()).thenReturn(DEFAULT_PLATFORM);
        when(rtrSettingsRequest.getLanguage()).thenReturn(DEFAULT_LANGUAGE);
        when(rtrSettingsRequest.getTermsAndConditionsAcceptedVersion()).thenReturn(DEFAULT_TERM_AND_CONDITION_VERSION);
        when(rtrClient.getTermAndConditionsVersion()).thenReturn(DEFAULT_TERM_AND_CONDITION_VERSION - 1);
        when(clientService.getClientByUUID(DEFAULT_CLIENT_UUID)).thenReturn(rtrClient);
        when(clientService.saveClient(rtrClient)).thenReturn(savedRtrClient);
        when(savedRtrClient.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        when(savedRtrClient.getId()).thenReturn(DEFAULT_UID);
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(getDefaultSettings());

        var response = rtrSettingsService.getSettings(rtrSettingsRequest);

        assertEquals(DEFAULT_CLIENT_UUID, response.getSettings().get(0).getUuid());
        assertEquals(getDefaultTermAndConditionsResponse(), response.getSettings().get(0).getTermAndConditionsResponse());
    }

    @Test
    public void createSettings_whenSettingsExist_expectSettingsUpdated() {
        when(settingsRepository.findAllByLangOrLangIsNullAndKeyIn(DEFAULT_LANGUAGE, Config.SETTINGS_KEYS))
                .thenReturn(List.of(settings));
        when(settings.getKey()).thenReturn(DEFAULT_SETTINGS_KEY);
        when(adminSettingsRequest.getSettings()).thenReturn(getAdminSettingsBodyRequest());
        when(adminSettingsBodyRequest.getTcUrl()).thenReturn(DEFAULT_TC_URL_VALUE);
        when(adminSettingsRequest.getLanguage()).thenReturn(DEFAULT_LANGUAGE);

        rtrSettingsService.createSettings(adminSettingsRequest);

        verify(settings).setValue(DEFAULT_TC_URL_VALUE);
        verify(settingsRepository).saveAll(List.of(settings));
    }

    @Test
    public void createSettings_whenSettingsNotExist_expectSettingsCreated() {
        when(settings.getKey()).thenReturn(DEFAULT_SETTINGS_KEY);
        when(adminSettingsRequest.getSettings()).thenReturn(getAdminSettingsBodyRequest());
        when(adminSettingsBodyRequest.getTcUrl()).thenReturn(DEFAULT_TC_URL_VALUE);
        when(adminSettingsRequest.getLanguage()).thenReturn(DEFAULT_LANGUAGE);

        rtrSettingsService.createSettings(adminSettingsRequest);

        verify(settingsRepository).saveAll(settingsArgumentCaptor.capture());
        assertEquals(1, settingsArgumentCaptor.getValue().size());
        assertEquals(DEFAULT_SETTINGS_KEY, settingsArgumentCaptor.getValue().get(0).getKey());
        assertEquals(DEFAULT_TC_URL_VALUE, settingsArgumentCaptor.getValue().get(0).getValue());
        assertEquals(DEFAULT_LANGUAGE, settingsArgumentCaptor.getValue().get(0).getLang());
    }

    private AdminSettingsBodyRequest getAdminSettingsBodyRequest() {
        return AdminSettingsBodyRequest.builder()
                .tcUrl(DEFAULT_TC_URL_VALUE)
                .build();
    }

    private TermAndConditionsResponse getDefaultTermAndConditionsResponse() {
        return TermAndConditionsResponse.builder()
                .version(Long.valueOf(DEFAULT_TC_VERSION_VALUE))
                .url(DEFAULT_TC_URL_VALUE)
                .build();
    }

    private TermAndConditionsResponse getIOSTermAndConditionsResponse() {
        return TermAndConditionsResponse.builder()
                .url(DEFAULT_TC_URL_IOS_VALUE)
                .version(Long.valueOf(DEFAULT_TC_VERSION_IOS_VALUE))
                .build();
    }

    private UrlsResponse getUrlsResponse() {
        return UrlsResponse.builder()
                .urlShare(DEFAULT_URLS_URL_SHARE)
                .urlIPV6Check(DEFAULT_URLS_IPV6_CHECK)
                .controlIPV4Only(DEFAULT_URLS_CONTROL_IPV4_ONLY)
                .openDataPrefix(DEFAULT_URLS_OPEN_DATA_PREFIX)
                .urlMapServer(DEFAULT_URLS_URL_MAP_SERVER)
                .urlIPV4Check(DEFAULT_URLS_URL_IPV4_CHECK)
                .controlIPV6Only(DEFAULT_URLS_CONTROL_IPV6_ONLY)
                .statistics(DEFAULT_URLS_STATISTICS)
                .build();
    }

    private TermAndConditionsResponse getAndroidTermAndConditionsResponse() {
        return TermAndConditionsResponse.builder()
                .url(DEFAULT_TC_URL_ANDROID_VALUE)
                .ndtUrl(DEFAULT_TC_NDT_URL_ANDROID_VALUE)
                .version(Long.valueOf(DEFAULT_TC_VERSION_ANDROID_VALUE))
                .build();
    }

    private MapServerResponse getMapServerResponse() {
        return MapServerResponse.builder()
                .host(DEFAULT_MAP_SERVER_HOST)
                .ssl(DEFAULT_FLAG_TRUE)
                .port(DEFAULT_MAP_SERVER_PORT)
                .build();
    }

    private List<TestServerResponse> getServerResponseList() {
        var testServerResponse = TestServerResponse.builder()
                .name(DEFAULT_TEST_SERVER_NAME)
                .uuid(DEFAULT_SERVER_UUID)
                .build();
        return List.of(testServerResponse);
    }

    private List<TestServerResponse> getServerWsResponseList() {
        var testServerResponse = TestServerResponse.builder()
                .name(DEFAULT_TEST_SERVER_WS_NAME)
                .uuid(DEFAULT_SERVER_WS_UUID)
                .build();
        return List.of(testServerResponse);
    }

    private List<TestServerResponse> getServerQoSResponseList() {
        var testServerResponse = TestServerResponse.builder()
                .name(DEFAULT_TEST_SERVER_WS_NAME)
                .uuid(DEFAULT_SERVER_WS_UUID)
                .build();
        return List.of(testServerResponse);
    }


    private List<QoSTestTypeDescResponse> getQoSTestTypeDescResponses() {
        var qosTestTypeDescResponse = QoSTestTypeDescResponse.builder()
                .name(DEFAULT_QOS_TEST_TYPE_DESC_NAME)
                .testType(DEFAULT_TEST_TYPE.toString())
                .build();
        return List.of(qosTestTypeDescResponse);
    }

    private List<Settings> getDefaultSettings() {
        var tcUrlAndroid = new Settings(null, "tc_url_android", DEFAULT_LANGUAGE, DEFAULT_TC_URL_ANDROID_VALUE);
        var tcNdtUrlAndroid = new Settings(null, "tc_ndt_url_android", DEFAULT_LANGUAGE, DEFAULT_TC_NDT_URL_ANDROID_VALUE);
        var tcVersionAndroid = new Settings(null, "tc_version_android", DEFAULT_LANGUAGE, DEFAULT_TC_VERSION_ANDROID_VALUE);
        var tcUrlAndroidV4 = new Settings(null, "tc_url_android_v4", DEFAULT_LANGUAGE, DEFAULT_TC_URL_ANDROID_V4_VALUE);
        var tcUrlIOS = new Settings(null, "tc_url_ios", DEFAULT_LANGUAGE, DEFAULT_TC_URL_IOS_VALUE);
        var tcVersionIOS = new Settings(null, "tc_version_ios", DEFAULT_LANGUAGE, DEFAULT_TC_VERSION_IOS_VALUE);
        var tcVersion = new Settings(null, "tc_version", DEFAULT_LANGUAGE, DEFAULT_TC_VERSION_VALUE);
        var tcUrl = new Settings(null, "tc_url", DEFAULT_LANGUAGE, DEFAULT_TC_URL_VALUE);
        var hostMapServer = new Settings(null, "host_map_server", DEFAULT_LANGUAGE, DEFAULT_MAP_SERVER_HOST);
        var sslMapServer = new Settings(null, "ssl_map_server", DEFAULT_LANGUAGE, String.valueOf(DEFAULT_FLAG_TRUE));
        var portMapServer = new Settings(null, "port_map_server", DEFAULT_LANGUAGE, DEFAULT_MAP_SERVER_PORT.toString());
        var urlOpenDataPrefix = new Settings(null, "url_open_data_prefix", DEFAULT_LANGUAGE, DEFAULT_URLS_OPEN_DATA_PREFIX);
        var urlShare = new Settings(null, "url_share", DEFAULT_LANGUAGE, DEFAULT_URLS_URL_SHARE);
        var urlStatistics = new Settings(null, "url_statistics", DEFAULT_LANGUAGE, DEFAULT_URLS_STATISTICS);
        var controlIpv4Only = new Settings(null, "control_ipv4_only", DEFAULT_LANGUAGE, DEFAULT_URLS_CONTROL_IPV4_ONLY);
        var controlIpv6Only = new Settings(null, "control_ipv6_only", DEFAULT_LANGUAGE, DEFAULT_URLS_CONTROL_IPV6_ONLY);
        var urlIpv4Check = new Settings(null, "url_ipv4_check", DEFAULT_LANGUAGE, DEFAULT_URLS_URL_IPV4_CHECK);
        var urlIpv6Check = new Settings(null, "url_ipv6_check", DEFAULT_LANGUAGE, DEFAULT_URLS_IPV6_CHECK);
        var urlMapServer = new Settings(null, "url_map_server", DEFAULT_LANGUAGE, DEFAULT_URLS_URL_MAP_SERVER);
        return List.of(tcUrlAndroid, tcNdtUrlAndroid, tcVersionAndroid,
                tcUrlAndroidV4, tcUrlIOS, tcVersionIOS,
                tcVersion, tcUrl, hostMapServer,
                sslMapServer, portMapServer, urlOpenDataPrefix, urlShare,
                urlStatistics, controlIpv4Only, controlIpv6Only,
                urlIpv4Check, urlIpv6Check, urlMapServer);
    }
}