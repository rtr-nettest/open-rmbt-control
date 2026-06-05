package at.rtr.rmbt.utils;

import at.rtr.rmbt.dto.ASInformation;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration tests for the network-based AS-information resolvers (cymru.com DNS and iptoasn.com
 * HTTP), exercised via {@link HelperFunctions#getASInformation(InetAddress, HelperFunctions.AsSource)}.
 * They need outbound network access and are tagged {@code "integration"} so they can be excluded from
 * fast unit runs. Each test skips (via {@code assumeTrue}) when its source is unreachable, and asserts
 * the AS number otherwise.
 *
 * <p>The MaxMind resolver is intentionally not covered here: it reads the local MaxMind databases,
 * which are not present in the dev/build environment.
 *
 * <p>131.130.190.232 belongs to the University of Vienna, which is AS760.
 */
@Tag("integration")
class HelperFunctionsAsInformationIntegrationTest {

    private static final long EXPECTED_ASN = 760L;
    private static final String UNI_VIENNA_IP = "131.130.190.232";

    @Test
    void cymru_resolvesUniViennaToAs760() throws Exception {
        final ASInformation info = HelperFunctions.getASInformation(
                InetAddress.getByName(UNI_VIENNA_IP), HelperFunctions.AsSource.CYMRU);

        assumeTrue(info != null, "cymru.com DNS API not reachable in this environment");
        assertEquals(EXPECTED_ASN, info.getNumber());
    }

    @Test
    void ipToAsn_resolvesUniViennaToAs760() throws Exception {
        final ASInformation info = HelperFunctions.getASInformation(
                InetAddress.getByName(UNI_VIENNA_IP), HelperFunctions.AsSource.IPTOASN);

        assumeTrue(info != null, "iptoasn.com HTTP API not reachable in this environment");
        assertEquals(EXPECTED_ASN, info.getNumber());
    }
}
