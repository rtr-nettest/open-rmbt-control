package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.ServerTypeDetailsMapper;
import at.rtr.rmbt.model.ServerTypeDetails;
import at.rtr.rmbt.request.ServerTypeDetailsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ServerTypeDetailsMapperImplTest {
    private ServerTypeDetailsMapper serverTypeDetailsMapper;

    @Mock
    private ServerTypeDetailsRequest serverTypeDetailsRequest;
    @Mock
    private ServerTypeDetails serverTypeDetails;

    @Before
    public void setUp() {
        serverTypeDetailsMapper = new ServerTypeDetailsMapperImpl();
    }

    @Test
    public void serverTypeDetailRequestToServerTypeDetails_whenCommonData_expectServerTypeDetails() {
        when(serverTypeDetailsRequest.getPort()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT);
        when(serverTypeDetailsRequest.getPortSsl()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL);
        when(serverTypeDetailsRequest.getServerType()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE);
        when(serverTypeDetailsRequest.isEncrypted()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);

        var response = serverTypeDetailsMapper.serverTypeDetailRequestToServerTypeDetails(serverTypeDetailsRequest);

        assertEquals(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE, response.getServerType());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT, response.getPort());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL, response.getPortSsl());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, response.isEncrypted());
    }

    @Test
    public void serverTypeDetailsToServerTypeDetailsResponse_whenCommonData_expectServerTypeDetailsResponse() {
        when(serverTypeDetails.getPort()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT);
        when(serverTypeDetails.getPortSsl()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL);
        when(serverTypeDetails.getServerType()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE);
        when(serverTypeDetails.isEncrypted()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);

        var response = serverTypeDetailsMapper.serverTypeDetailsToServerTypeDetailsResponse(serverTypeDetails);

        assertEquals(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE, response.getServerType());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT, response.getPort());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_PORT_SSL, response.getPortSsl());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, response.isEncrypted());
    }
}