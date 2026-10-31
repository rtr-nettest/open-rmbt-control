package at.rtr.rmbt.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresArrayUtilsTest {

    @Test
    void parseStringArray_parsesPlainUrlList() {
        assertEquals(
                List.of("https://vie1-httpv4.netztest.at/ip",
                        "https://vie2-httpv4.netztest.at/ip",
                        "https://vie3-httpv4.netztest.at/ip"),
                PostgresArrayUtils.parseStringArray(
                        "{https://vie1-httpv4.netztest.at/ip,https://vie2-httpv4.netztest.at/ip,https://vie3-httpv4.netztest.at/ip}"));
    }

    @Test
    void parseStringArray_handlesQuotedElementsWithEscapes() {
        assertEquals(
                List.of("a,b", "c\"d", "e\\f"),
                PostgresArrayUtils.parseStringArray("{\"a,b\",\"c\\\"d\",\"e\\\\f\"}"));
    }

    @Test
    void parseStringArray_trimsUnquotedWhitespaceAndMapsNull() {
        assertEquals(
                Arrays.asList("x", null, "y"),
                PostgresArrayUtils.parseStringArray("{ x , NULL , y }"));
    }

    @Test
    void parseStringArray_emptyBracesYieldEmptyList() {
        assertTrue(PostgresArrayUtils.parseStringArray("{}").isEmpty());
    }

    @Test
    void parseStringArray_nullOrBlankYieldsNull() {
        assertNull(PostgresArrayUtils.parseStringArray(null));
        assertNull(PostgresArrayUtils.parseStringArray(""));
        assertNull(PostgresArrayUtils.parseStringArray("   "));
    }

    @Test
    void parseStringArray_singleElementWithoutBraces() {
        assertEquals(Collections.singletonList("https://a/ip"),
                PostgresArrayUtils.parseStringArray("https://a/ip"));
    }
}
