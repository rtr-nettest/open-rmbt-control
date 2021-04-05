package at.rtr.rmbt.utils;

import at.rtr.rmbt.dto.qos.*;
import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.exception.HstoreParseException;
import at.rtr.rmbt.model.QosTestResult;
import at.rtr.rmbt.utils.hstoreparser.Hstore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
                for (final  AbstractResult<?> expResult : expectedResults) {
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
