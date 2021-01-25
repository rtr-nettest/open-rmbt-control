package com.rtr.nettest.controller;

import com.rtr.nettest.TestUtils;
import com.rtr.nettest.advice.RtrAdvice;
import com.rtr.nettest.request.SignalRequest;
import com.rtr.nettest.response.SignalResponse;
import com.rtr.nettest.service.SignalService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.rtr.nettest.TestConstants.*;
import static com.rtr.nettest.constant.URIConstants.SIGNAL_REQUEST;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class SignalControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private SignalService signalService;
    @Captor
    private ArgumentCaptor<SignalRequest> signalRequestArgumentCaptor;

    @Before
    public void setUp() {
        SignalController signalController = new SignalController(signalService);
        mockMvc = MockMvcBuilders.standaloneSetup(signalController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void registerSignal_whenCommonData_expectRegisterSignalCalled() throws Exception {
        var request = getRegisterSignalRequest();
        var response = getRegisterSignalResponse();
        when(signalService.registerSignal(any(), any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(SIGNAL_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result_url").value(DEFAULT_RESULT_URL))
                .andExpect(jsonPath("$.client_remote_ip").value(DEFAULT_IP))
                .andExpect(jsonPath("$.provider").value(DEFAULT_PROVIDER))
                .andExpect(jsonPath("$.test_uuid").value(DEFAULT_UUID.toString()));

        verify(signalService).registerSignal(signalRequestArgumentCaptor.capture(), any());

        assertEquals(request, signalRequestArgumentCaptor.getValue());
    }

    private SignalResponse getRegisterSignalResponse() {
        return SignalResponse.builder()
                .resultUrl(DEFAULT_RESULT_URL)
                .clientRemoteIp(DEFAULT_IP)
                .provider(DEFAULT_PROVIDER)
                .testUUID(DEFAULT_UUID)
                .build();
    }

    private SignalRequest getRegisterSignalRequest() {
        return SignalRequest.builder()
                .time(DEFAULT_TIME)
                .timezone(DEFAULT_TIMEZONE)
                .uuid(DEFAULT_CLIENT_UUID)
                .build();
    }
}