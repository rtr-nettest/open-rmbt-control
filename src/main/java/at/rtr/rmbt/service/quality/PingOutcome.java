package at.rtr.rmbt.service.quality;

/**
 * Result of a single RMBT PING against a test server.
 *
 * @param reachable whether the PING completed (connect + handshake + auth + PONG)
 * @param latencyMs client-measured PING-&gt;PONG round-trip in milliseconds, or {@code null} when unreachable
 * @param detail    short human-readable reason when unreachable (e.g. {@code "Token rejected: ERR"} or
 *                  {@code "RE01 unaccepted (source-IP mismatch)"}), {@code null} otherwise; for logging only
 */
public record PingOutcome(boolean reachable, Double latencyMs, String detail) {

    public static PingOutcome unreachable() {
        return new PingOutcome(false, null, null);
    }

    public static PingOutcome unreachable(final String detail) {
        return new PingOutcome(false, null, detail);
    }

    public static PingOutcome reachable(final double latencyMs) {
        return new PingOutcome(true, latencyMs, null);
    }

    /**
     * A short, human-readable reason for a failure: the throwable's message, or — when it has none
     * (e.g. a bare {@code EOFException}) — its simple class name. Avoids the fully-qualified exception
     * class name ({@code java.io.IOException: ...}) leaking into the log line.
     */
    public static String reason(final Throwable t) {
        final String m = t.getMessage();
        return (m != null && !m.isBlank()) ? m : t.getClass().getSimpleName();
    }
}
