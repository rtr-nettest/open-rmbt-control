package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.DataCollectorResponse;
import at.rtr.rmbt.service.RequestDataCollectorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class RequestDataCollectorControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private RequestDataCollectorService requestDataCollectorService;

    @Before
    public void setUp() {
        RequestDataCollectorController requestDataCollectorController = new RequestDataCollectorController(requestDataCollectorService);
        mockMvc = MockMvcBuilders.standaloneSetup(requestDataCollectorController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void getDataCollectorResponse_whenCommonRequest_expectDataCollectorResponse() throws Exception {
        var dataCollectorResponse = getDataCollectorResponse();
        when(requestDataCollectorService.getDataCollectorResponse(any(), any())).thenReturn(dataCollectorResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(URIConstants.REQUEST_DATA_COLLECTOR))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.ip").value(TestConstants.DEFAULT_IP))
                .andExpect(jsonPath("$.agent").value(TestConstants.DEFAULT_USER_AGENT_STRING))
                .andExpect(jsonPath("$.url").value(TestConstants.DEFAULT_REQUEST_URL))
                .andExpect(jsonPath("$.languages[0]").value(Locale.ENGLISH.toString()))
                .andExpect(jsonPath("$.languages[1]").value(Locale.FRANCE.toString()))
                .andExpect(jsonPath("$.product").value(TestConstants.DEFAULT_USER_AGENT_PRODUCT))
                .andExpect(jsonPath("$.version").value(TestConstants.DEFAULT_USER_AGENT_VERSION))
                .andExpect(jsonPath("$.category").value(TestConstants.DEFAULT_USER_AGENT_CATEGORY))
                .andExpect(jsonPath("$.os").value(TestConstants.DEFAULT_USER_AGENT_OS))
                .andExpect(jsonPath("$.headers.User-Agent").value(TestConstants.DEFAULT_USER_AGENT_STRING));

    }

    private DataCollectorResponse getDataCollectorResponse() {
        return DataCollectorResponse.builder()
                .ip(TestConstants.DEFAULT_IP)
                .agent(TestConstants.DEFAULT_USER_AGENT_STRING)
                .url(TestConstants.DEFAULT_REQUEST_URL)
                .languages(List.of(Locale.ENGLISH.toString(), Locale.FRANCE.toString()))
                .product(TestConstants.DEFAULT_USER_AGENT_PRODUCT)
                .version(TestConstants.DEFAULT_USER_AGENT_VERSION)
                .category(TestConstants.DEFAULT_USER_AGENT_CATEGORY)
                .os(TestConstants.DEFAULT_USER_AGENT_OS)
                .headers(getHeaders())
                .build();
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", TestConstants.DEFAULT_USER_AGENT_STRING);
        return headers;
    }
}