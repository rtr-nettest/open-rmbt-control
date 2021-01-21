package com.rtr.nettest.service;

import com.rtr.nettest.response.TestServerResponse;

import java.util.List;

public interface TestServerService {

    List<TestServerResponse> getServers();

    List<TestServerResponse> getServersHttp();

    List<TestServerResponse> getServersWs();

    List<TestServerResponse> getServersQos();
}
