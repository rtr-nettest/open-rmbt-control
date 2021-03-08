package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.SignalService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static at.rtr.rmbt.constant.URIConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();
        mapperBuilder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        SignalController signalController = new SignalController(signalService);
        mockMvc = MockMvcBuilders.standaloneSetup(signalController)
                .setControllerAdvice(new RtrAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapperBuilder.build()))
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
                .andExpect(jsonPath("$.result_url").value(TestConstants.DEFAULT_RESULT_URL))
                .andExpect(jsonPath("$.client_remote_ip").value(TestConstants.DEFAULT_IP))
                .andExpect(jsonPath("$.provider").value(TestConstants.DEFAULT_PROVIDER))
                .andExpect(jsonPath("$.test_uuid").value(TestConstants.DEFAULT_UUID.toString()));

        verify(signalService).registerSignal(signalRequestArgumentCaptor.capture(), any());

        assertEquals(request, signalRequestArgumentCaptor.getValue());
    }

    @Test
    public void getSignalHistory_withPageable_expectList() throws Exception {
        final var response = new PageImpl<>(Collections.singletonList(SignalMeasurementResponse.builder()
                .testUuid(TestConstants.DEFAULT_UUID)
                .build()));
        final PageRequest pageRequest = PageRequest.of(TestConstants.DEFAULT_PAGE, TestConstants.DEFAULT_SIZE,
                Sort.Direction.DESC, TestConstants.DEFAULT_SORT_PROPERTY);
        when(signalService.getSignalsHistory(pageRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_SIGNAL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", String.valueOf(TestConstants.DEFAULT_PAGE))
                .param("size", String.valueOf(TestConstants.DEFAULT_SIZE))
                .param("sort", TestConstants.DEFAULT_SORT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].test_uuid").value(TestConstants.DEFAULT_UUID.toString()));
    }

    @Test
    public void processSignalResult_whenCommonData_expectProcessSignalResultCalled() throws Exception {
        var request = getSignalResultRequest();
        var response = getSignalResultResponse();
        when(signalService.processSignalResult(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(SIGNAL_RESULT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.test_uuid").value(TestConstants.DEFAULT_UUID.toString()));

        verify(signalService).processSignalResult(request);
    }

    @Test
    public void getSignalStrength_whenCommonData_expectListSignalStrengthResponse() throws Exception {
        var response = getSignalStrengthResponse();
        when(signalService.getSignalStrength(TestConstants.DEFAULT_UUID)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(SIGNAL_STRENGTH_BY_UUID, TestConstants.DEFAULT_UUID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("signal_strength", hasSize(1)))
                .andExpect(jsonPath("signal_strength.[0].time").value(TestConstants.DEFAULT_SIGNAL_STRENGTH_TIME))
                .andExpect(jsonPath("signal_strength.[0].technology").value(TestConstants.DEFAULT_TECHNOLOGY.toString()))
                .andExpect(jsonPath("signal_strength.[0].signal_strength").value(TestConstants.DEFAULT_SIGNAL_STRENGTH_RESPONSE))
                .andExpect(jsonPath("signal_strength.[0].ci").value(TestConstants.DEFAULT_AREA_CODE))
                .andExpect(jsonPath("signal_strength.[0].tac").value(TestConstants.DEFAULT_LOCATION_ID))
                .andExpect(jsonPath("signal_strength.[0].pci").value(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE))
                .andExpect(jsonPath("signal_strength.[0].earfcn").value(TestConstants.DEFAULT_CHANNEL_NUMBER))
                .andExpect(jsonPath("signal_strength.[0].frequency").value(TestConstants.DEFAULT_FREQUENCY))
                .andExpect(jsonPath("signal_strength.[0].band").value(TestConstants.DEFAULT_BAND))
                .andExpect(jsonPath("signal_strength.[0].speed").value(TestConstants.DEFAULT_SIGNAL_STRENGTH_SPEED_RESPONSE))
                .andExpect(jsonPath("signal_strength.[0].accuracy").value(TestConstants.DEFAULT_SIGNAL_STRENGTH_ACCURACY_RESPONSE))
                .andExpect(jsonPath("signal_strength.[0].altitude").value(TestConstants.DEFAULT_SIGNAL_STRENGTH_ALTITUDE_RESPONSE))
                .andExpect(jsonPath("signal_strength.[0].bearing").value(TestConstants.DEFAULT_SIGNAL_STRENGTH_BEARING_RESPONSE))
                .andExpect(jsonPath("test_response.test_uuid").value(TestConstants.DEFAULT_TEST_UUID.toString()))
                .andExpect(jsonPath("test_response.time").value(TestConstants.DEFAULT_ZONED_DATE_TIME.toInstant().getEpochSecond()));

        verify(signalService).getSignalStrength(TestConstants.DEFAULT_UUID);
    }

    private SignalDetailsResponse getSignalStrengthResponse() {
        var testResponse = TestResponse.builder()
                .time(TestConstants.DEFAULT_ZONED_DATE_TIME)
                .testUUID(TestConstants.DEFAULT_TEST_UUID)
                .build();

        return SignalDetailsResponse.builder()
                .signalStrength(Collections.singletonList(SignalStrengthResponse.builder()
                        .technology(TestConstants.DEFAULT_TECHNOLOGY)
                        .band(TestConstants.DEFAULT_BAND)
                        .ci(TestConstants.DEFAULT_AREA_CODE)
                        .earfcn(TestConstants.DEFAULT_CHANNEL_NUMBER)
                        .frequency(TestConstants.DEFAULT_FREQUENCY)
                        .pci(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE)
                        .signalStrength(TestConstants.DEFAULT_SIGNAL_STRENGTH_RESPONSE)
                        .tac(TestConstants.DEFAULT_LOCATION_ID)
                        .time(TestConstants.DEFAULT_SIGNAL_STRENGTH_TIME)
                        .speed(TestConstants.DEFAULT_SIGNAL_STRENGTH_SPEED_RESPONSE)
                        .altitude(TestConstants.DEFAULT_SIGNAL_STRENGTH_ALTITUDE_RESPONSE)
                        .accuracy(TestConstants.DEFAULT_SIGNAL_STRENGTH_ACCURACY_RESPONSE)
                        .bearing(TestConstants.DEFAULT_SIGNAL_STRENGTH_BEARING_RESPONSE)
                        .build()))
                .testResponse(testResponse)
                .build();
    }

    private SignalResultResponse getSignalResultResponse() {
        return SignalResultResponse.builder()
                .testUUID(TestConstants.DEFAULT_UUID)
                .build();
    }

    private SignalResultRequest getSignalResultRequest() {
        return SignalResultRequest.builder()
                .testUUID(TestConstants.DEFAULT_UUID)
                .build();
    }

    private SignalSettingsResponse getRegisterSignalResponse() {
        return SignalSettingsResponse.builder()
                .resultUrl(TestConstants.DEFAULT_RESULT_URL)
                .clientRemoteIp(TestConstants.DEFAULT_IP)
                .provider(TestConstants.DEFAULT_PROVIDER)
                .testUUID(TestConstants.DEFAULT_UUID)
                .build();
    }

    private SignalRequest getRegisterSignalRequest() {
        return SignalRequest.builder()
                .time(TestConstants.DEFAULT_TIME)
                .timezone(TestConstants.DEFAULT_TIMEZONE)
                .uuid(TestConstants.DEFAULT_CLIENT_UUID)
                .build();
    }
}
