package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.mapper.QosTestObjectiveMapper;
import at.rtr.rmbt.model.QosTestObjective;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.QosTestObjectiveRepository;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosParamsResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import com.google.common.net.InetAddresses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class QosMeasurementServiceImplTest {
    private QosMeasurementService qosMeasurementService;

    @MockBean
    private QosTestObjectiveRepository qosTestObjectiveRepository;
    @MockBean
    private QosTestObjectiveMapper qosTestObjectiveMapper;

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private QosTestObjective qosTestObjectiveFirst;
    @Mock
    private QosTestObjective qosTestObjectiveSecond;
    @Mock
    private QosParamsResponse qosParamsResponseFirst;
    @Mock
    private QosParamsResponse qosParamsResponseSecond;

    private Map<String, String> headers;

    private final ApplicationProperties applicationProperties = new ApplicationProperties(
            new ApplicationProperties.LanguageProperties(Set.of("en", "de"), "en"),
            Set.of("RMBT", "RMBTjs", "Open-RMBT", "RMBTws", "HW-PROBE"),
            "0.1.0 || 0.3.0 || ^1.0.0",
            1,
            2,
            3,
            1000,
            2000
    );

    @Before
    public void setUp() {
        qosMeasurementService = new QosMeasurementServiceImpl(qosTestObjectiveRepository, qosTestObjectiveMapper, applicationProperties);
    }

    @Test
    public void getQosParameters_whenCommonData_expectMeasurementQosResponse() {
        var expectedResponse = getMeasurementQosResponse();
        var clientAddress = InetAddresses.forString(TestConstants.DEFAULT_IP_V4);
        when(httpServletRequest.getLocalAddr()).thenReturn(TestConstants.DEFAULT_IP_V4);
        when(qosTestObjectiveRepository.getByTestClassIdIn(List.of(1))).thenReturn(List.of(qosTestObjectiveFirst, qosTestObjectiveSecond));
        when(qosTestObjectiveFirst.getTestType()).thenReturn(TestConstants.DEFAULT_TEST_TYPE);
        when(qosTestObjectiveSecond.getTestType()).thenReturn(TestConstants.DEFAULT_TEST_TYPE);
        when(qosTestObjectiveMapper.qosTestObjectiveToQosParamsResponse(qosTestObjectiveFirst, clientAddress)).thenReturn(qosParamsResponseFirst);
        when(qosTestObjectiveMapper.qosTestObjectiveToQosParamsResponse(qosTestObjectiveSecond, clientAddress)).thenReturn(qosParamsResponseSecond);

        var response = qosMeasurementService.getQosParameters(httpServletRequest, headers);

        assertEquals(expectedResponse, response);
    }

    private MeasurementQosResponse getMeasurementQosResponse() {
        Map<TestType, List<QosParamsResponse>> objectives = new HashMap<>();
        objectives.put(TestConstants.DEFAULT_TEST_TYPE, List.of(qosParamsResponseFirst, qosParamsResponseSecond));

        return MeasurementQosResponse.builder()
                .clientRemoteIp(TestConstants.DEFAULT_IP_V4)
                .testNumPings(applicationProperties.getPings())
                .testDuration(applicationProperties.getDuration())
                .testNumThreads(applicationProperties.getThreads())
                .objectives(objectives)
                .error(Collections.emptyList())
                .build();
    }
}
