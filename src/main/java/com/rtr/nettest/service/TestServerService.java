package com.rtr.nettest.service;

import com.rtr.nettest.model.TestServer;
import com.rtr.nettest.model.enums.ServerType;
import com.rtr.nettest.response.TestServerResponse;

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
