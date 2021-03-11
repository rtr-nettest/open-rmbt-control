package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.service.ResultService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class ResultControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private ResultService resultService;

    @Captor
    private ArgumentCaptor<ResultRequest> resultRequestArgumentCaptor;


    @Before
    public void setUp() {
        ResultController resultController = new ResultController(resultService);
        mockMvc = MockMvcBuilders.standaloneSetup(resultController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void processResult_whenCommonRequest_expectProcessResultRequestCalled() throws Exception {
        var request = getResultRequest();
        mockMvc.perform(post(URIConstants.RESULT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(resultService).processResultRequest(any(), resultRequestArgumentCaptor.capture());

        assertEquals(request, resultRequestArgumentCaptor.getValue());
    }

    private ResultRequest getResultRequest() {
        return ResultRequest.builder()
                .clientName(TestConstants.DEFAULT_CLIENT_NAME)
                .build();
    }
}