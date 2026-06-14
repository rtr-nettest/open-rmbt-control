package at.rtr.rmbt.service.quality;

/**
 * Performs a single RMBT-protocol PING against a measurement server and reports reachability/latency.
 * Implementations must never throw — any failure (connect, TLS, handshake, auth, timeout) is reported
 * as {@link PingOutcome#unreachable()}.
 */
public interface RmbtPinger {

    /**
     * @param host  the server hostname/address to connect to (use the IPv4- or IPv6-specific address
     *              to force the protocol family)
     * @param port  the TLS port to connect to
     * @param token the RMBT auth token (see {@code RmbtTokenFactory})
     * @return the ping outcome (never {@code null})
     */
    PingOutcome ping(String host, int port, String token);
}
