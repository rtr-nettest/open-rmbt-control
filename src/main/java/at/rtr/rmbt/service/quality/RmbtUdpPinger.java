package at.rtr.rmbt.service.quality;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Pinger for RMBTudp servers (the {@code open-rmbt-udp-ping} protocol). Sends one {@code RP01} UDP
 * packet and treats <b>any</b> valid reply — {@code RR01} (source-IP confirmed) or {@code RE01}
 * (source-IP HMAC mismatch) — as "reachable": we do not know the control server's public source IP,
 * so the IP HMAC normally will not match and {@code RE01} is the expected, perfectly fine answer.
 * {@code latency_ms} is the client-measured round-trip. Never throws.
 */
@Component
@Slf4j
public class RmbtUdpPinger {

    private static final int TIMEOUT_MS = 3_000;
    private static final byte[] RESP_OK = "RR01".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] RESP_ERR = "RE01".getBytes(StandardCharsets.US_ASCII);

    /**
     * @param host           server hostname/address (use the IPv4- or IPv6-specific address to force the family)
     * @param port           UDP port
     * @param request        the 24-byte {@code RP01} request packet (see {@code RmbtUdpTokenFactory})
     * @param requireIpMatch when {@code true}, only an {@code RR01} reply (source-IP HMAC confirmed) counts
     *                       as reachable — use this when the request was built with the correct public
     *                       source IP. When {@code false}, {@code RE01} (IP mismatch) is also accepted.
     */
    public PingOutcome ping(final String host, final int port, final byte[] request, final boolean requireIpMatch) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            final InetAddress addr = InetAddress.getByName(host);
            final DatagramPacket out = new DatagramPacket(request, request.length, addr, port);
            final byte[] buf = new byte[64];
            final DatagramPacket in = new DatagramPacket(buf, buf.length);

            final long t0 = System.nanoTime();
            socket.send(out);
            socket.receive(in); // SocketTimeoutException when no reply arrives within TIMEOUT_MS
            final long clientNs = System.nanoTime() - t0;

            if (!isValidResponse(in, request, requireIpMatch)) {
                log.debug("UDP ping {}:{} got unexpected response (requireIpMatch={})", host, port, requireIpMatch);
                return PingOutcome.unreachable();
            }
            final double latencyMs = clientNs / 1_000_000.0;
            log.debug("UDP ping {}:{} reachable, latency={}ms", host, port, latencyMs);
            return PingOutcome.reachable(latencyMs);
        } catch (Exception e) {
            log.debug("UDP ping {}:{} failed: {}", host, port, e.toString());
            return PingOutcome.unreachable();
        }
    }

    /**
     * A valid reply is ≥ 8 bytes, echoes the request sequence, and carries an accepted magic: always
     * {@code RR01} (source-IP confirmed); {@code RE01} (IP mismatch) is accepted only when
     * {@code requireIpMatch} is {@code false}.
     */
    static boolean isValidResponse(final DatagramPacket response, final byte[] request, final boolean requireIpMatch) {
        final byte[] data = response.getData();
        final int len = response.getLength();
        if (len < 8) {
            return false;
        }
        final byte[] magic = Arrays.copyOfRange(data, 0, 4);
        final boolean magicOk = Arrays.equals(magic, RESP_OK)
                || (!requireIpMatch && Arrays.equals(magic, RESP_ERR));
        if (!magicOk) {
            return false;
        }
        // Sequence is request[4..8]; the server echoes it unchanged in response[4..8].
        return Arrays.equals(Arrays.copyOfRange(data, 4, 8), Arrays.copyOfRange(request, 4, 8));
    }
}
