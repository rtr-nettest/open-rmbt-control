package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.mapper.ServerTypeDetailsMapper;
import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.ServerTypeDetails;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.request.ServerTypeDetailsRequest;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.ServerTypeDetailsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestServerMapperImplTest {
    private TestServerMapper testServerMapper;

    @MockBean
    private UUIDGenerator uuidGenerator;
    @MockBean
    private ServerTypeDetailsMapper serverTypeDetailsMapper;

    @Mock
    private TestServer testServer;
    @Mock
    private TestServerRequest testServerRequest;
    @Mock
    private ServerTypeDetails serverTypeDetails;
    @Mock
    private ServerTypeDetailsRequest serverTypeDetailsRequest;
    @Mock
    private ServerTypeDetailsResponse serverTypeDetailsResponse;

    @Before
    public void setUp() {
        testServerMapper = new TestServerMapperImpl(uuidGenerator, serverTypeDetailsMapper);
    }

    @Test
    public void testServerToTestServerResponse_whenCommonData_expectTestServerResponseForSettings() {
        when(testServer.getUuid()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_UUID);
        when(testServer.getName()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NAME);

        var actualResponse = testServerMapper.testServerToTestServerResponseForSettings(testServer);

        assertEquals(TestConstants.DEFAULT_TEST_SERVER_UUID.toString(), actualResponse.getUuid());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NAME, actualResponse.getName());
    }

    @Test
    public void testServerToTestServerResponse_whenCommonData_expectTestServerResponse() {
        when(testServer.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(testServer.getName()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NAME);
        when(testServer.getWebAddress()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS);
        when(testServer.getPort()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT);
        when(testServer.getPortSsl()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL);
        when(testServer.getCity()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_CITY);
        when(testServer.getCountry()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_COUNTRY);
        when(testServer.getLatitude()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(testServer.getLongitude()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(testServer.getLocation()).thenReturn(TestConstants.DEFAULT_LOCATION);
        when(testServer.getWebAddressIpV4()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4);
        when(testServer.getWebAddressIpV6()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6);
        when(testServer.getServerTypeDetails()).thenReturn(Set.of(serverTypeDetails));
        when(testServer.getPriority()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PRIORITY);
        when(testServer.getWeight()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEIGHT);
        when(testServer.getActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testServer.getKey()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_KEY);
        when(testServer.getSelectable()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testServer.getNode()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NODE);
        when(serverTypeDetailsMapper.serverTypeDetailsToServerTypeDetailsResponse(serverTypeDetails)).thenReturn(serverTypeDetailsResponse);

        var actualResponse = testServerMapper.testServerToTestServerResponse(testServer, TestConstants.DEFAULT_LAST_TEST_TIMESTAMP,
                TestConstants.DEFAULT_LAST_SUCCESSFUL_TEST_TIMESTAMP, TestConstants.DEFAULT_FLAG_TRUE);

        assertEquals(TestConstants.DEFAULT_UID, actualResponse.getId());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NAME, actualResponse.getName());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS, actualResponse.getWebAddress());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT, actualResponse.getPort());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL, actualResponse.getPortSsl());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_CITY, actualResponse.getCity());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_COUNTRY, actualResponse.getCountry());
        assertEquals(TestConstants.DEFAULT_LATITUDE, actualResponse.getLatitude());
        assertEquals(TestConstants.DEFAULT_LONGITUDE, actualResponse.getLongitude());
        assertEquals(TestConstants.DEFAULT_LOCATION, actualResponse.getLocation());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4, actualResponse.getWebAddressIpV4());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6, actualResponse.getWebAddressIpV6());
        assertEquals(Set.of(serverTypeDetailsResponse), actualResponse.getServerTypeDetails());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PRIORITY, actualResponse.getPriority());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEIGHT, actualResponse.getWeight());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualResponse.getActive());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_KEY, actualResponse.getSecretKey());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualResponse.getSelectable());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NODE, actualResponse.getNode());
        assertEquals(TestConstants.DEFAULT_LAST_TEST_TIMESTAMP, actualResponse.getTimeOfLastMeasurement());
        assertEquals(TestConstants.DEFAULT_LAST_SUCCESSFUL_TEST_TIMESTAMP, actualResponse.getLastSuccessfulMeasurement());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualResponse.isLastMeasurementSuccess());
        assertFalse(actualResponse.isEncrypted());
    }

    @Test
    public void testServerRequestToTestServer_whenCommonData_expectTestServer() {
        when(testServerRequest.getName()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NAME);
        when(testServerRequest.getWebAddress()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS);
        when(testServerRequest.getPort()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT);
        when(testServerRequest.getPortSsl()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL);
        when(testServerRequest.getCity()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_CITY);
        when(testServerRequest.getCountry()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_COUNTRY);
        when(testServerRequest.getLatitude()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(testServerRequest.getLongitude()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(testServerRequest.getLocation()).thenReturn(TestConstants.DEFAULT_LOCATION);
        when(testServerRequest.getWebAddressIpV4()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4);
        when(testServerRequest.getWebAddressIpV6()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6);
        when(testServerRequest.getServerTypeDetails()).thenReturn(Set.of(serverTypeDetailsRequest));
        when(testServerRequest.getPriority()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PRIORITY);
        when(testServerRequest.getWeight()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEIGHT);
        when(testServerRequest.isActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testServerRequest.getSecretKey()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_KEY);
        when(testServerRequest.isSelectable()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testServerRequest.getNode()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NODE);
        when(testServerRequest.isEncrypted()).thenReturn(true);
        when(serverTypeDetailsMapper.serverTypeDetailRequestToServerTypeDetails(serverTypeDetailsRequest)).thenReturn(serverTypeDetails);

        var actualTestServer = testServerMapper.testServerRequestToTestServer(testServerRequest);

        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NAME, actualTestServer.getName());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS, actualTestServer.getWebAddress());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT, actualTestServer.getPort());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL, actualTestServer.getPortSsl());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_CITY, actualTestServer.getCity());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_COUNTRY, actualTestServer.getCountry());
        assertEquals(TestConstants.DEFAULT_LATITUDE, actualTestServer.getLatitude());
        assertEquals(TestConstants.DEFAULT_LONGITUDE, actualTestServer.getLongitude());
        assertEquals(TestConstants.DEFAULT_LOCATION, actualTestServer.getLocation());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4, actualTestServer.getWebAddressIpV4());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6, actualTestServer.getWebAddressIpV6());
        assertEquals(Set.of(serverTypeDetails), actualTestServer.getServerTypeDetails());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PRIORITY, actualTestServer.getPriority());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEIGHT, actualTestServer.getWeight());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualTestServer.getActive());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_KEY, actualTestServer.getKey());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualTestServer.getSelectable());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NODE, actualTestServer.getNode());
    }
}
