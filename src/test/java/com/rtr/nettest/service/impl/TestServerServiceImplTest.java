package com.rtr.nettest.service.impl;

import com.rtr.nettest.mapper.TestServerMapper;
import com.rtr.nettest.model.TestServer;
import com.rtr.nettest.repository.TestServerRepository;
import com.rtr.nettest.response.TestServerResponse;
import com.rtr.nettest.service.TestServerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.rtr.nettest.constant.Config.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestServerServiceImplTest {
    private TestServerService testServerService;

    @MockBean
    private TestServerRepository testServerRepository;
    @MockBean
    private TestServerMapper testServerMapper;

    @Mock
    private TestServer testServer;
    @Mock
    private TestServerResponse testServerResponse;

    @Before
    public void setUp() {
        testServerService = new TestServerServiceImpl(testServerRepository, testServerMapper);
    }

    @Test
    public void getServers_whenCommonData_expectTestServerList() {
        when(testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(SERVER_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponse(testServer)).thenReturn(testServerResponse);

        var response = testServerService.getServers();

        assertEquals(List.of(testServerResponse), response);
    }

    @Test
    public void getServersHttp_whenCommonData_expectTestServerList() {
        when(testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(SERVER_HTTP_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponse(testServer)).thenReturn(testServerResponse);

        var response = testServerService.getServersHttp();

        assertEquals(List.of(testServerResponse), response);
    }

    @Test
    public void getServersWs_whenCommonData_expectTestServerList() {
        when(testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(SERVER_WS_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponse(testServer)).thenReturn(testServerResponse);

        var response = testServerService.getServersWs();

        assertEquals(List.of(testServerResponse), response);
    }

    @Test
    public void getServersQos_whenCommonData_expectTestServerList() {
        when(testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(SERVER_QOS_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponse(testServer)).thenReturn(testServerResponse);

        var response = testServerService.getServersQos();

        assertEquals(List.of(testServerResponse), response);
    }
}