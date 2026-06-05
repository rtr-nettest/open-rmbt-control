package at.rtr.rmbt.utils;

import org.junit.jupiter.api.Test;
import org.xbill.DNS.Name;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Deterministic (no-network) unit tests for the pure parts of the DNS helpers in
 * {@link HelperFunctions}: trailing-dot stripping, reverse-IP name building, and parsing of
 * Team Cymru's pipe-delimited TXT lines (including the previously unsafe field access).
 */
class HelperFunctionsDnsTest {

    @Test
    void stripTrailingDot_removesExactlyOneTrailingDot() {
        assertEquals("dns.google", HelperFunctions.stripTrailingDot("dns.google."));
        assertEquals("dns.google", HelperFunctions.stripTrailingDot("dns.google"));
        assertEquals("", HelperFunctions.stripTrailingDot("."));
        assertNull(HelperFunctions.stripTrailingDot(null));
    }

    @Test
    void getReverseIPName_ipv4_reversesOctets() throws Exception {
        final Name postfix = Name.fromConstantString("origin.asn.cymru.com.");
        final Name name = HelperFunctions.getReverseIPName(InetAddress.getByName("1.2.3.4"), postfix);
        assertEquals("4.3.2.1.origin.asn.cymru.com.", name.toString());
    }

    @Test
    void getReverseIPName_ipv6_reversesNibbles() throws Exception {
        final Name postfix = Name.fromConstantString("origin6.asn.cymru.com.");
        final Name name = HelperFunctions.getReverseIPName(InetAddress.getByName("2001:db8::1"), postfix);
        // ...::1 -> last byte 0x01 -> nibbles "1.0.", preceding 0x00 -> "0.0." => starts "1.0.0.0."
        assertTrue(name.toString().startsWith("1.0.0.0."), name.toString());
        assertTrue(name.toString().endsWith(".origin6.asn.cymru.com."), name.toString());
    }

    @Test
    void cymruField_returnsTrimmedField_orNullWhenAbsent() {
        // origin line: "<asn> | <prefix> | <country> | <registry> | <date>"
        final String origin = "15169 | 8.8.8.0/24 | US | arin | 1992-12-01";
        assertEquals("15169", HelperFunctions.cymruField(origin, 0));
        assertEquals("US", HelperFunctions.cymruField(origin, 2));
        assertNull(HelperFunctions.cymruField(origin, 99));
        assertNull(HelperFunctions.cymruField(origin, -1));
        assertNull(HelperFunctions.cymruField(null, 0));
    }

    @Test
    void cymruField_shortLine_doesNotThrow() {
        // getASName reads field 4, getAScountry reads field 1; a truncated line must yield null,
        // not an ArrayIndexOutOfBoundsException (the bug this fixes).
        assertNull(HelperFunctions.cymruField("15169", 4));
        assertNull(HelperFunctions.cymruField("15169", 1));
    }

    @Test
    void cymruField_asLine_nameAndCountryIndices() {
        // AS line: "<asn> | <country> | <registry> | <date> | <name>"
        final String asLine = "15169 | US | arin | 2000-03-30 | GOOGLE, US";
        assertEquals("US", HelperFunctions.cymruField(asLine, 1));        // country (getAScountry)
        assertEquals("GOOGLE, US", HelperFunctions.cymruField(asLine, 4)); // name (getASName)
    }

    @Test
    void parseCymruAsn_takesFirstAsn_orNull() {
        assertEquals(Long.valueOf(15169), HelperFunctions.parseCymruAsn("15169 | 8.8.8.0/24 | US | arin | 1992"));
        // origin field 0 may list several ASNs, space-separated -> take the first
        assertEquals(Long.valueOf(15169), HelperFunctions.parseCymruAsn("15169 36040 | 8.8.8.0/24 | US"));
        assertNull(HelperFunctions.parseCymruAsn(null));
        assertNull(HelperFunctions.parseCymruAsn(""));
        assertNull(HelperFunctions.parseCymruAsn("not-a-number | x"));
    }
}
