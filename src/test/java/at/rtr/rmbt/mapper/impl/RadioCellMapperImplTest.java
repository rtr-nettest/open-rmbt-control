package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.RadioCellMapper;
import at.rtr.rmbt.request.RadioCellRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RadioCellMapperImplTest {
    private RadioCellMapper radioCellMapper;

    @Mock
    private RadioCellRequest radioCellRequest;

    @Before
    public void setUp() {
        radioCellMapper = new RadioCellMapperImpl();
    }

    @Test
    public void radioCellRequestToRadioCell_whenCommonData_expectRadioCell() {
        when(radioCellRequest.getUuid()).thenReturn(TestConstants.DEFAULT_UUID);
        when(radioCellRequest.getMnc()).thenReturn(TestConstants.DEFAULT_MNC);
        when(radioCellRequest.getMcc()).thenReturn(TestConstants.DEFAULT_MCC);
        when(radioCellRequest.getLocationId()).thenReturn(TestConstants.DEFAULT_LOCATION_ID);
        when(radioCellRequest.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE);
        when(radioCellRequest.getPrimaryScramblingCode()).thenReturn(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE);
        when(radioCellRequest.getTechnology()).thenReturn(TestConstants.DEFAULT_TECHNOLOGY);
        when(radioCellRequest.getChannelNumber()).thenReturn(TestConstants.DEFAULT_CHANNEL_NUMBER);
        when(radioCellRequest.isRegistered()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(radioCellRequest.isActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);

        var response = radioCellMapper.radioCellRequestToRadioCell(radioCellRequest);

        assertEquals(TestConstants.DEFAULT_UUID, response.getUuid());
        assertEquals(TestConstants.DEFAULT_MNC, response.getMnc());
        assertEquals(TestConstants.DEFAULT_MCC, response.getMcc());
        assertEquals(TestConstants.DEFAULT_LOCATION_ID, response.getLocationId());
        assertEquals(TestConstants.DEFAULT_AREA_CODE, response.getAreaCode());
        assertEquals(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE, response.getPrimaryScramblingCode());
        assertEquals(TestConstants.DEFAULT_TECHNOLOGY, response.getTechnology());
        assertEquals(TestConstants.DEFAULT_CHANNEL_NUMBER, response.getChannelNumber());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, response.isRegistered());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, response.isActive());
    }
}