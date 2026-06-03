package at.rtr.rmbt.utils;

import org.junit.jupiter.api.Test;

import java.net.Inet6Address;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies that {@link HelperFunctions#reverseDNSLookup(InetAddress)} resolves an IPv6 address,
 * proving the reverse lookup is not limited to IPv4.
 *
 * <p>Uses Google Public DNS {@code 2001:4860:4860::8888}, whose PTR record is the stable, globally
 * published {@code dns.google}. This test performs a real DNS query and therefore needs outbound
 * DNS / network access.
 */
class HelperFunctionsReverseDnsTest {

    @Test
    void reverseDNSLookup_resolvesIpv6GooglePublicDnsToDnsGoogle() throws Exception {
        final InetAddress address = InetAddress.getByName("2001:4860:4860::8888");
        assertInstanceOf(Inet6Address.class, address, "expected an IPv6 address");

        final String reverse = HelperFunctions.reverseDNSLookup(address);

        assertNotNull(reverse, "reverse lookup returned null - no PTR resolved for the IPv6 address");
        // dnsjava returns the fully qualified name with a trailing dot, e.g. "dns.google."
        assertEquals("dns.google", reverse.replaceFirst("\\.$", ""));
    }
}
