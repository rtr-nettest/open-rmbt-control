package at.rtr.rmbt.utils;

import at.rtr.rmbt.dto.qos.*;
import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.exception.HstoreParseException;
import at.rtr.rmbt.mapper.QosTestResultMapper;
import at.rtr.rmbt.model.QosTestDesc;
import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.model.QosTestTypeDesc;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.QosTestDescRepository;
import at.rtr.rmbt.repository.QosTestResultRepository;
import at.rtr.rmbt.repository.QosTestTypeDescRepository;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.request.CapabilitiesRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.QosMeasurementsResponse.*;
import at.rtr.rmbt.utils.hstoreparser.Hstore;
import at.rtr.rmbt.utils.testscript.TestScriptInterpreter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.context.MessageSource;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class QosUtil {

    public static final Hstore HSTORE_PARSER = new Hstore(HttpProxyResult.class, NonTransparentProxyResult.class,
        DnsResult.class, TcpResult.class, UdpResult.class, WebsiteResult.class, VoipResult.class, TracerouteResult.class);

    /**
     * compares test results with expected results and increases success/failure counter
     *
     * @param testResult    the test result
     * @param result        the parsed test result
     * @param resultKeys    result key map
     * @param testType      test type
     * @param resultOptions result options
     * @throws HstoreParseException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void compareTestResults(final QosTestResult testResult, final AbstractResult<?> result,
                                          final Map<TestType, TreeSet<ResultDesc>> resultKeys, final TestType testType,
                                          final ResultOptions resultOptions, final ObjectMapper objectMapper) throws HstoreParseException, IllegalArgumentException, IllegalAccessException, JsonProcessingException {

        //if expected resuls not null, compare them to the test results
        if (testResult.getResult() != null) {
            //create a parsed abstract result set sorted by priority
            final Set<AbstractResult<?>> expResultSet = new TreeSet<>(Comparator.comparing(AbstractResult::getPriority));

            int priority = Integer.MAX_VALUE;

            if (StringUtils.isNotBlank(testResult.getQosTestObjective().getResults())) {
                Class<? extends AbstractResult<?>> testClass = testResult.getQosTestObjective().getTestType().getClazz();
                List<? extends AbstractResult<?>> expectedResults = objectMapper.readerForListOf(testClass)
                    .readValue(testResult.getQosTestObjective().getResults());
                for (final AbstractResult<?> expResult : expectedResults) {
                    //parse hstore string to object
                    if (expResult.getPriority() == Integer.MAX_VALUE) {
                        expResult.setPriority(priority--);
                    }
                    expResultSet.add(expResult);
                }
            }

            for (final AbstractResult<?> expResult : expResultSet) {
                //compare expected result to test result and save the returned id
                ResultDesc resultDesc = ResultComparer.compare(result, expResult, QosUtil.HSTORE_PARSER, resultOptions);
                if (resultDesc != null) {
                    resultDesc.addTestResultUid(testResult.getUid());
                    resultDesc.setTestType(testType);

                    final ResultHolder resultHolder = calculateResultCounter(testResult, expResult, resultDesc);

                    //check if there is a result message
                    if (resultHolder != null) {
                        TreeSet<ResultDesc> resultDescSet;
                        if (resultKeys.containsKey(testType)) {
                            resultDescSet = resultKeys.get(testType);
                        } else {
                            resultDescSet = new TreeSet<>();
                            resultKeys.put(testType, resultDescSet);
                        }

                        resultDescSet.add(resultDesc);

                        testResult.getResultKeyMap().put(resultDesc.getKey(), resultHolder.resultKeyType);

                        if (AbstractResult.BEHAVIOUR_ABORT.equals(resultHolder.event)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * calculates and set the specific result counter
     *
     * @param testResult test result
     * @param expResult  expected test result
     * @param resultDesc result description
     * @return result type string, can be:
     * <ul>
     * 	<li>{@link ResultDesc#STATUS_CODE_SUCCESS}</li>
     * 	<li>{@link ResultDesc#STATUS_CODE_FAILURE}</li>
     * 	<li>{@link ResultDesc#STATUS_CODE_INFO}</li>
     * </ul>
     */
    public static ResultHolder calculateResultCounter(final QosTestResult testResult, final AbstractResult<?> expResult, final ResultDesc resultDesc) {
        String resultKeyType = null;
        String event = AbstractResult.BEHAVIOUR_NOTHING;

        //increase the failure or success counter of this result object
        if (resultDesc.getStatusCode().equals(ResultDesc.STATUS_CODE_SUCCESS)) {
            if (expResult.getOnSuccess() != null) {
                testResult.setSuccessCount(testResult.getSuccessCount() + 1);
                if (AbstractResult.RESULT_TYPE_DEFAULT.equals(expResult.getSuccessType())) {
                    resultKeyType = ResultDesc.STATUS_CODE_SUCCESS;
                } else {
                    resultKeyType = ResultDesc.STATUS_CODE_INFO;
                }

                event = expResult.getOnSuccessBehaivour();
            }
        } else if (resultDesc.getStatusCode().equals(ResultDesc.STATUS_CODE_FAILURE)) {
            if (expResult.getOnFailure() != null) {
                testResult.setFailureCount(testResult.getFailureCount() + 1);
                if (AbstractResult.RESULT_TYPE_DEFAULT.equals(expResult.getFailureType())) {
                    resultKeyType = ResultDesc.STATUS_CODE_FAILURE;
                } else {
                    resultKeyType = ResultDesc.STATUS_CODE_INFO;
                }

                event = expResult.getOnFailureBehaivour();
            }
        } else {
            resultKeyType = ResultDesc.STATUS_CODE_INFO;
            event = AbstractResult.BEHAVIOUR_NOTHING;
        }

        return resultKeyType != null ? new ResultHolder(resultKeyType, event) : null;
    }

    public static void evaluate(
        final QosMeasurementsResponseBuilder answer,
        final QosTestResultMapper qosTestResultMapper,
        final QosTestTypeDescRepository qosTestTypeDescRepository,
        final MessageSource messageSource,
        final ApplicationProperties applicationProperties,
        final TestRepository testRepository,
        final QosTestResultRepository qosTestResultRepository,
        final UUID uuid,
        final boolean isOpenTestUuid,
        final ObjectMapper objectMapper,
        final QosTestDescRepository qosTestDescRepository,
        final Locale locale,
        final ErrorResponse errorList,
        final CapabilitiesRequest capabilities
    ) throws SQLException, HstoreParseException, JSONException, IllegalArgumentException, IllegalAccessException, JsonProcessingException {
        // Load Language Files for Client
        Optional<Test> optionalTest = Optional.empty();

        if (uuid != null) {
            if (isOpenTestUuid) {
                optionalTest = testRepository.findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(uuid);
            } else {
                optionalTest = testRepository.findByUuidAndImplausibleIsFalseAndDeletedIsFalse(uuid);
            }
        }

        final long timeStampFullEval = System.currentTimeMillis();

        if (optionalTest != null && optionalTest.isPresent() && optionalTest.get().getClient() != null) {
            final ResultOptions resultOptions = new ResultOptions(locale);
            final List<QosTestResultItem> resultList = new ArrayList<>();
            Test test = optionalTest.get();

            List<QosTestResult> testResultList = qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(test.getUid());
            try {
                if (testResultList == null || testResultList.isEmpty()) {
                    Thread.sleep(5000);
                    testResultList = qosTestResultRepository.findByTestUidAndImplausibleIsFalseAndDeletedIsFalse(test.getUid());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (testResultList == null || testResultList.isEmpty()) {
                throw new UnsupportedOperationException("test " + test + " has no result list");
            }
            //map that contains all test types and their result descriptions determined by the test result <-> test objectives comparison
            Map<TestType, TreeSet<ResultDesc>> resultKeys = new HashMap<>();

            //test description set:
            Set<String> testDescSet = new TreeSet<>();
            //test summary set:
            Set<String> testSummarySet = new TreeSet<>();


            //Staring timestamp for evaluation time measurement
            final long timeStampEval = System.currentTimeMillis();

            //iterate through all result entries
            for (final QosTestResult testResult : testResultList) {

                //reset test counters
                testResult.setFailureCount(0);
                testResult.setSuccessCount(0);

                //get the correct class of the result;
                TestType testType = testResult.getQosTestObjective().getTestType();

                if (testType == null) {
                    continue;
                }

                Class<? extends AbstractResult<?>> clazz = testType.getClazz();
                //parse hstore data
                if (testResult.getQosTestObjective().getResults() != null) {
                    AbstractResult<?> result = objectMapper.readValue(testResult.getResult(), clazz);
                    result.setResultMap(objectMapper.readValue(testResult.getResult(), new TypeReference<>() {}));

                    //add each test description key to the testDescSet (to fetch it later from the db)
                    if (testResult.getQosTestObjective().getTestDescription() != null) {
                        testDescSet.add(testResult.getQosTestObjective().getTestDescription());
                    }
                    if (testResult.getQosTestObjective().getTestSummary() != null) {
                        testSummarySet.add(testResult.getQosTestObjective().getTestSummary());
                    }
                    testResult.setResult(objectMapper.writeValueAsString(result));

                    //compare test results
                    compareTestResults(testResult, result, resultKeys, testType, resultOptions, objectMapper);
                }
            }

            //ending timestamp for evaluation time measurement
            final long timeStampEvalEnd = System.currentTimeMillis();

            //-------------------------------------------------------------
            //fetch all result strings from the db

            //FIRST: get all test descriptions
            testDescSet.addAll(testSummarySet);

            for (TestType value : TestType.values()) {
                testDescSet.add(value.getDescriptionKey());
                testDescSet.add(value.getNameKey());
            }

            resultKeys.values().forEach(v -> v.forEach(resultDesc -> testDescSet.add(resultDesc.getKey())));

            Map<String, String> testDescMap = qosTestDescRepository.findByKeysAndLocales(locale.getLanguage(), applicationProperties.getLanguage().getSupportedLanguages(), testDescSet)
                .stream()
                .collect(Collectors.toMap(QosTestDesc::getDescKey, QosTestDesc::getValue));

            for (QosTestResult testResult : testResultList) {
                //and set the test results + put each one to the result list json array
                String preParsedDesc = testDescMap.get(testResult.getQosTestObjective().getTestDescription());
                AbstractResult<?> result = objectMapper.readValue(testResult.getResult(), testResult.getQosTestObjective().getTestType().getClazz());
                result.setResultMap(objectMapper.readValue(testResult.getResult(), new TypeReference<>() {}));
                if (preParsedDesc != null) {
                    String description = String.valueOf(TestScriptInterpreter.interprete(
                        preParsedDesc,
                        QosUtil.HSTORE_PARSER,
                        result,
                        true,
                        resultOptions
                    ));
                    testResult.setTestDescription(description);
                }

                //do the same for the test summary:
                String preParsedSummary = testDescMap.get(testResult.getQosTestObjective().getTestSummary());
                if (preParsedSummary != null) {
                    String description = String.valueOf(TestScriptInterpreter.interprete(
                        preParsedSummary,
                        QosUtil.HSTORE_PARSER,
                        result,
                        true,
                        resultOptions
                    ));
                    testResult.setTestSummary(description);
                }

                resultList.add(qosTestResultMapper.toQosTestResultItem(testResult, isOpenTestUuid));
            }

            //finally put results to json
            if (!resultList.isEmpty()) {
                answer.testResultDetails(resultList);
            }

            List<QosTestResultDescItem> resultDescArray = new ArrayList<>();

            //SECOND: fetch all test result descriptions
            for (TestType testType : resultKeys.keySet()) {
                TreeSet<ResultDesc> descSet = resultKeys.get(testType);

                //fetch results to same object
                for (ResultDesc resultDesc : descSet) {
                    resultDesc.setValue(testDescMap.get(resultDesc.getKey()));
                }

                //another tree set for duplicate entries:
                //TODO: there must be a better solution
                //(the issue is: compareTo() method returns diffrent values depending on the .value attribute (if it's set or not))
                TreeSet<ResultDesc> descSetNew = new TreeSet<>();
                //add fetched results to json

                for (ResultDesc desc : descSet) {
                    if (capabilities != null && capabilities.getQos() != null && !capabilities.getQos().isSupportsInfo()) {
                        if (ResultDesc.STATUS_CODE_INFO.equals(desc.getStatusCode())) {
                            continue;
                        }
                    }

                    if (!descSetNew.contains(desc)) {
                        descSetNew.add(desc);
                    } else {
                        for (ResultDesc d : descSetNew) {
                            if (d.compareTo(desc) == 0) {
                                d.getTestResultUidList().addAll(desc.getTestResultUidList());
                            }
                        }
                    }
                }

                for (ResultDesc desc : descSetNew) {
                    if (desc.getValue() != null) {
                        resultDescArray.add(new QosTestResultDescItem(desc.getTestResultUidList(), desc.getTestType().name(), desc.getKey(), desc.getStatusCode(), desc.getParsedValue()));
                    }
                }
            }

            //put result descriptions to json
            answer.testResultDetailDesc(resultDescArray);

            List<QosTestResultTestDescItem> testTypeDescArray = new ArrayList<>();
            for (QosTestTypeDesc desc : qosTestTypeDescRepository.findAll()) {
                if (desc.getTest() != null) {
                    testTypeDescArray.add(new QosTestResultTestDescItem(testDescMap.get(desc.getName()), desc.getTest(), testDescMap.get(desc.getDescription())));
                }
            }

            //put result descriptions to json
            answer.testResultDetailTestDesc(testTypeDescArray);
            answer.evalTimes(new EvalTimes(timeStampEvalEnd - timeStampEval, System.currentTimeMillis() - timeStampFullEval));
        } else {
            errorList.addErrorString(messageSource.getMessage("ERROR_REQUEST_TEST_RESULT_DETAIL_NO_UUID", null, locale));
        }
    }

    /**
     * @author lb
     */
    public static class ResultHolder {
        final String resultKeyType;
        final String event;

        public ResultHolder(final String resultKeyType, final String event) {
            this.resultKeyType = resultKeyType;
            this.event = event;
        }

        public String getResultKeyType() {
            return resultKeyType;
        }

        public String getEvent() {
            return event;
        }
    }
}
