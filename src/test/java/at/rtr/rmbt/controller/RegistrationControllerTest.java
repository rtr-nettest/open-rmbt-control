package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.facade.TestSettingsFacade;
import at.rtr.rmbt.enums.ClientType;
import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.enums.TestPlatform;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.request.TestSettingsRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.TestSettingsResponse;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class RegistrationControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TestSettingsFacade testSettingsFacade;

    @Before
    public void setUp() {
        RegistrationController registrationController = new RegistrationController(testSettingsFacade);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
            .setControllerAdvice(new RtrAdvice())
            .build();
    }

    @Test
    public void updateTestSettings_whenCommonRequest_shouldReturnUpdatedSettings() throws Exception {
        TestSettingsRequest testSettingsRequest = new TestSettingsRequest(
            TestPlatform.ANDROID,
            1,
            false,
            TestStatus.END,
            1,
            "fix/rtr_release_fixes_'4ce8bda9'",
            "4.1.19",
            true,
            "1",
            3,
            TestSettingsRequest.ProtocolVersion.IPV4,
            new TestSettingsRequest.Location(1.0, 1.0, "provider", 1f, 1.0, 1L, 1L, 1f, 1f, false, 1),
            System.currentTimeMillis(),
            "Europe/Vienna",
            ServerType.RMBT,
            "1",
            ClientType.MOBILE,
            "41ab60bd-becf-45c8-abbc-0e85b59d65ca",
            "en",
            true,
            new TestSettingsRequest.LoopModeInfo(1L, "f46b1165-2451-4989-a2f5-5eb7b598aa48", "c94e7c39-8774-4210-8be9-2411c5da9ff7", 30, 2, 10000, 1, -1, "a165c0a4-cc23-4e39-a1b3-8a111a32e755"),
            new TestSettingsRequest.Capabilities(
                new TestSettingsRequest.Capabilities.ClassificationCapabilities(1),
                new TestSettingsRequest.Capabilities.QosCapabilities(true),
                true
            ), Collections.emptyList()
        );

        TestSettingsResponse testSettingsResponse = new TestSettingsResponse(
            "127.0.0.1",
            "41ab60bd-becf-45c8-abbc-0e85b59d65ca",
            "https://test-server.rtr.com/test",
            "https://test-server.rtr.com/testQoS",
            "1000",
            "OpenRMBT Server",
            0,
            "dev-rmbt.rtr.com",
            "3",
            22,
            "5bd11dd8-992a-4429-b1e0-e93da81e5118",
            ServerType.RMBT,
            true,
            "test_token",
            "5",
            1L,
            "a165c0a4-cc23-4e39-a1b3-8a111a32e755",
            "provider",
            new ErrorResponse(),
            Lists.emptyList()
        );

        when(testSettingsFacade.updateTestSettings(eq(testSettingsRequest), any())).thenReturn(testSettingsResponse);

        mockMvc.perform(
            post("/testRequest")
                .content(TestUtils.asJsonString(testSettingsRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
        ).andExpect(status().isOk())
            .andExpect(content().json(TestUtils.asJsonString(testSettingsResponse)));

        verify(testSettingsFacade).updateTestSettings(eq(testSettingsRequest), any());
    }
}
