package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.PingMapper;
import at.rtr.rmbt.request.PingRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class PingMapperImplTest {
    private PingMapper pingMapper;

    @Mock
    private PingRequest pingRequest;
    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        pingMapper = new PingMapperImpl();
    }

    @Test
    public void pingRequestToPing_whenCommonData_expectPing() {
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(pingRequest.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(pingRequest.getValue()).thenReturn(TestConstants.DEFAULT_PING_VALUE);
        when(pingRequest.getValueServer()).thenReturn(TestConstants.DEFAULT_PING_VALUE_SERVER);

        var response = pingMapper.pingRequestToPing(pingRequest, test);

        assertEquals(test, response.getTest());
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getOpenTestUUID());
        assertEquals(TestConstants.DEFAULT_TIME_NS, response.getTimeNs());
        assertEquals(TestConstants.DEFAULT_PING_VALUE, response.getValue());
        assertEquals(TestConstants.DEFAULT_PING_VALUE_SERVER, response.getValueServer());
    }
}