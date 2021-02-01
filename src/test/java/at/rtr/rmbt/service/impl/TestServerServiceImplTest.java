package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.repository.TestServerRepository;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;
import at.rtr.rmbt.service.TestServerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static at.rtr.rmbt.constant.Config.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
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
    private TestServerResponseForSettings testServerResponseForSettings;
    @Mock
    private TestServerRequest testServerRequest;
    @Mock
    private TestServerResponse testServerResponse;
    @Mock
    private TestServer updatedTestServer;

    @Before
    public void setUp() {
        testServerService = new TestServerServiceImpl(testServerRepository, testServerMapper);
    }

    @Test
    public void getServers_whenCommonData_expectTestServerList() {
        when(testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(SERVER_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponseForSettings(testServer)).thenReturn(testServerResponseForSettings);

        var response = testServerService.getServers();

        assertEquals(List.of(testServerResponseForSettings), response);
    }

    @Test
    public void getServersHttp_whenCommonData_expectTestServerList() {
        when(testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(SERVER_HTTP_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponseForSettings(testServer)).thenReturn(testServerResponseForSettings);

        var response = testServerService.getServersHttp();

        assertEquals(List.of(testServerResponseForSettings), response);
    }

    @Test
    public void getServersWs_whenCommonData_expectTestServerList() {
        when(testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(SERVER_WS_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponseForSettings(testServer)).thenReturn(testServerResponseForSettings);

        var response = testServerService.getServersWs();

        assertEquals(List.of(testServerResponseForSettings), response);
    }

    @Test
    public void getServersQos_whenCommonData_expectTestServerList() {
        when(testServerRepository.getByActiveTrueAndSelectableTrueAndServerTypeIn(SERVER_QOS_TEST_SERVER_TYPES)).thenReturn(List.of(testServer));
        when(testServerMapper.testServerToTestServerResponseForSettings(testServer)).thenReturn(testServerResponseForSettings);

        var response = testServerService.getServersQos();

        assertEquals(List.of(testServerResponseForSettings), response);
    }

    @Test
    public void createTestServer_whenCommonData_expectSaved() {
        when(testServerMapper.testServerRequestToTestServer(testServerRequest)).thenReturn(testServer);

        testServerService.createTestServer(testServerRequest);

        verify(testServerRepository).save(testServer);
    }

    @Test
    public void getAllTestServer_whenExistOneTestServer_expectTestServerResponseList() {
        var testServerResponseList = List.of(testServerResponse);
        var testServerList = List.of(testServer);
        when(testServerRepository.findAll()).thenReturn(testServerList);
        when(testServerMapper.testServerToTestServerResponse(testServer)).thenReturn(testServerResponse);

        var responseList = testServerService.getAllTestServer();

        assertEquals(testServerResponseList, responseList);
    }

    @Test
    public void updateTestServer_whenExistOneTestServer_expectUpdated() {
        when(testServerRepository.findById(TestConstants.DEFAULT_UID)).thenReturn(Optional.of(testServer));
        when(testServer.getUuid()).thenReturn(TestConstants.DEFAULT_UUID);
        when(testServer.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(testServerMapper.testServerRequestToTestServer(testServerRequest)).thenReturn(updatedTestServer);

        testServerService.updateTestServer(TestConstants.DEFAULT_UID, testServerRequest);

        verify(updatedTestServer).setUuid(TestConstants.DEFAULT_UUID);
        verify(testServerRepository).save(updatedTestServer);
    }

    @Test
    public void deleteTestServer_whenExistTestServer_expectDeleted() {
        when(testServerRepository.findById(TestConstants.DEFAULT_UID)).thenReturn(Optional.of(testServer));

        testServerService.deleteTestServer(TestConstants.DEFAULT_UID);

        verify(testServerRepository).delete(testServer);
    }
}