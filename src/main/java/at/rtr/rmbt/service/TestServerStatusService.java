package at.rtr.rmbt.service;

import at.rtr.rmbt.repository.TestServerQualityRepository;
import at.rtr.rmbt.response.TestServerStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Read side of the test-server quality data: exposes the latest reachability/latency status plus 24h
 * aggregates per server and IP protocol (the {@code /testServerStatus} endpoint).
 */
@Service
@RequiredArgsConstructor
public class TestServerStatusService {

    private final TestServerQualityRepository testServerQualityRepository;

    /**
     * @param testServer optional {@code test_server.uuid} to filter to a single server; {@code null} = all
     * @param protocol   optional IP protocol (4 / 6) to filter to; {@code null} = both. Combined with
     *                   {@code testServer} this narrows the result to a single row.
     */
    public List<TestServerStatusResponse> getStatus(final UUID testServer, final Integer protocol) {
        return testServerQualityRepository.findStatus(testServer != null ? testServer.toString() : null, protocol)
                .stream()
                .map(TestServerStatusResponse::fromRow)
                .toList();
    }
}
