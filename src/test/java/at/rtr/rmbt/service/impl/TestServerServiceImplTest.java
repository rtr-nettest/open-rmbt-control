package at.rtr.rmbt.service.impl;


import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;
import at.rtr.rmbt.service.TestServerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


import static at.rtr.rmbt.constant.Config.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestServerServiceImplTest {
    private TestServerService testServerService;

    @MockitoBean
    private TestServerRepository testServerRepository;
    @MockitoBean
    private TestServerMapper testServerMapper;
    @MockitoBean
    private TestRepository testRepository;

    @Mock
    private TestServer testServer;
    @Mock
    private TestServerResponseForSettings testServerResponseForSettings;

    @Before
    public void setUp() {
        testServerService = new TestServerServiceImpl(testServerRepository, testServerMapper, testRepository);
    }

    @Test
    public void getServers_whenCommonData_expectTestServerList() {
        when(testServerRepository.findDistinctByActiveTrueAndSelectableTrueAndServerTypesIn(SERVER_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponseForSettings(testServer)).thenReturn(testServerResponseForSettings);

        var response = testServerService.getServers();

        assertEquals(List.of(testServerResponseForSettings), response);
    }

    @Test
    public void getServersHttp_whenCommonData_expectTestServerList() {
        when(testServerRepository.findDistinctByActiveTrueAndSelectableTrueAndServerTypesIn(SERVER_HTTP_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponseForSettings(testServer)).thenReturn(testServerResponseForSettings);

        var response = testServerService.getServersHttp();

        assertEquals(List.of(testServerResponseForSettings), response);
    }

    @Test
    public void getServersWs_whenCommonData_expectTestServerList() {
        when(testServerRepository.findDistinctByActiveTrueAndSelectableTrueAndServerTypesIn(SERVER_WS_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponseForSettings(testServer)).thenReturn(testServerResponseForSettings);

        var response = testServerService.getServersWs();

        assertEquals(List.of(testServerResponseForSettings), response);
    }

    @Test
    public void getServersQos_whenCommonData_expectTestServerList() {
        when(testServerRepository.findDistinctByActiveTrueAndSelectableTrueAndServerTypesIn(SERVER_QOS_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponseForSettings(testServer)).thenReturn(testServerResponseForSettings);

        var response = testServerService.getServersQos();

        assertEquals(List.of(testServerResponseForSettings), response);
    }
}







