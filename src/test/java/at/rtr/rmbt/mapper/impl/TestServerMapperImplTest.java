package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.TestServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestServerMapperImplTest {
    private TestServerMapper testServerMapper;

    @Mock
    private TestServer testServer;

    @Before
    public void setUp() {
        testServerMapper = new TestServerMapperImpl();
    }

    @Test
    public void testServerToTestServerResponse_whenCommonData_expectTestServerResponse() {
        when(testServer.getUuid()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_UUID);
        when(testServer.getName()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_NAME);

        var actualResponse = testServerMapper.testServerToTestServerResponse(testServer);

        assertEquals(TestConstants.DEFAULT_TEST_SERVER_UUID.toString(),actualResponse.getUuid());
        assertEquals(TestConstants.DEFAULT_TEST_SERVER_NAME,actualResponse.getName());
    }
}