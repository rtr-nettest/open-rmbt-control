package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.request.RadioSignalRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RadioSignalMapperImplTest {
    private RadioSignalMapper radioSignalMapper;


    @Mock
    private RadioSignalRequest radioSignalRequest;

    @Before
    public void setUp() {
        radioSignalMapper = new RadioSignalMapperImpl();
    }

    @Test
    public void radioSignalRequestToRadioSignal_whenCommonData_expectRadioSignal() {
        when(radioSignalRequest.getCellUUID()).thenReturn(TestConstants.DEFAULT_UUID);
        when(radioSignalRequest.getNetworkTypeId()).thenReturn(TestConstants.DEFAULT_NETWORK_TYPE_ID);
        when(radioSignalRequest.getBitErrorRate()).thenReturn(TestConstants.DEFAULT_BIT_ERROR_RATE);
        when(radioSignalRequest.getWifiLinkSpeed()).thenReturn(TestConstants.DEFAULT_WIFI_LINK_SPEED);
        when(radioSignalRequest.getLteCQI()).thenReturn(TestConstants.DEFAULT_LTE_SQI);
        when(radioSignalRequest.getLteRSSNR()).thenReturn(TestConstants.DEFAULT_LTE_RSSNR);
        when(radioSignalRequest.getLteRSRP()).thenReturn(TestConstants.DEFAULT_LTE_RSRP);
        when(radioSignalRequest.getLteRSRQ()).thenReturn(TestConstants.DEFAULT_LTE_RSRQ);
        when(radioSignalRequest.getSignal()).thenReturn(TestConstants.DEFAULT_SIGNAL_STRENGTH);
        when(radioSignalRequest.getTimingAdvance()).thenReturn(TestConstants.DEFAULT_TIMING_ADVANCE);
        when(radioSignalRequest.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(radioSignalRequest.getTimeNsLast()).thenReturn(TestConstants.DEFAULT_TIME_NS_LAST);

        var response = radioSignalMapper.radioSignalRequestToRadioSignal(radioSignalRequest);

        assertEquals(TestConstants.DEFAULT_UUID, response.getCellUUID());
        assertEquals(TestConstants.DEFAULT_NETWORK_TYPE_ID, response.getNetworkTypeId());
        assertEquals(TestConstants.DEFAULT_BIT_ERROR_RATE, response.getBitErrorRate());
        assertEquals(TestConstants.DEFAULT_WIFI_LINK_SPEED, response.getWifiLinkSpeed());
        assertEquals(TestConstants.DEFAULT_LTE_SQI, response.getLteCQI());
        assertEquals(TestConstants.DEFAULT_LTE_RSSNR, response.getLteRSSNR());
        assertEquals(TestConstants.DEFAULT_LTE_RSRP, response.getLteRSRP());
        assertEquals(TestConstants.DEFAULT_LTE_RSRQ, response.getLteRSRQ());
        assertEquals(TestConstants.DEFAULT_SIGNAL_STRENGTH, response.getSignalStrength());
        assertEquals(TestConstants.DEFAULT_TIMING_ADVANCE, response.getTimingAdvance());
        assertEquals(TestConstants.DEFAULT_TIME_NS, response.getTimeNs());
        assertEquals(TestConstants.DEFAULT_TIME_NS_LAST, response.getTimeNsLast());
    }
}