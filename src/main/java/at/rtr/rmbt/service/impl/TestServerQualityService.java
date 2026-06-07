package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.TestServerQuality;
import at.rtr.rmbt.repository.TestServerQualityRepository;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.service.quality.PingOutcome;
import at.rtr.rmbt.service.quality.RmbtPinger;
import at.rtr.rmbt.service.quality.QosTlsPinger;
import at.rtr.rmbt.service.quality.RmbtUdpPinger;
import at.rtr.rmbt.utils.RmbtTokenFactory;
import at.rtr.rmbt.utils.RmbtUdpTokenFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
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
 *       the server's {@code key} ({@link RmbtPinger}).</li>
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

    /**
     * Optional public IPs of <b>this</b> control server, as seen by the UDP test servers. When set, the
     * RMBTudp token's IP HMAC is built with the correct source IP and only an {@code RR01} (source-IP
     * confirmed) reply is accepted. When blank, a placeholder IP is used and {@code RE01} is accepted too.
     */
    @Value("${test-server-quality.public-ipv4:}")
    private String publicIpv4;

    @Value("${test-server-quality.public-ipv6:}")
    private String publicIpv6;

    @Scheduled(
            cron = "${test-server-quality.cron:0 */5 * * * *}",
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
            save(server, protocol, PingOutcome.unreachable());
            return;
        }

        save(server, protocol, outcome);
        if (outcome.reachable()) {
            log.info("Test-server quality: '{}' [{}] IPv{} reachable, latency={}ms",
                    server.getName(), host, protocol, outcome.latencyMs());
        } else {
            log.info("Test-server quality: '{}' [{}] IPv{} UNREACHABLE", server.getName(), host, protocol);
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
            final String token = RmbtTokenFactory.createToken(
                    server.getKey(), UUID.randomUUID(), Instant.now().getEpochSecond());
            return webSocketPinger.ping(host, server.getPortSsl(), token);
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
            if (server.getPortSsl() == null) {
                log.warn("Test-server quality: '{}' has no SSL port, skipping IPv{}", server.getName(), protocol);
                return null;
            }
            if (StringUtils.isBlank(server.getKey())) {
                log.warn("Test-server quality: '{}' has no key, cannot build token, skipping IPv{}", server.getName(), protocol);
                return null;
            }
            final int port = server.getPortSsl();
            // If our public IP for this family is configured, sign the token with it and demand an
            // RR01 (source-IP confirmed) reply; otherwise use a placeholder and accept RE01 too.
            final InetAddress publicIp = resolvePublicIp(protocol == 4 ? publicIpv4 : publicIpv6);
            final boolean requireIpMatch = publicIp != null;
            final InetAddress sourceIp = requireIpMatch ? publicIp : PLACEHOLDER_SOURCE_IP;
            final byte[] request = RmbtUdpTokenFactory.createRequestPacket(
                    server.getKey(), sourceIp, 1, Instant.now().getEpochSecond());
            return udpPinger.ping(host, port, request, requireIpMatch);
        }
        log.debug("Test-server quality: '{}' has unsupported server_type {}, skipping", server.getName(), type);
        return null;
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
