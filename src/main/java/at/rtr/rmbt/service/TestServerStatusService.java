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
     */
    public List<TestServerStatusResponse> getStatus(final UUID testServer) {
        return testServerQualityRepository.findStatus(testServer != null ? testServer.toString() : null)
                .stream()
                .map(TestServerStatusResponse::fromRow)
                .toList();
    }
}
