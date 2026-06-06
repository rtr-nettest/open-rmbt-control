package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.TestServerQuality;
import at.rtr.rmbt.repository.TestServerQualityRepository;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.service.quality.PingOutcome;
import at.rtr.rmbt.service.quality.RmbtPinger;
import at.rtr.rmbt.utils.RmbtTokenFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Scheduled test-server quality check.
 *
 * <p>Every 5 minutes (configurable via {@code test-server-quality.cron}) it walks all <b>active</b>
 * test servers of type {@link ServerType#RMBThttp} and, for each, performs an RMBT PING over IPv4
 * ({@code web_address_ipv4}) and IPv6 ({@code web_address_ipv6}) — one after another, no parallelism.
 * It authenticates with a freshly minted HMAC token (signed with the server's {@code key}) and stores
 * the protocol, reachability and client-measured latency in {@code test_server_quality}.
 *
 * <p>The actual network/protocol work is delegated to {@link RmbtPinger} (so this orchestration is
 * unit-testable and a ping failure never breaks the loop or the scheduler thread).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestServerQualityService {

    private final TestServerRepository testServerRepository;
    private final TestServerQualityRepository testServerQualityRepository;
    private final RmbtPinger pinger;

    @Scheduled(
            cron = "${test-server-quality.cron:0 */5 * * * *}",
            zone = "${test-server-quality.zone:}")
    public void measureAll() {
        final List<TestServer> servers = testServerRepository.findByServerTypeAndActiveTrue(ServerType.RMBThttp);
        if (servers == null || servers.isEmpty()) {
            log.info("Test-server quality: no active RMBThttp servers to check");
            return;
        }
        log.info("Test-server quality: checking {} active RMBThttp server(s)", servers.size());
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
        if (server.getPortSsl() == null) {
            log.warn("Test-server quality: '{}' has no SSL port, skipping IPv{}", server.getName(), protocol);
            return;
        }
        if (StringUtils.isBlank(server.getKey())) {
            log.warn("Test-server quality: '{}' has no key, cannot build token, skipping IPv{}", server.getName(), protocol);
            return;
        }

        final String token = RmbtTokenFactory.createToken(server.getKey(), UUID.randomUUID(), Instant.now().getEpochSecond());

        PingOutcome outcome;
        try {
            outcome = pinger.ping(host, server.getPortSsl(), token);
        } catch (Exception e) {
            // RmbtPinger should never throw, but be defensive so one bad server can't break the loop.
            log.warn("Test-server quality: '{}' IPv{} ping raised {}", server.getName(), protocol, e.toString());
            outcome = PingOutcome.unreachable();
        }

        final TestServerQuality quality = TestServerQuality.builder()
                .serverUuid(server.getUuid())
                .timestamp(Instant.now())
                .protocol(protocol)
                .reachable(outcome.reachable())
                .latencyMs(outcome.latencyMs())
                .build();
        testServerQualityRepository.save(quality);

        if (outcome.reachable()) {
            log.info("Test-server quality: '{}' [{}] IPv{} reachable, latency={}ms",
                    server.getName(), host, protocol, outcome.latencyMs());
        } else {
            log.info("Test-server quality: '{}' [{}] IPv{} UNREACHABLE", server.getName(), host, protocol);
        }
    }
}
