package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.RadioCellMapper;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.repository.RadioCellRepository;
import at.rtr.rmbt.request.RadioCellRequest;
import at.rtr.rmbt.service.RadioCellService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class RadioCellServiceImplTest {
    private RadioCellService radioCellService;

    @MockBean
    private RadioCellMapper radioCellMapper;
    @MockBean
    private RadioCellRepository radioCellRepository;

    @Mock
    private RadioCellRequest radioCellRequestFirst;
    @Mock
    private RadioCellRequest radioCellRequestSecond;
    @Mock
    private RadioCell radioCellFirst;
    @Mock
    private RadioCell radioCellSecond;
    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        radioCellService = new RadioCellServiceImpl(radioCellMapper, radioCellRepository);
    }

    @Test
    public void processRadioCellRequests_whenLocationChangedAndAreaCodeChangedAndBandChanged_expectRadioCellSavedAndTestNotModified() {
        var requests = List.of(radioCellRequestFirst, radioCellRequestSecond);
        when(radioCellMapper.radioCellRequestToRadioCell(radioCellRequestFirst, test)).thenReturn(radioCellFirst);
        when(radioCellMapper.radioCellRequestToRadioCell(radioCellRequestSecond, test)).thenReturn(radioCellSecond);
        when(radioCellFirst.isActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(radioCellFirst.getChannelNumber()).thenReturn(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST);
        when(radioCellFirst.getTechnology()).thenReturn(TestConstants.DEFAULT_TECHNOLOGY_FIRST);
        when(radioCellFirst.getLocationId()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST);
        when(radioCellFirst.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE_FIRST);
        when(radioCellSecond.isActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(radioCellSecond.getChannelNumber()).thenReturn(TestConstants.DEFAULT_CHANNEL_NUMBER_SECOND);
        when(radioCellSecond.getTechnology()).thenReturn(TestConstants.DEFAULT_TECHNOLOGY_SECOND);
        when(radioCellSecond.getLocationId()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UID_SECOND);
        when(radioCellSecond.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE_SECOND);

        radioCellService.processRadioCellRequests(requests, test);

        verify(radioCellRepository).saveAll(List.of(radioCellFirst, radioCellSecond));
        verify(test, times(0)).setRadioBand(any());
        verify(test, times(0)).setCellLocationId(any());
        verify(test, times(0)).setCellAreaCode(any());
        verify(test, times(0)).setChannelNumber(any());
    }

    @Test
    public void processRadioCellRequests_whenLocationNotChangedAndAreaCodeNotChangedAndNotBandChanged_expectRadioCellSavedAndTestModified() {
        var requests = List.of(radioCellRequestFirst, radioCellRequestSecond);
        when(radioCellMapper.radioCellRequestToRadioCell(radioCellRequestFirst, test)).thenReturn(radioCellFirst);
        when(radioCellMapper.radioCellRequestToRadioCell(radioCellRequestSecond, test)).thenReturn(radioCellSecond);
        when(radioCellFirst.isActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(radioCellFirst.getChannelNumber()).thenReturn(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST);
        when(radioCellFirst.getTechnology()).thenReturn(TestConstants.DEFAULT_TECHNOLOGY_FIRST);
        when(radioCellFirst.getLocationId()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST);
        when(radioCellFirst.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE_FIRST);
        when(radioCellSecond.isActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(radioCellSecond.getChannelNumber()).thenReturn(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST);
        when(radioCellSecond.getTechnology()).thenReturn(TestConstants.DEFAULT_TECHNOLOGY_SECOND);
        when(radioCellSecond.getLocationId()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST);
        when(radioCellSecond.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE_FIRST);

        radioCellService.processRadioCellRequests(requests, test);

        verify(radioCellRepository).saveAll(List.of(radioCellFirst, radioCellSecond));
        verify(test).setRadioBand(TestConstants.DEFAULT_BAND);
        verify(test).setCellLocationId(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST);
        verify(test).setCellAreaCode(TestConstants.DEFAULT_AREA_CODE_FIRST);
        verify(test).setChannelNumber(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST);
    }
}