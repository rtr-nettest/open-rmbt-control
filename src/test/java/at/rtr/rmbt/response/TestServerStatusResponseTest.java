package at.rtr.rmbt.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestServerStatusResponseTest {

    private static final UUID UUID_VALUE = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    @Test
    void fromRow_mapsAllColumnsAndTypes() {
        final Object[] row = {
                UUID_VALUE, "srv1", "RMBThttp", 4, Boolean.TRUE, 12.5, 30.0, 5.0, new BigDecimal("99.50")
        };

        final TestServerStatusResponse r = TestServerStatusResponse.fromRow(row);

        assertEquals(UUID_VALUE, r.getServerUuid());
        assertEquals("srv1", r.getName());
        assertEquals("RMBThttp", r.getServerType());
        assertEquals(4, r.getProtocol());
        assertTrue(r.getReachable());
        assertEquals(12.5, r.getLatencyMs());
        assertEquals(30.0, r.getMaxLatencyMs());
        assertEquals(5.0, r.getMinLatencyMs());
        assertEquals(99.5, r.getReachabilityPct()); // BigDecimal → Double
    }

    @Test
    void fromRow_acceptsUuidAsStringAndToleratesNullAggregates() {
        final Object[] row = {UUID_VALUE.toString(), "srv2", "RMBTudp", 6, Boolean.FALSE, null, null, null, null};

        final TestServerStatusResponse r = TestServerStatusResponse.fromRow(row);

        assertEquals(UUID_VALUE, r.getServerUuid()); // String → UUID
        assertEquals("RMBTudp", r.getServerType());
        assertEquals(6, r.getProtocol());
        assertFalse(r.getReachable());
        assertNull(r.getLatencyMs());
        assertNull(r.getMaxLatencyMs());
        assertNull(r.getReachabilityPct());
    }
}
