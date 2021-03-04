package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.TestMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestMapperImplTest {
    private TestMapper testMapper;

    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        testMapper = new TestMapperImpl();
    }

    @Test
    public void testToTestResponse_whenCommonData_expectTestResponse() {
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);
        when(test.getUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);

        var response = testMapper.testToTestResponse(test);

        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
        assertEquals(TestConstants.DEFAULT_ZONED_DATE_TIME, response.getTime());
    }
}
