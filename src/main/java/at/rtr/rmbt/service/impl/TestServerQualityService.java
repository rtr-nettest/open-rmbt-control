package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.TestServerQuality;
import at.rtr.rmbt.repository.TestServerQualityRepository;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.service.SettingsCache;
import at.rtr.rmbt.service.quality.PingOutcome;
import at.rtr.rmbt.service.quality.RmbtPinger;
import at.rtr.rmbt.service.quality.QosTlsPinger;
import at.rtr.rmbt.service.quality.RmbtUdpPinger;
import at.rtr.rmbt.utils.RmbtTokenFactory;
import at.rtr.rmbt.utils.RmbtUdpTokenFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Scheduled test-server quality check.
 *
 * <p>Every 5 minutes (configurable via {@code test-server-quality.cron}) it walks every <b>active</b>
 * test server of type {@link ServerType#RMBThttp}, {@link ServerType#RMBTudp} or {@link ServerType#QoS}
 * and, for each, performs a PING over IPv4 ({@code web_address_ipv4}) and IPv6 ({@code web_address_ipv6})
 * — one after another, no parallelism. The protocol, reachability and client-measured latency are
 * stored in {@code test_server_quality}.
 *
 * <ul>
 * <p>All ports come from {@code port_ssl} (the legacy {@code port} column is obsolete); a server with
 * no {@code port_ssl} is skipped.
 *
 * <ul>
 *   <li><b>RMBThttp</b>: RMBT protocol over WebSocket/TLS on {@code port_ssl}, HMAC token signed with
 *       the server's {@code key} ({@link RmbtPinger}). New servers require the v2 token
 *       ({@code <v1>_#v2#<base64 udp token>}); its source-IP HMAC must match this control server's
 *       public IP, read from the {@code public_ipv4}/{@code public_ipv6} settings — when unset no v2
 *       token is sent and new servers reply {@code ERR}.</li>
 *   <li><b>RMBTudp</b>: {@code open-rmbt-udp-ping} on {@code port_ssl} ({@link RmbtUdpPinger}).
 *       The IP HMAC normally will not match our (unknown) public source IP, so the server answers
 *       {@code RE01} — which still confirms reachability.</li>
 *   <li><b>QoS</b>: a TLS handshake to {@code port_ssl} + the QoS greeting ({@link QosTlsPinger});
 *       the TCP-connect round-trip is the latency. No token needed.</li>
 * </ul>
 *
 * <p>The network/protocol work is delegated to the pingers (so this orchestration is unit-testable
 * and a ping failure never breaks the loop or the scheduler thread).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestServerQualityService {

    /**
     * Placeholder source IP for the UDP token's IP HMAC: we do not know the control server's public
     * source address, so the IP HMAC will not match and the server replies {@code RE01} — acceptable
     * for a reachability probe.
     */
    private static final InetAddress PLACEHOLDER_SOURCE_IP;

    static {
        try {
            PLACEHOLDER_SOURCE_IP = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        } catch (UnknownHostException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final TestServerRepository testServerRepository;
    private final TestServerQualityRepository testServerQualityRepository;
    private final RmbtPinger webSocketPinger;
    private final RmbtUdpPinger udpPinger;
    private final QosTlsPinger qosPinger;
    private final SettingsCache settingsCache;

    @Scheduled(
            cron = "${test-server-quality.cron:0 * * * * *}",
            zone = "${test-server-quality.zone:}")
    public void measureAll() {
        final List<TestServer> servers = testServerRepository.findByServerTypeInAndActiveTrue(
                List.of(ServerType.RMBThttp, ServerType.RMBTudp, ServerType.QoS));
        if (servers == null || servers.isEmpty()) {
            log.info("Test-server quality: no active RMBThttp/RMBTudp/QoS servers to check");
            return;
        }
        log.info("Test-server quality: checking {} active server(s)", servers.size());
        for (final TestServer server : servers) {
            log.debug("Measure server {} with port {} and SSL port {} v4: {} v6: {}",
                    server,server.getPort(), server.getPortSsl(), server.getWebAddressIpV4(), server.getWebAddressIpV6());
            measureServer(server, 4, server.getWebAddressIpV4());
            measureServer(server, 6, server.getWebAddressIpV6());
        }
    }

    /** Pings one server over one IP family and records the result. Never throws. */
    void measureServer(final TestServer server, final int protocol, final String host) {
        if (StringUtils.isBlank(host)) {
            log.debug("Test-server quality: '{}' has no IPv{} address, skipping", server.getName(), protocol);
            return;
        }

        final PingOutcome outcome;
        try {
            final PingOutcome attempted = pingByType(server, protocol, host);
            if (attempted == null) {
                return; // could not attempt (missing port / unsupported type) — already logged
            }
            outcome = attempted;
        } catch (Exception e) {
            // Pingers should never throw, but be defensive so one bad server can't break the loop.
            log.warn("Test-server quality: '{}' IPv{} ping raised {}", server.getName(), protocol, e.toString());
            save(server, protocol, PingOutcome.unreachable(PingOutcome.reason(e)));
            return;
        }

        save(server, protocol, outcome);
        if (outcome.reachable()) {
            log.info("Test-server quality: '{}' [{}]  IPv{} reachable, latency={} ms",
                    server.getName(), host, protocol, outcome.latencyMs());
        } else {
            log.info("Test-server quality: '{}' [{}] IPv{} UNREACHABLE{}", server.getName(), host, protocol,
                    outcome.detail() != null ? " (" + outcome.detail() + ")" : "");
        }
    }

    /** Dispatches to the right pinger by server type; returns {@code null} if the ping cannot be attempted. */
    private PingOutcome pingByType(final TestServer server, final int protocol, final String host) {
        final ServerType type = server.getServerType();
        if (type == ServerType.RMBThttp) {
            if (server.getPortSsl() == null) {
                log.warn("Test-server quality: '{}' has no SSL port, skipping IPv{}", server.getName(), protocol);
                return null;
            }
            if (StringUtils.isBlank(server.getKey())) {
                log.warn("Test-server quality: '{}' has no key, cannot build token, skipping IPv{}", server.getName(), protocol);
                return null;
            }
            final InetAddress publicIp = publicIpForFamily(protocol);
            final String token = buildRmbtHttpToken(server, publicIp, protocol);
            final PingOutcome outcome = webSocketPinger.ping(host, server.getPortSsl(), token);
            return withSourceIp(outcome, publicIp != null ? publicIp.getHostAddress() : "none (v1-only token)");
        }
        if (type == ServerType.QoS) {
            // QoS servers speak a TLS line protocol; a TLS handshake + greeting confirms reachability
            // and the TCP-connect round-trip is the latency. No token needed.
            if (server.getPortSsl() == null) {
                log.warn("Test-server quality: '{}' has no SSL port, skipping IPv{}", server.getName(), protocol);
                return null;
            }
            return qosPinger.ping(host, server.getPortSsl());
        }
        if (type == ServerType.RMBTudp) {
            if (server.getPort() == null) {
                log.warn("Test-server quality: '{}' has no SSL port, skipping IPv{}", server.getName(), protocol);
                return null;
            }
            if (StringUtils.isBlank(server.getKey())) {
                log.warn("Test-server quality: '{}' has no key, cannot build token, skipping IPv{}", server.getName(), protocol);
                return null;
            }
            final int port = server.getPort();
            // If our public IP for this family is set (public_ipv4/public_ipv6 settings), sign the token
            // with it and demand an RR01 (source-IP confirmed) reply; otherwise use a placeholder and
            // accept RE01 too (the server cannot verify our source IP, but still confirms reachability).
            final InetAddress publicIp = publicIpForFamily(protocol);
            final boolean requireIpMatch = publicIp != null;
            final InetAddress sourceIp = requireIpMatch ? publicIp : PLACEHOLDER_SOURCE_IP;
            final byte[] request = RmbtUdpTokenFactory.createRequestPacket(
                    server.getKey(), sourceIp, 1, Instant.now().getEpochSecond());
            final PingOutcome outcome = udpPinger.ping(host, port, request, requireIpMatch);
            return withSourceIp(outcome, sourceIp.getHostAddress());
        }
        log.debug("Test-server quality: '{}' has unsupported server_type {}, skipping", server.getName(), type);
        return null;
    }

    /**
     * Builds the token for the RMBThttp (RMBT-over-WebSocket) reachability check.
     *
     * <p>New measurement servers require the v2 token appended to the classic v1 token as
     * {@code <v1>_#v2#<base64 udp token>} and reject a v1-only token with {@code ERR}. On the TCP/TLS
     * path the server validates the v2 token's source-IP HMAC against the real peer address (there is
     * no {@code RE01}-style tolerance as for UDP), so the v2 part is only valid when this control
     * server's public IP for the probed family is known. That IP is read from the {@code public_ipv4}
     * / {@code public_ipv6} settings; when the matching setting is absent (or not a valid address) no
     * v2 token is appended and the plain v1 token is sent (still accepted by old servers, which ignore
     * the {@code #v2#} field).
     */
    private String buildRmbtHttpToken(final TestServer server, final InetAddress publicIp, final int protocol) {
        final long now = Instant.now().getEpochSecond();
        String token = RmbtTokenFactory.createToken(server.getKey(), UUID.randomUUID(), now);
        if (publicIp != null) {
            final byte[] udpToken = RmbtUdpTokenFactory.createToken(server.getKey(), publicIp, now);
            token = token + "_#v2#" + Base64.getEncoder().encodeToString(udpToken);
        } else {
            log.warn("Test-server quality: '{}' setting is unset/invalid; sending a v1-only token, which "
                    + "new measurement servers reject with ERR",
                    protocol == 4 ? Config.PUBLIC_IPV4_KEY : Config.PUBLIC_IPV6_KEY);
        }
        return token;
    }

    /**
     * Appends the source IP that was fed into the token's IP HMAC to an unreachable outcome's detail,
     * so the log shows the exact address the measurement server will have checked against (e.g. a
     * {@code RE01}/{@code ERR} means this address did not match the peer the server actually saw).
     * Reachable outcomes are returned unchanged.
     */
    private PingOutcome withSourceIp(final PingOutcome outcome, final String sourceLabel) {
        if (outcome.reachable()) {
            return outcome;
        }
        final String base = outcome.detail();
        return PingOutcome.unreachable((base == null ? "" : base + " ") + "[token src IP: " + sourceLabel + "]");
    }

    /**
     * This control server's public IP for the given family, read from the {@code public_ipv4} /
     * {@code public_ipv6} settings. Returns {@code null} when the setting is absent or not a valid
     * address, in which case callers fall back (UDP: placeholder + {@code RE01}; RMBThttp: v1-only token).
     */
    private InetAddress publicIpForFamily(final int protocol) {
        final String key = protocol == 4 ? Config.PUBLIC_IPV4_KEY : Config.PUBLIC_IPV6_KEY;
        return resolvePublicIp(settingsCache.get(key).orElse(null));
    }

    /** Parses a configured public IP, or returns {@code null} when blank/invalid (→ unverified probe). */
    private InetAddress resolvePublicIp(final String config) {
        if (StringUtils.isBlank(config)) {
            return null;
        }
        try {
            return InetAddress.getByName(config.trim());
        } catch (UnknownHostException e) {
            log.warn("Test-server quality: invalid configured public IP '{}', falling back to unverified probe", config);
            return null;
        }
    }

    private void save(final TestServer server, final int protocol, final PingOutcome outcome) {
        testServerQualityRepository.save(TestServerQuality.builder()
                .serverUuid(server.getUuid())
                .timestamp(Instant.now())
                .protocol(protocol)
                .reachable(outcome.reachable())
                .latencyMs(outcome.latencyMs())
                .build());
    }
}
