package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.CellLocationMapper;
import at.rtr.rmbt.request.CellLocationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CellLocationMapperImplTest {
    private CellLocationMapper cellLocationMapper;

    @Mock
    private CellLocationRequest cellLocationRequest;
    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        cellLocationMapper = new CellLocationMapperImpl();
    }

    @Test
    public void cellLocationRequestToCellLocation_whenCommonData_expectCellLocation() {
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(cellLocationRequest.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE_FIRST);
        when(cellLocationRequest.getLocationId()).thenReturn(TestConstants.DEFAULT_LOCATION_ID);
        when(cellLocationRequest.getPrimaryScramblingCode()).thenReturn(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE);
        when(cellLocationRequest.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(cellLocationRequest.getTime()).thenReturn(TestConstants.DEFAULT_TIME_INSTANT);

        var response = cellLocationMapper.cellLocationRequestToCellLocation(cellLocationRequest, test);

        assertEquals(test, response.getTest());
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getOpenTestUUID());
        assertEquals(TestConstants.DEFAULT_LOCATION_ID, response.getLocationId());
        assertEquals(TestConstants.DEFAULT_AREA_CODE_FIRST, response.getAreaCode());
        assertEquals(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE, response.getPrimaryScramblingCode());
        assertEquals(TestConstants.DEFAULT_TIME_NS, response.getTimeNs());
        assertEquals(TestConstants.DEFAULT_ZONED_DATE_TIME, response.getTime());
    }
}