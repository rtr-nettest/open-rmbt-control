package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.enums.ClientType;
import at.rtr.rmbt.request.AdminSettingsBodyRequest;
import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.request.settings.admin.update.AdminUpdateSettingsMapServerRequest;
import at.rtr.rmbt.request.settings.admin.update.AdminUpdateSettingsRequest;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.response.settings.admin.update.*;
import at.rtr.rmbt.service.RtrSettingsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static at.rtr.rmbt.TestConstants.*;
import static at.rtr.rmbt.constant.URIConstants.ADMIN_SETTING;
import static at.rtr.rmbt.constant.URIConstants.SETTINGS_URL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class RTRSettingsControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private RtrSettingsService rtrSettingsService;

    @Before
    public void setUp() {
        RTRSettingsController settingsController = new RTRSettingsController(rtrSettingsService);
        mockMvc = MockMvcBuilders.standaloneSetup(settingsController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void createSettings_whenCommonRequest_expectCreateSettingsCalled() throws Exception {
        var request = getCreateSettingsRequest();

        mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_SETTING)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(rtrSettingsService).createSettings(request);
    }

    @Test
    public void showNewsList_whenCommonRequest_expectGetSettingsCalled() throws Exception {
        var request = getSettingsRequest();
        var response = getSettingsResponse();
        when(rtrSettingsService.getSettings(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(SETTINGS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.settings[0].terms_and_conditions.version").value(DEFAULT_TERM_AND_CONDITION_VERSION))
                .andExpect(jsonPath("$.settings[0].terms_and_conditions.url").value(DEFAULT_TERM_AND_CONDITION_URL))
                .andExpect(jsonPath("$.settings[0].terms_and_conditions.ndt_url").value(DEFAULT_TERM_AND_CONDITION_NDT_URL))
                .andExpect(jsonPath("$.settings[0].urls.url_share").value(DEFAULT_URLS_URL_SHARE))
                .andExpect(jsonPath("$.settings[0].urls.url_ipv6_check").value(DEFAULT_URLS_URL_IPV6_CHECK))
                .andExpect(jsonPath("$.settings[0].urls.control_ipv4_only").value(DEFAULT_URLS_CONTROL_IPV4_ONLY))
                .andExpect(jsonPath("$.settings[0].urls.open_data_prefix").value(DEFAULT_URLS_OPEN_DATA_PREFIX))
                .andExpect(jsonPath("$.settings[0].urls.url_map_server").value(DEFAULT_URLS_URL_MAP_SERVER))
                .andExpect(jsonPath("$.settings[0].urls.url_ipv4_check").value(DEFAULT_URLS_URL_IPV4_CHECK))
                .andExpect(jsonPath("$.settings[0].urls.control_ipv6_only").value(DEFAULT_URLS_CONTROL_IPV6_ONLY))
                .andExpect(jsonPath("$.settings[0].urls.statistics").value(DEFAULT_URLS_STATISTICS))
                .andExpect(jsonPath("$.settings[0].qostesttype_desc[0].name").value(DEFAULT_QOS_TEST_TYPE_DESC_NAME))
                .andExpect(jsonPath("$.settings[0].qostesttype_desc[0].test_type").value(DEFAULT_TEST_TYPE.toString()))
                .andExpect(jsonPath("$.settings[0].versions.control_server_version").value(DEFAULT_CONTROL_SERVER_VERSION))
                .andExpect(jsonPath("$.settings[0].servers[0].name").value(DEFAULT_TEST_SERVER_NAME))
                .andExpect(jsonPath("$.settings[0].servers[0].uuid").value(DEFAULT_SERVER_UUID))
                .andExpect(jsonPath("$.settings[0].servers_ws[0].name").value(DEFAULT_TEST_SERVER_WS_NAME))
                .andExpect(jsonPath("$.settings[0].servers_ws[0].uuid").value(DEFAULT_SERVER_WS_UUID))
                .andExpect(jsonPath("$.settings[0].servers_qos[0].name").value(DEFAULT_TEST_SERVER_QOS_NAME))
                .andExpect(jsonPath("$.settings[0].servers_qos[0].uuid").value(DEFAULT_SERVER_QOS_UUID))
                .andExpect(jsonPath("$.settings[0].history.devices[0]").value(DEFAULT_HISTORY_DEVICE))
                .andExpect(jsonPath("$.settings[0].history.networks[0]").value(DEFAULT_HISTORY_NETWORK))
                .andExpect(jsonPath("$.settings[0].uuid").value(DEFAULT_CLIENT_UUID.toString()))
                .andExpect(jsonPath("$.settings[0].map_server.port").value(DEFAULT_MAP_SERVER_PORT))
                .andExpect(jsonPath("$.settings[0].map_server.host").value(DEFAULT_MAP_SERVER_HOST))
                .andExpect(jsonPath("$.settings[0].map_server.ssl").value(DEFAULT_FLAG_TRUE));
    }

    @Test
    public void getSettings_whenCommonData_expectAdminSettingResponse() throws Exception {
        var response = getAllSettings();
        when(rtrSettingsService.getAllSettings()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_SETTING))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.termsAndConditions.tcUrl").value(DEFAULT_TERM_AND_CONDITION_URL))
            .andExpect(jsonPath("$.termsAndConditions.tcUrlIOS").value(DEFAULT_TERM_AND_CONDITION_URL_IOS))
            .andExpect(jsonPath("$.termsAndConditions.tcUrlAndroid").value(DEFAULT_TERM_AND_CONDITION_URL_ANDROID))
            .andExpect(jsonPath("$.termsAndConditions.tcVersion").value(DEFAULT_TERM_AND_CONDITION_VERSION))
            .andExpect(jsonPath("$.termsAndConditions.tcVersionIOS").value(DEFAULT_TERM_AND_CONDITION_VERSION_IOS))
            .andExpect(jsonPath("$.termsAndConditions.tcVersionAndroid").value(DEFAULT_TERM_AND_CONDITION_VERSION_ANDROID))
            .andExpect(jsonPath("$.termsAndConditions.tcNdtUrlAndroid").value(DEFAULT_TERM_AND_CONDITION_NDT_URL))
            .andExpect(jsonPath("$.urls.urlShare").value(DEFAULT_URLS_URL_SHARE))
            .andExpect(jsonPath("$.urls.urlIpV4Check").value(DEFAULT_URLS_URL_IPV4_CHECK))
            .andExpect(jsonPath("$.urls.urlIpV6Check").value(DEFAULT_URLS_URL_IPV6_CHECK))
            .andExpect(jsonPath("$.urls.controlIpV4Only").value(DEFAULT_URLS_CONTROL_IPV4_ONLY))
            .andExpect(jsonPath("$.urls.controlIpV6Only").value(DEFAULT_URLS_CONTROL_IPV6_ONLY))
            .andExpect(jsonPath("$.urls.openDataPrefix").value(DEFAULT_URLS_OPEN_DATA_PREFIX))
            .andExpect(jsonPath("$.urls.urlMapServer").value(DEFAULT_URLS_URL_MAP_SERVER))
            .andExpect(jsonPath("$.urls.statistics").value(DEFAULT_URLS_STATISTICS))
            .andExpect(jsonPath("$.testRequest.resultUrl").value(DEFAULT_TEST_REQUEST_RESULT_URL))
            .andExpect(jsonPath("$.testRequest.resultQosUrl").value(DEFAULT_TEST_REQUEST_RESULT_QOS_URL))
            .andExpect(jsonPath("$.testRequest.testDuration").value(DEFAULT_TEST_REQUEST_TEST_DURATION))
            .andExpect(jsonPath("$.testRequest.testNumThreads").value(DEFAULT_TEST_REQUEST_TEST_NUM_THREADS))
            .andExpect(jsonPath("$.testRequest.testNumPings").value(DEFAULT_TEST_REQUEST_TEST_NUM_PINGS))
            .andExpect(jsonPath("$.signalTestRequest.resultUrl").value(DEFAULT_SIGNAL_TEST_REQUEST_RESULT_URL))
            .andExpect(jsonPath("$.mapServer.port").value(DEFAULT_MAP_SERVER_PORT))
            .andExpect(jsonPath("$.mapServer.host").value(DEFAULT_MAP_SERVER_HOST))
            .andExpect(jsonPath("$.mapServer.ssl").value(String.valueOf(DEFAULT_FLAG_TRUE)))
            .andExpect(jsonPath("$.versions.controlServerVersion").value(DEFAULT_CONTROL_SERVER_VERSION));

        verify(rtrSettingsService).getAllSettings();
    }

    @Test
    public void updateSettings_whenCommonRequest_expectUpdateSettingsCalled() throws Exception {
        var request = getAdminUpdateSettingsRequest();

        mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_SETTING)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtils.asJsonString(request)))
            .andExpect(status().isOk());

        verify(rtrSettingsService).updateSettings(request);
    }

    private AdminUpdateSettingsRequest getAdminUpdateSettingsRequest() {
        var adminUpdateSettingsMapServerRequest = AdminUpdateSettingsMapServerRequest.builder()
            .host(DEFAULT_MAP_SERVER_HOST)
            .port(String.valueOf(DEFAULT_MAP_SERVER_PORT))
            .ssl(String.valueOf(DEFAULT_FLAG_TRUE))
            .build();

        return AdminUpdateSettingsRequest.builder()
            .adminUpdateSettingsMapServerRequest(adminUpdateSettingsMapServerRequest)
            .build();
    }

    private AdminSettingsResponse getAllSettings() {
        var adminSettingsTermAndConditionsResponse = AdminSettingsTermAndConditionsResponse.builder()
            .tcUrl(DEFAULT_TERM_AND_CONDITION_URL)
            .tcUrlIOS(DEFAULT_TERM_AND_CONDITION_URL_IOS)
            .tcUrlAndroid(DEFAULT_TERM_AND_CONDITION_URL_ANDROID)
            .tcVersion(String.valueOf(DEFAULT_TERM_AND_CONDITION_VERSION))
            .tcVersionIOS(String.valueOf(DEFAULT_TERM_AND_CONDITION_VERSION_IOS))
            .tcVersionAndroid(String.valueOf(DEFAULT_TERM_AND_CONDITION_VERSION_ANDROID))
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

    private AdminSettingsRequest getCreateSettingsRequest() {

        return AdminSettingsRequest.builder()
            .settings(getAdminSettingsBodyRequest())
            .language(DEFAULT_LANGUAGE)
            .build();
    }

    private AdminSettingsBodyRequest getAdminSettingsBodyRequest() {
        return AdminSettingsBodyRequest.builder()
            .tcUrl(DEFAULT_TC_URL_ANDROID_VALUE)
            .tcVersion(DEFAULT_TC_VERSION_VALUE)
            .tcUrlAndroid(DEFAULT_TC_URL_ANDROID_VALUE)
            .tcNdtUrlAndroid(DEFAULT_TC_NDT_URL_ANDROID_VALUE)
            .tcVersionAndroid(DEFAULT_TC_VERSION_ANDROID_VALUE)
            .tcUrlIOS(DEFAULT_TC_URL_IOS_VALUE)
            .tcVersionIOS(DEFAULT_TC_VERSION_IOS_VALUE)
            .urlMapServer(DEFAULT_URLS_URL_MAP_SERVER)
            .urlShare(DEFAULT_URLS_URL_SHARE)
            .urlIPV6Check(DEFAULT_URLS_URL_IPV6_CHECK)
            .urlIPV4Check(DEFAULT_URLS_URL_IPV4_CHECK)
            .controlIPV4Only(DEFAULT_URLS_CONTROL_IPV4_ONLY)
            .statistics(DEFAULT_URLS_STATISTICS)
            .controlIPV6Only(DEFAULT_URLS_CONTROL_IPV6_ONLY)
            .openDataPrefix(DEFAULT_URLS_OPEN_DATA_PREFIX)
            .port(DEFAULT_MAP_SERVER_PORT)
            .ssl(DEFAULT_FLAG_TRUE)
            .host(DEFAULT_MAP_SERVER_HOST)
            .build();
    }

    private SettingsResponse getSettingsResponse() {
        var mapServerResponse = MapServerResponse.builder()
                .port(DEFAULT_MAP_SERVER_PORT)
                .ssl(DEFAULT_FLAG_TRUE)
                .host(DEFAULT_MAP_SERVER_HOST)
                .build();

        var history = SettingsHistoryResponse.builder()
                .networks(List.of(DEFAULT_HISTORY_NETWORK))
                .devices(List.of(DEFAULT_HISTORY_DEVICE))
                .build();

        var qosTestTypeDescResponse = QosTestTypeDescResponse.builder()
                .testType(DEFAULT_TEST_TYPE.toString())
                .name(DEFAULT_QOS_TEST_TYPE_DESC_NAME)
                .build();

        var server = TestServerResponseForSettings.builder()
                .name(DEFAULT_TEST_SERVER_NAME)
                .uuid(DEFAULT_SERVER_UUID)
                .build();

        var serverWSResponseList = TestServerResponseForSettings.builder()
                .name(DEFAULT_TEST_SERVER_WS_NAME)
                .uuid(DEFAULT_SERVER_WS_UUID)
                .build();

        var serverQosResponseList = TestServerResponseForSettings.builder()
                .name(DEFAULT_TEST_SERVER_QOS_NAME)
                .uuid(DEFAULT_SERVER_QOS_UUID)
                .build();

        var version = VersionResponse.builder()
                .controlServerVersion(DEFAULT_CONTROL_SERVER_VERSION)
                .build();

        var termAndConditionsResponse = TermAndConditionsResponse.builder()
                .version(DEFAULT_TERM_AND_CONDITION_VERSION)
                .url(DEFAULT_TERM_AND_CONDITION_URL)
                .ndtUrl(DEFAULT_TERM_AND_CONDITION_NDT_URL)
                .build();

        var urls = UrlsResponse.builder()
                .urlMapServer(DEFAULT_URLS_URL_MAP_SERVER)
                .urlShare(DEFAULT_URLS_URL_SHARE)
                .urlIPV6Check(DEFAULT_URLS_URL_IPV6_CHECK)
                .urlIPV4Check(DEFAULT_URLS_URL_IPV4_CHECK)
                .controlIPV4Only(DEFAULT_URLS_CONTROL_IPV4_ONLY)
                .statistics(DEFAULT_URLS_STATISTICS)
                .controlIPV6Only(DEFAULT_URLS_CONTROL_IPV6_ONLY)
                .openDataPrefix(DEFAULT_URLS_OPEN_DATA_PREFIX)
                .build();

        var setting = SettingResponse.builder()
                .uuid(DEFAULT_CLIENT_UUID)
                .history(history)
                .mapServerResponse(mapServerResponse)
                .qosTestTypeDescResponse(List.of(qosTestTypeDescResponse))
                .servers(List.of(server))
                .serverWSResponseList(List.of(serverWSResponseList))
                .serverQosResponseList(List.of(serverQosResponseList))
                .versions(version)
                .termAndConditionsResponse(termAndConditionsResponse)
                .urls(urls)
                .build();

        return SettingsResponse.builder()
                .settings(List.of(setting))
                .build();
    }

    private RtrSettingsRequest getSettingsRequest() {
        return RtrSettingsRequest.builder()
                .type(ClientType.DESKTOP)
                .language(DEFAULT_LANGUAGE)
                .uuid(DEFAULT_CLIENT_UUID)
                .build();
    }
}
