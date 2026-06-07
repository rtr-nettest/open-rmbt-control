package at.rtr.rmbt.service.quality;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TcpPingerTest {

    private final TcpPinger pinger = new TcpPinger();

    @Test
    void ping_reachable_whenPortIsListening() throws Exception {
        final InetAddress loopback = InetAddress.getByName("127.0.0.1");
        try (ServerSocket server = new ServerSocket(0, 1, loopback)) {
            final PingOutcome outcome = pinger.ping("127.0.0.1", server.getLocalPort());

            assertTrue(outcome.reachable());
            assertNotNull(outcome.latencyMs());
            assertTrue(outcome.latencyMs() >= 0.0);
        }
    }

    @Test
    void ping_unreachable_whenPortIsClosed() throws Exception {
        final InetAddress loopback = InetAddress.getByName("127.0.0.1");
        final int closedPort;
        try (ServerSocket server = new ServerSocket(0, 1, loopback)) {
            closedPort = server.getLocalPort();
        } // socket closed here → the port is no longer listening

        final PingOutcome outcome = pinger.ping("127.0.0.1", closedPort);

        assertFalse(outcome.reachable());
        assertNull(outcome.latencyMs());
    }
}
