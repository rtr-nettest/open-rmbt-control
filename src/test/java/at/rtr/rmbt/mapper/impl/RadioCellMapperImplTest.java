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
    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        radioCellMapper = new RadioCellMapperImpl();
    }

    @Test
    public void radioCellRequestToRadioCell_whenCommonData_expectRadioCell() {
        when(radioCellRequest.getUuid()).thenReturn(TestConstants.DEFAULT_UUID);
        when(radioCellRequest.getMnc()).thenReturn(TestConstants.DEFAULT_MNC);
        when(radioCellRequest.getMcc()).thenReturn(TestConstants.DEFAULT_MCC);
        when(radioCellRequest.getLocationId()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST);
        when(radioCellRequest.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE_FIRST);
        when(radioCellRequest.getPrimaryScramblingCode()).thenReturn(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE);
        when(radioCellRequest.getTechnology()).thenReturn(TestConstants.DEFAULT_TECHNOLOGY_FIRST);
        when(radioCellRequest.getChannelNumber()).thenReturn(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST);
        when(radioCellRequest.isRegistered()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(radioCellRequest.isActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);

        var response = radioCellMapper.radioCellRequestToRadioCell(radioCellRequest, test);

        assertEquals(TestConstants.DEFAULT_UUID, response.getUuid());
        assertEquals(TestConstants.DEFAULT_MNC, response.getMnc());
        assertEquals(TestConstants.DEFAULT_MCC, response.getMcc());
        assertEquals(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST, response.getLocationId());
        assertEquals(TestConstants.DEFAULT_AREA_CODE_FIRST, response.getAreaCode());
        assertEquals(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE, response.getPrimaryScramblingCode());
        assertEquals(TestConstants.DEFAULT_TECHNOLOGY_FIRST, response.getTechnology());
        assertEquals(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST, response.getChannelNumber());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, response.isRegistered());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, response.isActive());
        assertEquals(test, response.getTest());
    }
}