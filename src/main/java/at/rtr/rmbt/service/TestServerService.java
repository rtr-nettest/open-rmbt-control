package at.rtr.rmbt.service;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.model.enums.ServerType;
import at.rtr.rmbt.response.TestServerResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestServerService {
    Optional<TestServer> findByUuidAndActive(UUID preferServer, boolean active);

    TestServer findActiveByServerTypeInAndCountry(List<ServerType> serverTypes, String countries);

    List<TestServerResponse> getServers();

    List<TestServerResponse> getServersHttp();

    List<TestServerResponse> getServersWs();

    List<TestServerResponse> getServersQos();
}
