package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.enums.ServerType;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.service.TestServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static at.rtr.rmbt.constant.Config.*;

@Service
@RequiredArgsConstructor
public class TestServerServiceImpl implements TestServerService {

    private final TestServerRepository testServerRepository;
    private final TestServerMapper testServerMapper;

    @Override
    public Optional<TestServer> findByUuidAndActive(UUID preferServer, boolean active) {
        return testServerRepository.findByUuidAndActive(preferServer, active);
    }

    @Override
    public TestServer findActiveByServerTypeInAndCountry(List<ServerType> serverTypes, String country) {
        return testServerRepository.findActiveByServerTypeInAndCountries(serverTypes, country);
    }

    @Override
    public List<TestServerResponse> getServers() {
        return getServers(SERVER_TEST_SERVER_TYPES);
    }

    @Override
    public List<TestServerResponse> getServersHttp() {
        return getServers(SERVER_HTTP_TEST_SERVER_TYPES);
    }

    @Override
    public List<TestServerResponse> getServersWs() {
        return getServers(SERVER_WS_TEST_SERVER_TYPES);
    }

    @Override
    public List<TestServerResponse> getServersQos() {
        return getServers(SERVER_QOS_TEST_SERVER_TYPES);
    }

    private List<TestServerResponse> getServers(List<String> serverTypes) {
        return testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(serverTypes).stream()
            .map(testServerMapper::testServerToTestServerResponse)
            .collect(Collectors.toList());
    }
}
