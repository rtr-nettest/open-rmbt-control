package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.ApplicationVersionResponse;
import at.rtr.rmbt.service.ApplicationVersionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
public class ApplicationVersionControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ApplicationVersionService applicationVersionService;

    @Before
    public void setUp() throws Exception {
        ApplicationVersionController applicationVersionController = new ApplicationVersionController(applicationVersionService);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationVersionController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void getApplicationVersion_whenCommonData_expectApplicationVersionResponse() throws Exception {
        var applicationVersionResponse = getApplicationVersionResponse();
        when(applicationVersionService.getApplicationVersion()).thenReturn(applicationVersionResponse);
        mockMvc.perform(post(URIConstants.VERSION))
                .andDo(print())
                .andExpect(content().json(TestUtils.asJsonString(applicationVersionResponse)));
    }

    private ApplicationVersionResponse getApplicationVersionResponse() {
        return ApplicationVersionResponse.builder()
                .systemUUID(TestConstants.DEFAULT_SYSTEM_UUID_VALUE)
                .version(TestConstants.DEFAULT_CONTROL_SERVER_VERSION)
                .host(TestConstants.DEFAULT_APPLICATION_HOST)
                .build();
    }
}
