package at.rtr.rmbt.service.quality;

/**
 * Result of a single RMBT PING against a test server.
 *
 * @param reachable whether the PING completed (connect + handshake + auth + PONG)
 * @param latencyMs client-measured PING-&gt;PONG round-trip in milliseconds, or {@code null} when unreachable
 */
public record PingOutcome(boolean reachable, Double latencyMs) {

    public static PingOutcome unreachable() {
        return new PingOutcome(false, null);
    }

    public static PingOutcome reachable(final double latencyMs) {
        return new PingOutcome(true, latencyMs);
    }
}
