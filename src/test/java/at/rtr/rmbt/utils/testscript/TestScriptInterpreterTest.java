package at.rtr.rmbt.utils.testscript;

import at.rtr.rmbt.dto.qos.ResultOptions;
import at.rtr.rmbt.dto.qos.VoipResult;
import at.rtr.rmbt.utils.testscript.TestScriptInterpreter.EvalResult;
import at.rtr.rmbt.utils.testscript.TestScriptInterpreter.EvalResult.EvalResultType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Exercises the GraalJS-backed {@link TestScriptInterpreter} end-to-end with the real
 * {@code %EVAL ...%} QoS rule shapes (host {@code nn} calls, scalar boolean results, and the
 * {@code result = {type, key}} object-literal signal).
 */
class TestScriptInterpreterTest {

    private final ResultOptions options = new ResultOptions(Locale.ENGLISH);

    private static VoipResult voipWith(Map<String, Object> resultMap) {
        final VoipResult result = new VoipResult();
        result.setResultMap(resultMap);
        return result;
    }

    private Object eval(String script, Map<String, Object> resultMap) {
        return TestScriptInterpreter.interprete(script, null, voipWith(resultMap), false, options);
    }

    @Test
    void coalesce_withPresentValue_returnsValueAndPassesThreshold() {
        final Map<String, Object> map = new HashMap<>();
        map.put("voip_result_in_mean_jitter", 1214508);

        final Object result = eval(
            "%EVAL if (nn.coalesce(voip_result_in_mean_jitter, 50000000) < 50000000) result=true; else result=false;%",
            map);

        assertEquals("true", String.valueOf(result));
    }

    @Test
    void coalesce_withPresentNullValue_usesFallbackAndFailsThreshold() {
        // value present but null -> coalesce returns the 50000000 fallback, which is not < 50000000
        final Map<String, Object> map = new HashMap<>();
        map.put("voip_result_in_mean_jitter", null);

        final Object result = eval(
            "%EVAL if (nn.coalesce(voip_result_in_mean_jitter, 50000000) < 50000000) result=true; else result=false;%",
            map);

        assertEquals("false", String.valueOf(result));
    }

    @Test
    void incomingPacketCheck_withPresentCount_passes() {
        final Map<String, Object> map = new HashMap<>();
        map.put("voip_result_in_num_packets", 100);

        final Object result = eval(
            "%EVAL if (nn.coalesce(voip_result_in_num_packets, 0) > 0) result=true; else result=false;%",
            map);

        assertEquals("true", String.valueOf(result));
    }

    @Test
    void objectLiteralResult_returnsEvalResultWithTypeAndKey() {
        final Map<String, Object> map = new HashMap<>();
        map.put("voip_result_status", "TIMEOUT");

        final Object result = eval(
            "%EVAL if (voip_result_status=='TIMEOUT') result={type: 'failure', key: 'voip.timeout'}%",
            map);

        final EvalResult evalResult = assertInstanceOf(EvalResult.class, result);
        assertEquals(EvalResultType.FAILURE, evalResult.getType());
        assertEquals("voip.timeout", evalResult.getResultKey());
    }

    @Test
    void missingVariable_isSwallowedAsNullResult() {
        // voip_result_in_mean_jitter is absent from the result map -> ReferenceError; eval() swallows
        // it and returns null (the comparator later scores String.valueOf(null) = "null" as failure),
        // matching the previous Nashorn behaviour.
        final Object result = eval(
            "%EVAL if (nn.coalesce(voip_result_in_mean_jitter, 50000000) < 50000000) result=true; else result=false;%",
            new HashMap<>());

        assertEquals("null", String.valueOf(result));
    }
}
