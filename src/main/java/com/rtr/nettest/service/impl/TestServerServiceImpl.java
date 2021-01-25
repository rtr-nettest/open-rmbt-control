package com.rtr.nettest.service.impl;

import com.rtr.nettest.mapper.TestServerMapper;
import com.rtr.nettest.model.TestServer;
import com.rtr.nettest.model.enums.ServerType;
import com.rtr.nettest.repository.TestServerRepository;
import com.rtr.nettest.response.TestServerResponse;
import com.rtr.nettest.service.TestServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rtr.nettest.constant.Config.*;

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
