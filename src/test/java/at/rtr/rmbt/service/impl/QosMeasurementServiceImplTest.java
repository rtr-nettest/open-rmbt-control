package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.TestFixtures;
import at.rtr.rmbt.dto.qos.DnsResult;
import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.mapper.QosTestObjectiveMapper;
import at.rtr.rmbt.mapper.QosTestResultMapper;
import at.rtr.rmbt.model.QosTestDesc;
import at.rtr.rmbt.model.QosTestObjective;
import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.repository.*;
import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosMeasurementsResponse;
import at.rtr.rmbt.response.QosParamsResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class QosMeasurementServiceImplTest {
    private static final at.rtr.rmbt.model.Test test = at.rtr.rmbt.model.Test.builder()
        .uid(DEFAULT_UID)
        .client(client)
        .build();
    private static final HashSet<DnsResult.DnsEntry> dnsEntries = new HashSet<>();
    private static final DnsResult.DnsEntry dnsEntry = DnsResult.DnsEntry.builder().address("addr").priority(((short) 1)).build();
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
    private QosMeasurementService qosMeasurementService;
    @MockBean
    private QosTestObjectiveRepository qosTestObjectiveRepository;
    @MockBean
    private QosTestObjectiveMapper qosTestObjectiveMapper;
    @Mock
    private QosTestTypeDescRepository qosTestTypeDescRepository;
    @Mock
    private QosTestResultMapper qosTestResultMapper;
    @Mock
    private QosTestDescRepository qosTestDescRepository;
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
    private TestRepository testRepository;
    @Mock
    private QosTestResultRepository qosTestResultRepository;
    @Mock
    private MessageSource messageSource;
    private Map<String, String> headers;

    @Before
    public void setUp() throws Exception {
        qosMeasurementService = new QosMeasurementServiceImpl(
            qosTestObjectiveRepository,
            qosTestObjectiveMapper,
            applicationProperties,
            testRepository,
            qosTestResultRepository,
            messageSource,
            mapper,
            qosTestResultMapper,
            qosTestTypeDescRepository,
            qosTestDescRepository
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

        when(testRepository.findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(qosTestObjectiveRepository.findById(qosSendTestResultItem.getQosTestUid())).thenReturn(Optional.of(qosTestObjective));
        when(qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(test.getUid())).thenReturn(List.of(qosTestResult));

        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertTrue(response.getError().isEmpty());
        verify(qosTestResultRepository).save(qosTestResult);
        verify(qosTestResultRepository).save(qosTestResult);
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

        when(testRepository.findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID)).thenReturn(Optional.empty());
        when(testRepository.findByOpenTestUuidAndClientUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID, iosClientUuid)).thenReturn(Optional.of(test));
        when(qosTestObjectiveRepository.findById(qosSendTestResultItem.getQosTestUid())).thenReturn(Optional.of(qosTestObjective));
        when(qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(test.getUid())).thenReturn(List.of(qosTestResult));

        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertTrue(response.getError().isEmpty());
        verify(qosTestResultRepository).save(qosTestResult);
        verify(qosTestResultRepository).save(qosTestResult);
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

        when(testRepository.findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID)).thenReturn(Optional.empty());
        when(testRepository.findByOpenTestUuidAndClientUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID, DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(test));
        when(qosTestObjectiveRepository.findById(qosSendTestResultItem.getQosTestUid())).thenReturn(Optional.of(qosTestObjective));
        when(qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(test.getUid())).thenReturn(List.of(qosTestResult));

        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertTrue(response.getError().isEmpty());
        verify(qosTestResultRepository).save(qosTestResult);
        verify(qosTestResultRepository).save(qosTestResult);
    }

    @Test
    public void saveQosMeasurementResult_whenNoTestFound_expectNothingSaved() {
        when(testRepository.findByUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID)).thenReturn(Optional.empty());
        when(testRepository.findByOpenTestUuidAndClientUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID, DEFAULT_CLIENT_UUID)).thenReturn(Optional.empty());

        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertTrue(response.getError().isEmpty());
        verify(qosTestResultRepository, never()).save(any());
    }

    @Test
    public void saveQosMeasurementResult_whenClientVersionIsNotValid_expectNothingSaved() {
        QosResultRequest qosResultRequest = TestFixtures.qosResultRequest.toBuilder()
            .clientVersion("abc")
            .build();

        when(testRepository.findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(messageSource.getMessage("ERROR_CLIENT_VERSION", null, Locale.ENGLISH)).thenReturn("ERROR_CLIENT_VERSION");
        ErrorResponse response = qosMeasurementService.saveQosMeasurementResult(qosResultRequest);

        assertEquals(1, response.getError().size());
        assertTrue(response.getError().contains("ERROR_CLIENT_VERSION"));
        verify(qosTestResultRepository, never()).save(any());
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
        verify(qosTestResultRepository, never()).save(any());
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
        verify(qosTestResultRepository, never()).save(any());
    }

    @Test
    public void getQosResult_whenCommonData_expectQosResults() throws Exception {
        QosTestDesc qosTestDesc = new QosTestDesc(1L, "timeout", "Timeout", DEFAULT_LANGUAGE);

        when(testRepository.findByUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(test.getUid())).thenReturn(List.of(qosTestResult));
        when(qosTestDescRepository.findByKeysAndLocales(eq(DEFAULT_LANGUAGE), eq(applicationProperties.getLanguage().getSupportedLanguages()), any()))
            .thenReturn(List.of(qosTestDesc));
        when(qosTestResultMapper.toQosTestResultItem(qosTestResult, false)).thenReturn(
            QosMeasurementsResponse.QosTestResultItem.builder()
                .uid(qosTestResult.getUid())
                .testType(qosTestResult.getQosTestObjective().getTestType())
                .result(mapper.readValue(qosTestResult.getResult(), new TypeReference<>() {}))
                .testDesc(qosTestResult.getTestDescription())
                .successCount(qosTestResult.getSuccessCount())
                .failureCount(qosTestResult.getFailureCount())
                .testSummary(qosTestResult.getTestSummary())
                .testResultKeys(qosTestResult.getResultKeyMap().keySet())
                .testResultKeyMap(qosTestResult.getResultKeyMap())
                .nnTestUid(qosTestResult.getQosTestObjective().getUid())
                .qosTestUid(qosTestResult.getQosTestObjective().getUid())
                .testUid(qosTestResult.getTestUid())
                .build()
        );
        QosMeasurementsResponse result = qosMeasurementService.getQosResult(DEFAULT_TEST_UUID, DEFAULT_LANGUAGE, null);

        assertNotNull(result.getEvalTimes());
        assertTrue(result.getError().isEmpty());
    }

    @Test
    public void evaluateQosByOpenTestUUID_whenCommonData_expectQosMeasurementsResponse() {
        when(testRepository.findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(DEFAULT_TEST_OPEN_TEST_UUID)).thenReturn(Optional.of(test));
        when(qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(test.getUid())).thenReturn(List.of(qosTestResult));

        QosMeasurementsResponse result = qosMeasurementService.evaluateQosByOpenTestUUID(DEFAULT_TEST_OPEN_TEST_UUID, DEFAULT_LANGUAGE);

        assertNotNull(result.getEvalTimes());
        assertTrue(result.getError().isEmpty());
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
