package at.rtr.rmbt.utils;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UuidUtilsTest {

    private static final String VALID = "46e6e982-d654-4bba-9dc4-0242ac130002";
    // The value that crashed /result: 'orf' is not hex and the last group is too short.
    private static final String MALFORMED = "46e6e982-d654-4bba-9dc4-orf";

    @Test
    void isValidUuidFormat_acceptsStandard36CharUuid() {
        assertTrue(UuidUtils.isValidUuidFormat(VALID));
        assertTrue(UuidUtils.isValidUuidFormat(VALID.toUpperCase()));
    }

    @Test
    void isValidUuidFormat_rejectsMalformedNullAndWrongLength() {
        assertFalse(UuidUtils.isValidUuidFormat(MALFORMED));
        assertFalse(UuidUtils.isValidUuidFormat(null));
        assertFalse(UuidUtils.isValidUuidFormat(""));
        assertFalse(UuidUtils.isValidUuidFormat("null"));
        assertFalse(UuidUtils.isValidUuidFormat("46e6e982d6544bba9dc40242ac130002")); // no dashes
        assertFalse(UuidUtils.isValidUuidFormat("zzzzzzzz-d654-4bba-9dc4-0242ac130002")); // non-hex
    }

    @Test
    void toUuidOrNull_parsesValid_filtersEverythingElse() {
        assertEquals(UUID.fromString(VALID), UuidUtils.toUuidOrNull(VALID));
        assertEquals(UUID.fromString(VALID), UuidUtils.toUuidOrNull("  " + VALID + "  ")); // trimmed
        assertNull(UuidUtils.toUuidOrNull(MALFORMED));
        assertNull(UuidUtils.toUuidOrNull(null));
        assertNull(UuidUtils.toUuidOrNull(""));
        assertNull(UuidUtils.toUuidOrNull("null"));
    }
}
