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
    public TestServer findActiveByServerTypeInAndCountry(List<ServerType> serverTypes, String country) {
        List<String> labels = serverTypes.stream()
            .map(ServerType::getLabel)
            .collect(Collectors.toList());
        return testServerRepository.findActiveByServerTypeInAndCountries(labels, country);
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
    public void createTestServer(TestServerRequest testServerRequest) {
        Optional.of(testServerRequest)
                .map(testServerMapper::testServerRequestToTestServer)
                .ifPresent(testServerRepository::save);
    }

    @Override
    public List<TestServerResponse> getAllTestServer() {
        List<TestServer> testServers = testServerRepository.findAll();
        Set<Long> testServersId = testServers.stream()
                .map(TestServer::getUid)
                .collect(Collectors.toSet());

        Map<Long, Timestamp> lastTest = testRepository.findLastTestByServerIdIn(testServersId).stream()
                .collect(Collectors.toMap(test -> test.getTestServer().getUid(), test -> Timestamp.valueOf(test.getTime().toLocalDateTime())));

        Map<Long, Timestamp> lastSuccessfulTest = testRepository.findLastSuccessTestByServerIdInAndStatusIn(testServersId, List.of(TestStatus.FINISHED.toString())).stream()
                .collect(Collectors.toMap(test -> test.getTestServer().getUid(), test -> Timestamp.valueOf(test.getTime().toLocalDateTime())));

        return testServers.stream()
                .map(x -> {
                    var lastTestTimestamp = lastTest.get(x.getUid());
                    var lastSuccessfulTestTimestamp = lastSuccessfulTest.get(x.getUid());
                    var lastMeasurementSuccess = Objects.nonNull(lastSuccessfulTestTimestamp);
                    return testServerMapper.testServerToTestServerResponse(x, lastTestTimestamp, lastSuccessfulTestTimestamp, lastMeasurementSuccess);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateTestServer(Long id, TestServerRequest testServerRequest) {
        var formerTestServer = getTestServerById(id);
        var updatedTestServer = testServerMapper.testServerRequestToTestServer(testServerRequest);
        updatedTestServer.setUid(id);
        updatedTestServer.setUuid(formerTestServer.getUuid());
        testServerRepository.save(updatedTestServer);
    }

    @Override
    public void deleteTestServer(Long id) {
        var testServer = getTestServerById(id);
        testServer.setArchived(true);
        testServer.setActive(false);
        testServerRepository.save(testServer);
    }

    private TestServer getTestServerById(Long id) {
        return testServerRepository.findById(id)
                .orElseThrow(TestServerNotFoundException::new);
    }

    private List<TestServerResponseForSettings> getServers(List<ServerType> serverTypes) {
        return testServerRepository.findDistinctByActiveTrueAndSelectableTrueAndServerTypesIn(serverTypes).stream()
                .map(testServerMapper::testServerToTestServerResponseForSettings)
                .collect(Collectors.toList());
    }
}
