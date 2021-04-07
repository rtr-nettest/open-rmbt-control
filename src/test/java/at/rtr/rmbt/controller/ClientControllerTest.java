package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.SyncRequest;
import at.rtr.rmbt.response.SyncItemResponse;
import at.rtr.rmbt.response.SyncResponse;
import at.rtr.rmbt.service.ClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
public class ClientControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Before
    public void setUp() {
        ClientController clientController = new ClientController(clientService);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void sync_whenCommonData_expectResponse() throws Exception {
        var syncRequest = getSyncRequest();
        var syncResponse = getSyncResponse();
        when(clientService.sync(syncRequest)).thenReturn(syncResponse);
        mockMvc.perform(post(URIConstants.SYNC)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(syncRequest)))
                .andDo(print())
                .andExpect(content().json(TestUtils.asJsonString(syncResponse)));
    }

    private SyncResponse getSyncResponse() {
        var synItemResponse = SyncItemResponse.builder()
                .msgText(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TEXT)
                .msgTitle(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TEXT)
                .success(true)
                .syncCode(TestConstants.DEFAULT_SYNC_CODE)
                .build();
        return SyncResponse.builder()
                .sync(List.of(synItemResponse))
                .build();
    }

    private SyncRequest getSyncRequest() {
        return SyncRequest.builder()
                .language(TestConstants.LANGUAGE_EN)
                .syncCode(TestConstants.DEFAULT_SYNC_CODE)
                .uuid(TestConstants.DEFAULT_CLIENT_UUID)
                .build();
    }
}
