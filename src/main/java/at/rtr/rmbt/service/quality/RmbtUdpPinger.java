package at.rtr.rmbt.service.quality;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Pinger for RMBTudp servers (the {@code open-rmbt-udp-ping} protocol). Sends a few {@code RP01} UDP
 * packets and reports the <b>minimum</b> round-trip; any valid reply — {@code RR01} (source-IP
 * confirmed) or {@code RE01} (source-IP HMAC mismatch) — counts as "reachable" (we usually do not know
 * the control server's public source IP, so {@code RE01} is the expected, fine answer).
 *
 * <p>Taking the minimum matters: the first packet on a fresh datagram socket pays the kernel's
 * ARP/route-resolution (and JIT/scheduler) cost — on a LAN that alone can be ~10–20 ms — while
 * subsequent packets are clean, so the minimum reflects the true latency. Never throws.
 */
@Component
@Slf4j
public class RmbtUdpPinger {

    private static final int PING_COUNT = 5;
    private static final int PER_PACKET_TIMEOUT_MS = 1_000;
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
            socket.setSoTimeout(PER_PACKET_TIMEOUT_MS);
            final InetAddress addr = InetAddress.getByName(host);
            final DatagramPacket out = new DatagramPacket(request, request.length, addr, port);

            double bestMs = Double.POSITIVE_INFINITY;
            int valid = 0;
            int consecutiveTimeouts = 0;

            // Send several pings and keep the MINIMUM round-trip — the first packet pays the ARP/route
            // resolution cost (see class doc), later ones are clean. Bail out early if the server is
            // clearly down (two unanswered probes) or only ever gives an unaccepted reply.
            for (int i = 0; i < PING_COUNT; i++) {
                final DatagramPacket in = new DatagramPacket(new byte[64], 64);
                try {
                    final long t0 = System.nanoTime();
                    socket.send(out);
                    socket.receive(in); // SocketTimeoutException after PER_PACKET_TIMEOUT_MS
                    final long rttNs = System.nanoTime() - t0;
                    if (isValidResponse(in, request, requireIpMatch)) {
                        valid++;
                        consecutiveTimeouts = 0;
                        bestMs = Math.min(bestMs, rttNs / 1_000_000.0);
                    } else if (valid == 0) {
                        // Reply received but not accepted (e.g. RE01 while requireIpMatch) — it will not
                        // change, so stop probing.
                        log.debug("UDP ping {}:{} unaccepted response (requireIpMatch={})", host, port, requireIpMatch);
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    if (valid == 0 && ++consecutiveTimeouts >= 2) {
                        break; // no answer to the first probes → unreachable, don't keep waiting
                    }
                }
            }

            if (valid == 0) {
                log.debug("UDP ping {}:{} unreachable", host, port);
                return PingOutcome.unreachable();
            }
            log.debug("UDP ping {}:{} reachable, latency={}ms (min of {} replies)", host, port, bestMs, valid);
            return PingOutcome.reachable(bestMs);
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
