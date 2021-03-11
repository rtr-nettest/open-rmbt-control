package at.rtr.rmbt.controller;

import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class QosMeasurementControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private QosMeasurementService qosMeasurementService;

    @Before
    public void setUp() {
        QosMeasurementController qosMeasurementController = new QosMeasurementController(qosMeasurementService);
        mockMvc = MockMvcBuilders.standaloneSetup(qosMeasurementController)
                .setControllerAdvice(new RtrAdvice())
                .build();
    }

    @Test
    public void provideMeasurementQosParameters_whenCommonData_expectMeasurementQosResponse() throws Exception {
        var response = getMeasurementQosResponse();
        when(qosMeasurementService.getQosParameters(any())).thenReturn(response);

        mockMvc.perform(post(URIConstants.MEASUREMENT_QOS_REQUEST))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private MeasurementQosResponse getMeasurementQosResponse() {
        return MeasurementQosResponse.builder()
                .build();
    }
}
