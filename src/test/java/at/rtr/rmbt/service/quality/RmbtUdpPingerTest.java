package at.rtr.rmbt.service.quality;

import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RmbtUdpPingerTest {

    /** A 24-byte RP01 request with the given sequence bytes at [4..8]. */
    private static byte[] request(final int s4, final int s5, final int s6, final int s7) {
        final byte[] r = new byte[24];
        System.arraycopy("RP01".getBytes(StandardCharsets.US_ASCII), 0, r, 0, 4);
        r[4] = (byte) s4;
        r[5] = (byte) s5;
        r[6] = (byte) s6;
        r[7] = (byte) s7;
        return r;
    }

    /** An 8-byte response: magic + 4 sequence bytes. */
    private static DatagramPacket response(final String magic, final byte[] seq) {
        final byte[] d = new byte[8];
        System.arraycopy(magic.getBytes(StandardCharsets.US_ASCII), 0, d, 0, 4);
        System.arraycopy(seq, 0, d, 4, 4);
        return new DatagramPacket(d, d.length);
    }

    // ── requireIpMatch = false (unverified probe): RR01 and RE01 both accepted ──

    @Test
    void unverified_acceptsRr01() {
        assertTrue(RmbtUdpPinger.isValidResponse(response("RR01", new byte[]{0, 0, 0, 1}), request(0, 0, 0, 1), false));
    }

    @Test
    void unverified_acceptsRe01() {
        assertTrue(RmbtUdpPinger.isValidResponse(response("RE01", new byte[]{0, 0, 0, 1}), request(0, 0, 0, 1), false));
    }

    // ── requireIpMatch = true (public IP configured): only RR01 accepted ──

    @Test
    void verified_acceptsRr01() {
        assertTrue(RmbtUdpPinger.isValidResponse(response("RR01", new byte[]{0, 0, 0, 1}), request(0, 0, 0, 1), true));
    }

    @Test
    void verified_rejectsRe01() {
        assertFalse(RmbtUdpPinger.isValidResponse(response("RE01", new byte[]{0, 0, 0, 1}), request(0, 0, 0, 1), true));
    }

    // ── general validation ──

    @Test
    void rejectsUnknownMagic() {
        assertFalse(RmbtUdpPinger.isValidResponse(response("RX01", new byte[]{0, 0, 0, 1}), request(0, 0, 0, 1), false));
    }

    @Test
    void rejectsMismatchedSequence() {
        assertFalse(RmbtUdpPinger.isValidResponse(response("RR01", new byte[]{0, 0, 0, 2}), request(0, 0, 0, 1), false));
    }

    @Test
    void rejectsTooShort() {
        final byte[] tooShort = "RR01".getBytes(StandardCharsets.US_ASCII); // 4 bytes
        assertFalse(RmbtUdpPinger.isValidResponse(new DatagramPacket(tooShort, tooShort.length), request(0, 0, 0, 1), false));
    }
}
