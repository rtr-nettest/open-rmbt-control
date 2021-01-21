package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.model.TestServer;
import com.rtr.nettest.mapper.TestServerMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static com.rtr.nettest.TestConstants.DEFAULT_TEST_SERVER_NAME;
import static com.rtr.nettest.TestConstants.DEFAULT_TEST_SERVER_UUID;
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
        when(testServer.getUuid()).thenReturn(DEFAULT_TEST_SERVER_UUID);
        when(testServer.getName()).thenReturn(DEFAULT_TEST_SERVER_NAME);

        var actualResponse = testServerMapper.testServerToTestServerResponse(testServer);

        assertEquals(DEFAULT_TEST_SERVER_UUID,actualResponse.getUuid());
        assertEquals(DEFAULT_TEST_SERVER_NAME,actualResponse.getName());
    }
}