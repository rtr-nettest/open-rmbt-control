package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.ProviderResponse;
import at.rtr.rmbt.service.ProviderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class ProviderControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private ProviderService providerService;

    @Before
    public void setUp() {
        ProviderController providerController = new ProviderController(providerService);
        mockMvc = MockMvcBuilders.standaloneSetup(providerController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void getAllProviders_whenCommonData_expectListOfProviderResponse() throws Exception {
        var response = getAllProvidersResponse();
        when(providerService.getAllProviders()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(URIConstants.PROVIDERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(TestConstants.DEFAULT_UID))
                .andExpect(jsonPath("$[0].name").value(TestConstants.DEFAULT_PROVIDER));

        verify(providerService).getAllProviders();
    }

    private List<ProviderResponse> getAllProvidersResponse() {
        var providerResponse = ProviderResponse.builder()
                .id(TestConstants.DEFAULT_UID)
                .name(TestConstants.DEFAULT_PROVIDER)
                .build();
        return List.of(providerResponse);
    }
}