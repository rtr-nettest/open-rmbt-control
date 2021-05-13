package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.dto.qos.AbstractResult;
import at.rtr.rmbt.dto.qos.ResultDesc;
import at.rtr.rmbt.dto.qos.ResultOptions;
import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.exception.HstoreParseException;
import at.rtr.rmbt.mapper.QosTestObjectiveMapper;
import at.rtr.rmbt.mapper.QosTestResultMapper;
import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.*;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.MeasurementQosResponse;
import at.rtr.rmbt.response.QosMeasurementsResponse;
import at.rtr.rmbt.response.QosParamsResponse;
import at.rtr.rmbt.service.QosMeasurementService;
import at.rtr.rmbt.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.InetAddresses;
import com.vdurmont.semver4j.SemverException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QosMeasurementServiceImpl implements QosMeasurementService {
    private static final Logger logger = LoggerFactory.getLogger(QosMeasurementServiceImpl.class);
    private static final int STATIC_CLASS_NAME = 1;
    private final QosTestObjectiveRepository qosTestObjectiveRepository;
    private final QosTestObjectiveMapper qosTestObjectiveMapper;
    private final ApplicationProperties applicationProperties;
    private final TestRepository testRepository;
    private final QosTestResultRepository qosTestResultRepository;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;
    private final QosTestResultMapper qosTestResultMapper;
    private final QosTestTypeDescRepository qosTestTypeDescRepository;
    private final QosTestDescRepository qosTestDescRepository;

    @Value("${RMBT_SECRETKEY}")
    private String rmbtSecretKey;

    @Override
    public MeasurementQosResponse getQosParameters(HttpServletRequest httpServletRequest, Map<String, String> headers) {
        InetAddress clientAddress = InetAddresses.forString(HeaderExtrudeUtil.getIpFromNgNixHeader(httpServletRequest, headers));
        String clientIpString = InetAddresses.toAddrString(clientAddress);

        Map<TestType, List<QosParamsResponse>> objectives = new HashMap<>();
        qosTestObjectiveRepository.getByTestClassIdIn(List.of(STATIC_CLASS_NAME)).forEach(qosTestObjective -> {
            List<QosParamsResponse> paramsList;

            if (objectives.containsKey(qosTestObjective.getTestType())) {
                paramsList = objectives.get(qosTestObjective.getTestType());
            } else {
                paramsList = new ArrayList<>();
                objectives.put(qosTestObjective.getTestType(), paramsList);
            }

            QosParamsResponse params = qosTestObjectiveMapper.qosTestObjectiveToQosParamsResponse(qosTestObjective, clientAddress);

            paramsList.add(params);
        });

        return MeasurementQosResponse.builder()
            .objectives(objectives)
            .testDuration(applicationProperties.getDuration())
            .testNumThreads(applicationProperties.getThreads())
            .testNumPings(applicationProperties.getPings())
            .clientRemoteIp(clientIpString)
            .error(Collections.emptyList())
            .build();
    }

    @Override
    public ErrorResponse saveQosMeasurementResult(QosResultRequest request) {
        final ErrorResponse errorList = new ErrorResponse();
        final String lang = request.getClientLanguage();
        Locale locale = MessageUtils.getLocaleFormLanguage(lang, applicationProperties.getLanguage());
        ResultOptions resultOptions = new ResultOptions(new Locale(lang));

        if (StringUtils.isNotBlank(request.getTestToken())) {
            final String[] token = request.getTestToken().split("_");

            try {
                final UUID testUuid = UUID.fromString(token[0]);
                final UUID clientUuid = getClientUuid(request);
                final String data = token[0] + "_" + token[1];
                final String hmac = HelperFunctions.calculateHMAC(rmbtSecretKey.getBytes(), data);

                if (hmac.length() == 0) {
                    errorList.addErrorString(messageSource.getMessage("ERROR_TEST_TOKEN", null, locale));
                }

                if (token.length > 2 && token[2].length() > 0) { // && hmac.equals(token[2])) (can be different server keys)
                    final Set<String> clientNames = applicationProperties.getClientNames();
                    ValidateUtils.validateClientVersion(applicationProperties.getVersion(), request.getClientVersion());
                    Test test = testRepository.findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(testUuid)
                        .orElseGet(() -> {
                            if (clientUuid != null)
                                return testRepository.findByOpenTestUuidAndClientUuidAndImplausibleIsFalseAndDeletedIsFalse(testUuid, clientUuid).orElse(null);
                            return null;
                        });
                    if (test != null) {
                        if (clientNames.contains(request.getClientName())) { //save qos test results:
                            List<QosSendTestResultItem> qosResult = request.getQosResults();
                            if (qosResult != null) {
                                saveQosTestResults(test, qosResult);
                            }

                            List<QosTestResult> testResultList = qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(test.getUid());
                            //map that contains all test types and their result descriptions determined by the test result <-> test objectives comparison
                            Map<TestType, TreeSet<ResultDesc>> resultKeys = new HashMap<>();

                            for (QosTestResult testResult : testResultList) { // iterate through all result entries
                                compareResultsAndSave(resultOptions, resultKeys, testResult);
                            }
                        } else {
                            errorList.addErrorString(messageSource.getMessage("ERROR_CLIENT_VERSION", null, locale));
                        }
                    }
                } else {
                    errorList.addErrorString(messageSource.getMessage("ERROR_TEST_TOKEN_MALFORMED", null, locale));
                }
            } catch (final IllegalArgumentException | IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                errorList.addErrorString(messageSource.getMessage("ERROR_TEST_TOKEN_MALFORMED", null, locale));
            } catch (HstoreParseException e) {
                logger.error(e.getMessage(), e);
                errorList.addErrorString(messageSource.getMessage("ERROR_DB_CONNECTION", null, locale));
            } catch (SemverException e) {
                errorList.addErrorString(messageSource.getMessage("ERROR_CLIENT_VERSION", null, locale));
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            errorList.addErrorString(messageSource.getMessage("ERROR_TEST_TOKEN_MISSING", null, locale));
        }

        return errorList;
    }

    @Override
    public QosMeasurementsResponse getQosResult(UUID qosTestUuid, String language, CapabilitiesRequest capabilitiesRequest) {
        final ErrorResponse errorList = new ErrorResponse();
        Locale locale = MessageUtils.getLocaleFormLanguage(language, applicationProperties.getLanguage());
        QosMeasurementsResponse.QosMeasurementsResponseBuilder answer = QosMeasurementsResponse.builder();
        try {
            QosUtil.evaluate(
                answer,
                qosTestResultMapper,
                qosTestTypeDescRepository,
                messageSource,
                applicationProperties,
                testRepository,
                qosTestResultRepository,
                qosTestUuid,
                false,
                objectMapper,
                qosTestDescRepository,
                locale,
                errorList,
                capabilitiesRequest
            );
        } catch (final JSONException | IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            errorList.addErrorString(messageSource.getMessage("ERROR_REQUEST_JSON", null, locale));
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            errorList.addErrorString(messageSource.getMessage("ERROR_DB_CONNECTION", null, locale));
        } catch (HstoreParseException e) {
            logger.error(e.getMessage(), e);
            errorList.addErrorString(e.getMessage());
        } catch (IllegalAccessException | JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedOperationException e) {
            logger.error(e.getMessage(), e);
            errorList.addErrorString(messageSource.getMessage("ERROR_REQUEST_QOS_RESULT_DETAIL_NO_UUID", null, locale));
        }

        QosMeasurementsResponse response = answer.build();
        response.getError().addAll(errorList.getError());

        return response;
    }

    @Override
    public QosMeasurementsResponse evaluateQosByOpenTestUUID(UUID openTestUUID, String lang) {
        Locale locale = MessageUtils.getLocaleFormLanguage(lang, applicationProperties.getLanguage());
        QosMeasurementsResponse.QosMeasurementsResponseBuilder answer = QosMeasurementsResponse.builder();
        final ErrorResponse errorList = new ErrorResponse();
        try {
            QosUtil.evaluate(
                    answer,
                    qosTestResultMapper,
                    qosTestTypeDescRepository,
                    messageSource,
                    applicationProperties,
                    testRepository,
                    qosTestResultRepository,
                    openTestUUID,
                    true,
                    objectMapper,
                    qosTestDescRepository,
                    locale,
                    errorList,
                    getDefaultCapabilitiesRequest()
            );
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            errorList.addErrorString(messageSource.getMessage("ERROR_DB_CONNECTION", null, locale));
        } catch (HstoreParseException e) {
            logger.error(e.getMessage(), e);
            errorList.addErrorString(e.getMessage());
        } catch (IllegalAccessException | JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedOperationException e) {
            logger.error(e.getMessage(), e);
            errorList.addErrorString(messageSource.getMessage("ERROR_REQUEST_QOS_RESULT_DETAIL_NO_UUID", null, locale));
        }

        QosMeasurementsResponse response = answer.build();
        response.getError().addAll(errorList.getError());

        return response;
    }

    private CapabilitiesRequest getDefaultCapabilitiesRequest() {
        return CapabilitiesRequest.builder()
                .classification(ClassificationRequest.builder().count(Constants.DEFAULT_CLASSIFICATION_COUNT).build())
                .qos(QosRequest.builder().supportsInfo(Constants.DEFAULT_QOS_SUPPORTS_INFO).build())
                .rmbtHttp(Constants.DEFAULT_RMBT_HTTP)
                .build();
    }

    private void saveQosTestResults(Test test, List<QosSendTestResultItem> qosResult) throws JsonProcessingException {
        for (QosSendTestResultItem testObject : qosResult) {
            QosSendTestResultItem resultJson = testObject.toBuilder()
                .testType(null)
                .qosTestUid(null)
                .build();
            QosTestResult testResult = new QosTestResult();
            testResult.setResult(objectMapper.writeValueAsString(resultJson));
            testResult.setTestUid(test.getUid());
            testResult.setSuccessCount(0);
            testResult.setFailureCount(0);
            long qosTestId = testObject.getQosTestUid() != null ? testObject.getQosTestUid() : Long.MIN_VALUE;
            qosTestObjectiveRepository.findById(qosTestId).ifPresent(testResult::setQosTestObjective);
            qosTestResultRepository.save(testResult);
        }
    }

    private void compareResultsAndSave(ResultOptions resultOptions, Map<TestType, TreeSet<ResultDesc>> resultKeys, QosTestResult testResult) throws JsonProcessingException, HstoreParseException, IllegalAccessException {
        TestType testType = testResult.getQosTestObjective().getTestType(); //get the correct class of the result;
        testResult.setFailureCount(0);
        testResult.setSuccessCount(0);
        Class<? extends AbstractResult<?>> clazz = testType.getClazz();
        AbstractResult<?> result = objectMapper.readValue(testResult.getResult(), clazz);
        result.setResultMap(objectMapper.readValue(testResult.getResult(), new TypeReference<>() {
        }));
        testResult.setResult(objectMapper.writeValueAsString(result));
        //compare test results with expected results
        QosUtil.compareTestResults(testResult, result, resultKeys, testType, resultOptions, objectMapper);

        //update all test results after the success and failure counters have been set
        qosTestResultRepository.save(testResult);
    }

    private UUID getClientUuid(QosResultRequest request) {
        if (StringUtils.isNotBlank(request.getAndroidClientUUID())) { //Android
            return UUID.fromString(request.getAndroidClientUUID());
        } else if (StringUtils.isNotBlank(request.getIosClientUUID())) { //iOS
            return UUID.fromString(request.getIosClientUUID());
        }
        return null;
    }
}
