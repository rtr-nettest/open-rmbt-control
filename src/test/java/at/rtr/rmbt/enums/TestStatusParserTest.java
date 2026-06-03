package at.rtr.rmbt.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the lenient {@link TestStatusParser}: known values parse, unknown values (e.g. a client
 * sending an invalid status) are reported as unknown and parse to empty rather than throwing.
 */
class TestStatusParserTest {

    @Test
    void knownValueParses() {
        assertEquals(TestStatus.STARTED, TestStatusParser.parse("STARTED").orElseThrow());
        assertEquals(TestStatus.STARTED, TestStatusParser.parse("  STARTED  ").orElseThrow());
    }

    @Test
    void knownValueIsNotUnknown() {
        assertFalse(TestStatusParser.isUnknown("STARTED"));
    }

    @Test
    void unknownValueParsesToEmptyAndIsFlaggedUnknown() {
        // e.g. a client erroneously sending "SPEEDTEST_END", which is not a valid status
        assertTrue(TestStatusParser.parse("SPEEDTEST_END").isEmpty());
        assertTrue(TestStatusParser.isUnknown("SPEEDTEST_END"));
    }

    @Test
    void blankAndNullAreEmptyButNotFlaggedUnknown() {
        assertTrue(TestStatusParser.parse(null).isEmpty());
        assertTrue(TestStatusParser.parse("").isEmpty());
        assertTrue(TestStatusParser.parse("   ").isEmpty());
        assertFalse(TestStatusParser.isUnknown(null));
        assertFalse(TestStatusParser.isUnknown(""));
        assertFalse(TestStatusParser.isUnknown("   "));
    }
}
