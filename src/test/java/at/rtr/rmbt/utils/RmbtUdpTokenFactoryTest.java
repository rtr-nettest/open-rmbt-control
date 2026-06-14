package at.rtr.rmbt.utils;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RmbtUdpTokenFactoryTest {

    private static final String SEED = "topsecret";
    private static final long TS = 1_700_000_000L;

    private static byte[] hmacSha256(final byte[] key, final byte[] data) throws Exception {
        final Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    @Test
    void createToken_layoutMatchesProtocol() throws Exception {
        final InetAddress ip = InetAddress.getByName("62.1.2.3");
        final byte[] token = RmbtUdpTokenFactory.createToken(SEED, ip, TS);
        assertEquals(16, token.length);

        // [0..4] time = big-endian u32 of the timestamp
        final byte[] timeBytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt((int) TS).array();
        assertArrayEquals(timeBytes, Arrays.copyOfRange(token, 0, 4));

        final byte[] key = SEED.getBytes(StandardCharsets.UTF_8);

        // [4..12] = HMAC-SHA256(seed, time)[0..8]
        assertArrayEquals(Arrays.copyOf(hmacSha256(key, timeBytes), 8), Arrays.copyOfRange(token, 4, 12));

        // [12..16] = HMAC-SHA256(seed, time ‖ ip16)[0..4]
        final byte[] ip16 = RmbtUdpTokenFactory.toMappedIpv6Bytes(ip);
        final byte[] ipInput = new byte[timeBytes.length + ip16.length];
        System.arraycopy(timeBytes, 0, ipInput, 0, timeBytes.length);
        System.arraycopy(ip16, 0, ipInput, timeBytes.length, ip16.length);
        assertArrayEquals(Arrays.copyOf(hmacSha256(key, ipInput), 4), Arrays.copyOfRange(token, 12, 16));
    }

    @Test
    void createRequestPacket_isMagicThenSequenceThenToken() {
        final byte[] token = new byte[16];
        for (int i = 0; i < 16; i++) {
            token[i] = (byte) i;
        }
        final byte[] packet = RmbtUdpTokenFactory.createRequestPacket(1, token);

        assertEquals(24, packet.length);
        assertArrayEquals("RP01".getBytes(StandardCharsets.US_ASCII), Arrays.copyOfRange(packet, 0, 4));
        assertArrayEquals(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(1).array(),
                Arrays.copyOfRange(packet, 4, 8));
        assertArrayEquals(token, Arrays.copyOfRange(packet, 8, 24));
    }

    @Test
    void createRequestPacket_rejectsWrongTokenLength() {
        assertThrows(IllegalArgumentException.class, () -> RmbtUdpTokenFactory.createRequestPacket(1, new byte[10]));
    }

    @Test
    void toMappedIpv6Bytes_mapsIpv4() throws Exception {
        final byte[] mapped = RmbtUdpTokenFactory.toMappedIpv6Bytes(InetAddress.getByName("192.168.1.1"));
        assertEquals(16, mapped.length);
        assertEquals((byte) 0xff, mapped[10]);
        assertEquals((byte) 0xff, mapped[11]);
        assertEquals((byte) 192, mapped[12]);
        assertEquals((byte) 168, mapped[13]);
        assertEquals((byte) 1, mapped[14]);
        assertEquals((byte) 1, mapped[15]);
    }

    @Test
    void toMappedIpv6Bytes_passesIpv6Through() throws Exception {
        final InetAddress v6 = InetAddress.getByName("2001:db8::1");
        assertArrayEquals(v6.getAddress(), RmbtUdpTokenFactory.toMappedIpv6Bytes(v6));
    }
}
