package com.rtr.nettest.controller;

import com.rtr.nettest.TestUtils;
import com.rtr.nettest.advice.RtrAdvice;
import com.rtr.nettest.request.SettingsRequest;
import com.rtr.nettest.response.*;
import com.rtr.nettest.service.SettingsService;
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

import static com.rtr.nettest.TestConstants.*;
import static com.rtr.nettest.constant.HeaderConstants.IP;
import static com.rtr.nettest.constant.URIConstants.SETTINGS_URL;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class SettingsControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private SettingsService settingsService;

    @Before
    public void setUp() {
        SettingsController settingsController = new SettingsController(settingsService);
        mockMvc = MockMvcBuilders.standaloneSetup(settingsController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void showNewsList_whenCommonRequest_expectGetSettingsCalled() throws Exception {
        var request = getSettingsRequest();
        var response = getSettingsResponse();
        when(settingsService.getSettings(request, DEFAULT_IP_HEADER)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(SETTINGS_URL)
                .header(IP, DEFAULT_IP_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.settings[0].terms_and_conditions.version").value(DEFAULT_TERM_AND_CONDITION_VERSION))
                .andExpect(jsonPath("$.settings[0].terms_and_conditions.url").value(DEFAULT_TERM_AND_CONDITION_URL))
                .andExpect(jsonPath("$.settings[0].terms_and_conditions.ndt_url").value(DEFAULT_TERM_AND_CONDITION_NDT_URL))
                .andExpect(jsonPath("$.settings[0].urls.url_share").value(DEFAULT_URLS_URL_SHARE))
                .andExpect(jsonPath("$.settings[0].urls.url_ipv6_check").value(DEFAULT_URLS_IPV6_CHECK))
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

    private SettingsResponse getSettingsResponse() {
        var mapServerResponse = MapServerResponse.builder()
                .port(DEFAULT_MAP_SERVER_PORT)
                .ssl(DEFAULT_FLAG_TRUE)
                .host(DEFAULT_MAP_SERVER_HOST)
                .build();

        var history = HistoryResponse.builder()
                .networks(List.of(DEFAULT_HISTORY_NETWORK))
                .devices(List.of(DEFAULT_HISTORY_DEVICE))
                .build();

        var qosTestTypeDescResponse = QoSTestTypeDescResponse.builder()
                .testType(DEFAULT_TEST_TYPE.toString())
                .name(DEFAULT_QOS_TEST_TYPE_DESC_NAME)
                .build();

        var server = TestServerResponse.builder()
                .name(DEFAULT_TEST_SERVER_NAME)
                .uuid(DEFAULT_SERVER_UUID)
                .build();

        var serverWSResponseList = TestServerResponse.builder()
                .name(DEFAULT_TEST_SERVER_WS_NAME)
                .uuid(DEFAULT_SERVER_WS_UUID)
                .build();

        var serverQoSResponseList = TestServerResponse.builder()
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
                .urlIPV6Check(DEFAULT_URLS_IPV6_CHECK)
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
                .serverQoSResponseList(List.of(serverQoSResponseList))
                .versions(version)
                .termAndConditionsResponse(termAndConditionsResponse)
                .urls(urls)
                .build();

        return SettingsResponse.builder()
                .settings(List.of(setting))
                .build();
    }

    private SettingsRequest getSettingsRequest() {
        return SettingsRequest.builder()
                .type("DESKTOP")
                .language(DEFAULT_LANGUAGE)
                .uuid(DEFAULT_CLIENT_UUID)
                .build();
    }
}