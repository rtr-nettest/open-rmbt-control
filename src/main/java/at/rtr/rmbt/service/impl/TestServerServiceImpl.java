package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.exception.TestServerNotFoundException;
import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;
import at.rtr.rmbt.service.TestServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static at.rtr.rmbt.constant.Config.*;

@Service
@RequiredArgsConstructor
public class TestServerServiceImpl implements TestServerService {

    private final TestServerRepository testServerRepository;
    private final TestServerMapper testServerMapper;
    private final TestRepository testRepository;

    @Override
    public Optional<TestServer> findByUuidAndActive(UUID preferServer, boolean active) {
        return testServerRepository.findByUuidAndActive(preferServer, active);
    }

    @Override
    public TestServer findActiveByServerTypeInAndCountry(List<ServerType> serverTypes, String country, Boolean coverage) {
        List<String> labels = serverTypes.stream()
            .map(ServerType::getLabel)
            .collect(Collectors.toList());
        return testServerRepository.findActiveByServerTypeInAndCountries(labels, country, coverage);
    }

    @Override
    public List<TestServerResponseForSettings> getServers() {
        return getServers(SERVER_TEST_SERVER_TYPES);
    }

    @Override
    public List<TestServerResponseForSettings> getServersHttp() {
        return getServers(SERVER_HTTP_TEST_SERVER_TYPES);
    }

    @Override
    public List<TestServerResponseForSettings> getServersWs() {
        return getServers(SERVER_WS_TEST_SERVER_TYPES);
    }

    @Override
    public List<TestServerResponseForSettings> getServersQos() {
        return getServers(SERVER_QOS_TEST_SERVER_TYPES);
    }

    @Override
    public List<TestServerResponseForSettings> getServersUdp() {
        return getServers(SERVER_UDP_TEST_SERVER_TYPES);
    }

    @Override
    public String getHello() {
        return "nothing";

    }







    private List<TestServerResponseForSettings> getServers(List<ServerType> serverTypes) {
        return testServerRepository.findDistinctByActiveTrueAndSelectableTrueAndServerTypesIn(serverTypes).stream()
                .map(testServerMapper::testServerToTestServerResponseForSettings)
                .collect(Collectors.toList());
    }
}
