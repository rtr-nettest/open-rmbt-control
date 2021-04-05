package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.TestFixtures;
import at.rtr.rmbt.dto.qos.DnsResult;
import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.mapper.QosTestObjectiveMapper;
import at.rtr.rmbt.model.QosTestObjective;
import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.repository.QosTestObjectiveRepository;
import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosParamsResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import at.rtr.rmbt.service.QosTestResultService;
import at.rtr.rmbt.service.TestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.InetAddresses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static at.rtr.rmbt.TestConstants.*;
import static at.rtr.rmbt.TestFixtures.*;
import static at.rtr.rmbt.TestUtils.mapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
    @Mock
    private TestService testService;
    @Mock
    private QosTestResultService qosTestResultService;
    @Mock
    private MessageSource messageSource;
    private Map<String, String> headers;
    private static final at.rtr.rmbt.model.Test test = at.rtr.rmbt.model.Test.builder()
        .uid(DEFAULT_UID)
        .build();
    private static final HashSet<DnsResult.DnsEntry> dnsEntries = new HashSet<>();
    private static final DnsResult.DnsEntry dnsEntry = DnsResult.DnsEntry.builder().address("addr").priority(((short)1)).build();
    private static final DnsResult dnsResult = DnsResult.builder()
        .duration(1)
        .entriesFound(2)
        .host("host")
        .record("record")
        .info("info")
        .resultEntries(dnsEntries)
        .resolver("Resolver")
        .status("DONE")
        .timeout(100)
        .build();
    public static QosTestObjective qosTestObjective = new QosTestObjective(
        qosSendTestResultItem.getQosTestUid(),
        TestType.DNS,
        1,
        null,
        null,
        1,
        null
    );

    @Before
    public void setUp() throws Exception {
        qosMeasurementService = new QosMeasurementServiceImpl(qosTestObjectiveRepository,
            qosTestObjectiveMapper,
            applicationProperties,
            testService,
            qosTestResultService,
            messageSource,
            mapper
        );

        ReflectionTestUtils.setField(qosMeasurementService, "rmbtSecretKey", "test_server_key");
        headers = new HashMap<>();
        dnsEntries.add(dnsEntry);

        qosTestObjective.setResults(mapper.writeValueAsString(List.of(dnsResult)));
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

    @Test
    public void saveQosMeasurementResult_whenCommonData_expectEmptyErrorResponse() throws JsonProcessingException {
        QosTestResult qosTestResult = new QosTestResult();
        qosTestResult.setResult(mapper.writeValueAsString(
            qosSendTestResultItem.toBuilder()
                .testType(null)
                .qosTestUid(null)
                .build()
        ));
        qosTestResult.setTestUid(test.getUid());
        qosTestResult.setQosTestObjective(qosTestObjective);

        when(testService.getByOpenTestUuid(DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(qosTestObjectiveRepository.findById(qosSendTestResultItem.getQosTestUid())).thenReturn(Optional.of(qosTestObjective));
        when(qosTestResultService.listByTestUid(test.getUid())).thenReturn(List.of(qosTestResult));

        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertTrue(response.getError().isEmpty());
        verify(qosTestResultService).save(qosTestResult);
        verify(qosTestResultService).save(qosTestResult);
    }

    @Test
    public void saveQosMeasurementResult_whenCommonDataForIos_expectEmptyErrorResponse() throws JsonProcessingException {
        QosTestResult qosTestResult = new QosTestResult();
        qosTestResult.setResult(mapper.writeValueAsString(
            qosSendTestResultItem.toBuilder()
                .testType(null)
                .qosTestUid(null)
                .build()
        ));
        qosTestResult.setTestUid(test.getUid());
        qosTestResult.setQosTestObjective(qosTestObjective);
        UUID iosClientUuid = UUID.randomUUID();
        QosResultRequest qosResultRequest = TestFixtures.qosResultRequest.toBuilder()
            .androidClientUUID(null)
            .iosClientUUID(iosClientUuid.toString())
            .build();

        when(testService.getByOpenTestUuid(DEFAULT_TEST_UUID)).thenReturn(Optional.empty());
        when(testService.getByOpenTestUuidAndClientId(DEFAULT_TEST_UUID, iosClientUuid)).thenReturn(Optional.of(test));
        when(qosTestObjectiveRepository.findById(qosSendTestResultItem.getQosTestUid())).thenReturn(Optional.of(qosTestObjective));
        when(qosTestResultService.listByTestUid(test.getUid())).thenReturn(List.of(qosTestResult));

        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertTrue(response.getError().isEmpty());
        verify(qosTestResultService).save(qosTestResult);
        verify(qosTestResultService).save(qosTestResult);
    }

    @Test
    public void saveQosMeasurementResult_whenNoTestByOpenTestUuid_expectErrorResponse() throws JsonProcessingException {
        QosTestResult qosTestResult = new QosTestResult();
        qosTestResult.setResult(mapper.writeValueAsString(
            qosSendTestResultItem.toBuilder()
                .testType(null)
                .qosTestUid(null)
                .build()
        ));
        qosTestResult.setTestUid(test.getUid());
        qosTestResult.setQosTestObjective(qosTestObjective);

        when(testService.getByOpenTestUuid(DEFAULT_TEST_UUID)).thenReturn(Optional.empty());
        when(testService.getByOpenTestUuidAndClientId(DEFAULT_TEST_UUID, DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(test));
        when(qosTestObjectiveRepository.findById(qosSendTestResultItem.getQosTestUid())).thenReturn(Optional.of(qosTestObjective));
        when(qosTestResultService.listByTestUid(test.getUid())).thenReturn(List.of(qosTestResult));

        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertTrue(response.getError().isEmpty());
        verify(qosTestResultService).save(qosTestResult);
        verify(qosTestResultService).save(qosTestResult);
    }

    @Test
    public void saveQosMeasurementResult_whenNoTestFound_expectNothingSaved() {
        when(testService.getByOpenTestUuid(DEFAULT_TEST_UUID)).thenReturn(Optional.empty());
        when(testService.getByOpenTestUuidAndClientId(DEFAULT_TEST_UUID, DEFAULT_CLIENT_UUID)).thenReturn(Optional.empty());

        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertTrue(response.getError().isEmpty());
        verify(qosTestResultService, never()).save(any());
    }

    @Test
    public void saveQosMeasurementResult_whenClientVersionIsNotValid_expectNothingSaved() {
        QosResultRequest qosResultRequest = TestFixtures.qosResultRequest.toBuilder()
            .clientVersion("abc")
            .build();

        when(testService.getByOpenTestUuid(DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(messageSource.getMessage("ERROR_CLIENT_VERSION", null, Locale.ENGLISH)).thenReturn("ERROR_CLIENT_VERSION");
        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertEquals(1, response.getError().size());
        assertTrue(response.getError().contains("ERROR_CLIENT_VERSION"));
        verify(qosTestResultService, never()).save(any());
    }

    @Test
    public void saveQosMeasurementResult_whenTestTokenInvalid_expectNothingSaved() {
        QosResultRequest qosResultRequest = TestFixtures.qosResultRequest.toBuilder()
            .testToken(DEFAULT_TEST_UUID + "_2")
            .build();

        when(messageSource.getMessage("ERROR_TEST_TOKEN_MALFORMED", null, Locale.ENGLISH)).thenReturn("ERROR_TEST_TOKEN_MALFORMED");
        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertEquals(1, response.getError().size());
        assertTrue(response.getError().contains("ERROR_TEST_TOKEN_MALFORMED"));
        verify(qosTestResultService, never()).save(any());
    }

    @Test
    public void saveQosMeasurementResult_whenTestTokenMissing_expectNothingSaved() {
        QosResultRequest qosResultRequest = TestFixtures.qosResultRequest.toBuilder()
            .testToken("")
            .build();

        when(messageSource.getMessage("ERROR_TEST_TOKEN_MISSING", null, Locale.ENGLISH)).thenReturn("ERROR_TEST_TOKEN_MISSING");
        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertEquals(1, response.getError().size());
        assertTrue(response.getError().contains("ERROR_TEST_TOKEN_MISSING"));
        verify(qosTestResultService, never()).save(any());
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
