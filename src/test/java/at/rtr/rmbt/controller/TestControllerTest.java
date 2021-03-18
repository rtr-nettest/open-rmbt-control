package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.CapabilitiesRequest;
import at.rtr.rmbt.request.ClassificationRequest;
import at.rtr.rmbt.request.TestResultRequest;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.response.TestResultContainerResponse;
import at.rtr.rmbt.response.TestResultResponse;
import at.rtr.rmbt.service.TestService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class TestControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TestService testService;

    @Before
    public void setUp() {
        TestController testController = new TestController(testService);
        mockMvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void getSignalStrength_whenTestExist_expectTestResponse() throws Exception {
        var response = getTestResponse();
        when(testService.getTestByUUID(TestConstants.DEFAULT_TEST_UUID)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(String.join("", URIConstants.TEST, URIConstants.BY_TEST_UUID), TestConstants.DEFAULT_TEST_UUID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testUUID").value(TestConstants.DEFAULT_TEST_UUID.toString()))
                .andExpect(jsonPath("$.time").value(TestConstants.DEFAULT_ZONED_DATE_TIME.toInstant().getEpochSecond()));
    }

    @Test
    public void getTestResultByTestUUID_whenTestExist_expectTestResponse() throws Exception {
        var request = getTestResultRequest();
        var response = getTestResultResponse();
        when(testService.getTestResult(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(String.join(StringUtils.EMPTY, URIConstants.TEST, URIConstants.TEST_RESULT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.testresult[0].time_string").value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME))
                .andExpect(jsonPath("$.testresult[0].share_text").value(TestConstants.DEFAULT_TEST_RESULT_RESPONSE_SHARE_TEXT_DUAL_SIM_FALSE_LTE_RSRP_NOT_NULL))
                .andExpect(jsonPath("$.testresult[0].share_subject").value(TestConstants.DEFAULT_TEST_RESULT_RESPONSE_SHARE_SUBJECT))
                .andExpect(jsonPath("$.testresult[0].open_test_uuid").value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID));
    }

    private TestResultRequest getTestResultRequest() {
        return TestResultRequest.builder()
                .language(TestConstants.LANGUAGE_EN)
                .testUUID(TestConstants.DEFAULT_TEST_UUID)
                .capabilitiesRequest(CapabilitiesRequest.builder()
                        .classification(ClassificationRequest.builder()
                                .count(TestConstants.DEFAULT_CLASSIFICATION_COUNT)
                                .build())
                        .build())
                .build();
    }

    private TestResultContainerResponse getTestResultResponse() {
        TestResultResponse testResultResponse = TestResultResponse.builder()
                .timeString(TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME)
                .shareText(TestConstants.DEFAULT_TEST_RESULT_RESPONSE_SHARE_TEXT_DUAL_SIM_FALSE_LTE_RSRP_NOT_NULL)
                .shareSubject(TestConstants.DEFAULT_TEST_RESULT_RESPONSE_SHARE_SUBJECT)
                .openTestUUID(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID)
                .build();
        List<TestResultResponse> testResultResponses = new ArrayList<>();
        testResultResponses.add(testResultResponse);
        return TestResultContainerResponse.builder()
                .testResultResponses(testResultResponses)
                .build();
    }

    private TestResponse getTestResponse() {
        return TestResponse.builder()
                .testUUID(TestConstants.DEFAULT_TEST_UUID)
                .time(TestConstants.DEFAULT_ZONED_DATE_TIME)
                .build();
    }
}
