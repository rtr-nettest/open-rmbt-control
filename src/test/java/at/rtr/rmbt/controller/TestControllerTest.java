package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.service.TestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

        mockMvc.perform(MockMvcRequestBuilders.get(String.join("",URIConstants.TEST, URIConstants.BY_TEST_UUID),TestConstants.DEFAULT_TEST_UUID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testUUID").value(TestConstants.DEFAULT_TEST_UUID.toString()))
                .andExpect(jsonPath("$.time").value(TestConstants.DEFAULT_ZONED_DATE_TIME.toInstant().getEpochSecond()));
    }

    private TestResponse getTestResponse() {
        return TestResponse.builder()
                .testUUID(TestConstants.DEFAULT_TEST_UUID)
                .time(TestConstants.DEFAULT_ZONED_DATE_TIME)
                .build();
    }
}
