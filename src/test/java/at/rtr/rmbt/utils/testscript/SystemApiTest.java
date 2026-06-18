package at.rtr.rmbt.utils.testscript;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link SystemApi#parseTraceroute(Object)} - now a single, null-safe method so the QoS
 * evaluation script can call it with a null traceroute detail without Nashorn failing to pick an
 * overload ("Can't unambiguously select between ... parseTraceroute").
 */
class SystemApiTest {

    private final SystemApi systemApi = new SystemApi();

    @Test
    void parseTraceroute_null_returnsNullInsteadOfThrowing()  {
        assertNull(systemApi.parseTraceroute(null));
    }

    @Test
    void parseTraceroute_jsonString_parsesHostsAndTimes()  {
        final String json = "[{\"host\":\"10.0.0.1\",\"time\":2000000},{\"host\":\"10.0.0.2\",\"time\":5000000}]";

        final String result = systemApi.parseTraceroute(json);

        assertNotNull(result);
        assertTrue(result.contains("10.0.0.1"), result);
        assertTrue(result.contains("10.0.0.2"), result);
        assertTrue(result.contains("time="), result);
        assertTrue(result.contains("ms"), result);
    }

    @Test
    void parseTraceroute_unsupportedType_returnsNull()  {
        assertNull(systemApi.parseTraceroute(42));
    }

    @Test
    void coalesce_valuePresent_returnsValue() {
        // mirrors nn.coalesce(voip_result_in_mean_jitter, 50000000) when the result is present
        assertEquals(1214508, systemApi.coalesce(1214508, 50000000));
    }

    @Test
    void coalesce_valueNull_returnsFallback() {
        assertEquals(50000000, systemApi.coalesce(null, 50000000));
    }
}
