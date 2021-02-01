package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.service.impl.UUIDGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestServerMapperImplTest {
    private TestServerMapper testServerMapper;

    @MockBean
    private UUIDGenerator uuidGenerator;

    @Mock
    private TestServer testServer;
    @Mock
    private TestServerRequest testServerRequest;

    @Before
    public void setUp() {
        testServerMapper = new TestServerMapperImpl(uuidGenerator);
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
        when(testServer.getLatitude()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_LATITUDE);
        when(testServer.getLongitude()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_LONGITUDE);
        when(testServer.getLocation()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_LOCATION);
        when(testServer.getWebAddressIpV4()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4);
        when(testServer.getWebAddressIpV6()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6);
        when(testServer.getServerType()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE);
        when(testServer.getPriority()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PRIORITY);
        when(testServer.getWeight()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEIGHT);
        when(testServer.getActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testServer.getKey()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_KEY);
        when(testServer.getSelectable()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testServer.getNode()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NODE);

        var actualResponse = testServerMapper.testServerToTestServerResponse(testServer);

        assertEquals(TestConstants.DEFAULT_UID, actualResponse.getUid());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NAME, actualResponse.getName());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS, actualResponse.getWebAddress());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT, actualResponse.getPort());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL, actualResponse.getPortSsl());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_CITY, actualResponse.getCity());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_COUNTRY, actualResponse.getCountry());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_LATITUDE, actualResponse.getLatitude());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_LONGITUDE, actualResponse.getLongitude());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_LOCATION, actualResponse.getLocation());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4, actualResponse.getWebAddressIpV4());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6, actualResponse.getWebAddressIpV6());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE, actualResponse.getServerType());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PRIORITY, actualResponse.getPriority());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEIGHT, actualResponse.getWeight());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualResponse.getActive());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_KEY, actualResponse.getKey());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualResponse.getSelectable());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NODE, actualResponse.getNode());
    }

    @Test
    public void testServerRequestToTestServer_whenCommonData_expectTestServer() {
        when(testServerRequest.getName()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NAME);
        when(testServerRequest.getWebAddress()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS);
        when(testServerRequest.getPort()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT);
        when(testServerRequest.getPortSsl()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL);
        when(testServerRequest.getCity()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_CITY);
        when(testServerRequest.getCountry()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_COUNTRY);
        when(testServerRequest.getLatitude()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_LATITUDE);
        when(testServerRequest.getLongitude()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_LONGITUDE);
        when(testServerRequest.getLocation()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_LOCATION);
        when(testServerRequest.getWebAddressIpV4()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4);
        when(testServerRequest.getWebAddressIpV6()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6);
        when(testServerRequest.getServerType()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE);
        when(testServerRequest.getPriority()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PRIORITY);
        when(testServerRequest.getWeight()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_WEIGHT);
        when(testServerRequest.getActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testServerRequest.getKey()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_KEY);
        when(testServerRequest.getSelectable()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testServerRequest.getNode()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NODE);

        var actualTestServer = testServerMapper.testServerRequestToTestServer(testServerRequest);

        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NAME, actualTestServer.getName());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS, actualTestServer.getWebAddress());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT, actualTestServer.getPort());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL, actualTestServer.getPortSsl());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_CITY, actualTestServer.getCity());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_COUNTRY, actualTestServer.getCountry());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_LATITUDE, actualTestServer.getLatitude());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_LONGITUDE, actualTestServer.getLongitude());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_LOCATION, actualTestServer.getLocation());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4, actualTestServer.getWebAddressIpV4());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6, actualTestServer.getWebAddressIpV6());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE, actualTestServer.getServerType());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PRIORITY, actualTestServer.getPriority());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_WEIGHT, actualTestServer.getWeight());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualTestServer.getActive());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_KEY, actualTestServer.getKey());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, actualTestServer.getSelectable());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NODE, actualTestServer.getNode());
    }
}