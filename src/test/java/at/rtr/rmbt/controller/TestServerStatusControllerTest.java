package at.rtr.rmbt.controller;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestServerStatusControllerTest {

    private static final String UUID_STR = "b527ae00-d8ac-4519-a45c-705a0183ce63";
    private static final UUID UUID_VALUE = UUID.fromString(UUID_STR);

    @Test
    void parseUuid_plainValue() {
        assertEquals(UUID_VALUE, TestServerStatusController.parseUuid(UUID_STR));
    }

    @Test
    void parseUuid_stripsDoubleQuotes() {
        assertEquals(UUID_VALUE, TestServerStatusController.parseUuid("\"" + UUID_STR + "\""));
    }

    @Test
    void parseUuid_stripsSingleQuotes() {
        assertEquals(UUID_VALUE, TestServerStatusController.parseUuid("'" + UUID_STR + "'"));
    }

    @Test
    void parseUuid_stripsQuotesAndWhitespace() {
        assertEquals(UUID_VALUE, TestServerStatusController.parseUuid("  \"" + UUID_STR + "\"  "));
    }

    @Test
    void parseUuid_blankOrNullIsNull() {
        assertNull(TestServerStatusController.parseUuid(null));
        assertNull(TestServerStatusController.parseUuid(""));
        assertNull(TestServerStatusController.parseUuid("   "));
        assertNull(TestServerStatusController.parseUuid("\"\""));
    }

    @Test
    void parseUuid_invalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> TestServerStatusController.parseUuid("not-a-uuid"));
    }
}
