package at.rtr.rmbt.service.quality;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Minimal reachability/latency pinger that just opens a TCP connection and measures the time to
 * establish it (the TCP handshake round-trip). Used for QoS test servers, where a plain TCP connect
 * is enough to confirm the server is up and to get a latency figure — no protocol handshake or token
 * is needed (it works whether or not the port is TLS, since only the TCP connect is performed).
 * Never throws: any failure (connect refused / timeout / unknown host) yields
 * {@link PingOutcome#unreachable()}.
 */
@Component
@Slf4j
public class TcpPinger {

    private static final int CONNECT_TIMEOUT_MS = 5_000;

    /**
     * @param host server hostname/address (use the IPv4- or IPv6-specific address to force the family)
     * @param port TCP port to connect to
     */
    public PingOutcome ping(final String host, final int port) {
        try (Socket socket = new Socket()) {
            final long t0 = System.nanoTime();
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);
            final double latencyMs = (System.nanoTime() - t0) / 1_000_000.0;
            log.debug("TCP ping {}:{} reachable, connect={}ms", host, port, latencyMs);
            return PingOutcome.reachable(latencyMs);
        } catch (Exception e) {
            log.debug("TCP ping {}:{} failed: {}", host, port, e.toString());
            return PingOutcome.unreachable();
        }
    }
}
