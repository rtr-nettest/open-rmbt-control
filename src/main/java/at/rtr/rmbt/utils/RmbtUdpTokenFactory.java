package at.rtr.rmbt.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Builds the authentication token and request packet for the RMBTudp ping protocol
 * ({@code open-rmbt-udp-ping}). Mirrors the existing control-server mechanism
 * ({@code SignalServiceImpl.generatePingToken}) and the reference {@code makeToken.py} / Rust server.
 *
 * <p>The 16-byte token is {@code time(4) ‖ HMAC-SHA256(seed, time)[0..8] ‖ HMAC-SHA256(seed, time ‖ ip)[0..4]}.
 * The 24-byte request packet prepends the magic and sequence: {@code "RP01" ‖ sequence(4) ‖ token(16)}.
 *
 * <p>The server only answers when the <b>time</b> HMAC is valid and within its window; if the
 * <b>IP</b> HMAC does not match the real source address it replies {@code RE01} instead of {@code RR01}
 * — which, for a reachability probe where we do not know our own public IP, is an acceptable answer.
 */
public final class RmbtUdpTokenFactory {

    private static final byte[] MAGIC = "RP01".getBytes(StandardCharsets.US_ASCII);

    private RmbtUdpTokenFactory() {
    }

    /**
     * @param seed        the test server's shared secret ({@code test_server.key})
     * @param sourceIp    the source IP to embed in the IP HMAC (a placeholder is fine for a probe)
     * @param epochSeconds token time in seconds since the epoch (use "now")
     * @return the 16-byte token
     */
    public static byte[] createToken(final String seed, final InetAddress sourceIp, final long epochSeconds) {
        final byte[] key = seed.getBytes(StandardCharsets.UTF_8);
        final byte[] timeBytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
                .putInt((int) (epochSeconds & 0xFFFFFFFFL)).array();

        final byte[] packetHash = Arrays.copyOf(hmacSha256(key, timeBytes), 8);

        final byte[] ipBytes = toMappedIpv6Bytes(sourceIp);
        final byte[] ipInput = new byte[timeBytes.length + ipBytes.length];
        System.arraycopy(timeBytes, 0, ipInput, 0, timeBytes.length);
        System.arraycopy(ipBytes, 0, ipInput, timeBytes.length, ipBytes.length);
        final byte[] ipHash = Arrays.copyOf(hmacSha256(key, ipInput), 4);

        final byte[] token = new byte[16];
        System.arraycopy(timeBytes, 0, token, 0, 4);
        System.arraycopy(packetHash, 0, token, 4, 8);
        System.arraycopy(ipHash, 0, token, 12, 4);
        return token;
    }

    /** Wraps a 16-byte token into the 24-byte RP01 request: {@code "RP01" ‖ sequence(4 BE) ‖ token(16)}. */
    public static byte[] createRequestPacket(final int sequence, final byte[] token) {
        if (token.length != 16) {
            throw new IllegalArgumentException("token must be 16 bytes, was " + token.length);
        }
        return ByteBuffer.allocate(24).order(ByteOrder.BIG_ENDIAN)
                .put(MAGIC).putInt(sequence).put(token).array();
    }

    /** Convenience: token + request packet in one step. */
    public static byte[] createRequestPacket(final String seed, final InetAddress sourceIp,
                                             final int sequence, final long epochSeconds) {
        return createRequestPacket(sequence, createToken(seed, sourceIp, epochSeconds));
    }

    /** IPv4 → IPv4-mapped IPv6 ({@code ::ffff:a.b.c.d}); IPv6 → as-is. Always 16 bytes. */
    static byte[] toMappedIpv6Bytes(final InetAddress ip) {
        if (ip instanceof Inet6Address) {
            return ip.getAddress();
        }
        if (ip instanceof Inet4Address) {
            final byte[] v4 = ip.getAddress();
            final byte[] mapped = new byte[16];
            mapped[10] = (byte) 0xff;
            mapped[11] = (byte) 0xff;
            System.arraycopy(v4, 0, mapped, 12, 4);
            return mapped;
        }
        throw new IllegalArgumentException("Unsupported IP address type: " + ip);
    }

    private static byte[] hmacSha256(final byte[] key, final byte[] data) {
        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA256 failed", e);
        }
    }
}
