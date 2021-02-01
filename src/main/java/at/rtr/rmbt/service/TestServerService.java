package at.rtr.rmbt.service;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.enums.ServerType;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestServerService {
    Optional<TestServer> findByUuidAndActive(UUID preferServer, boolean active);

    TestServer findActiveByServerTypeInAndCountry(List<ServerType> serverTypes, String countries);

    List<TestServerResponseForSettings> getServers();

    List<TestServerResponseForSettings> getServersHttp();

    List<TestServerResponseForSettings> getServersWs();

    List<TestServerResponseForSettings> getServersQos();

    void createTestServer(TestServerRequest testServerRequest);

    List<TestServerResponse> getAllTestServer();

    void updateTestServer(Long id, TestServerRequest testServerRequest);

    void deleteTestServer(Long id);
}
