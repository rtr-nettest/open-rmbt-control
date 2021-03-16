package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.request.RadioSignalRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RadioSignalMapperImplTest {
    private RadioSignalMapper radioSignalMapper;

    @MockBean
    private UUIDGenerator uuidGenerator;

    @Mock
    private RadioSignalRequest radioSignalRequest;
    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        radioSignalMapper = new RadioSignalMapperImpl(uuidGenerator);
    }

    @Test
    public void radioSignalRequestToRadioSignal_whenCommonData_expectRadioSignal() {
        when(uuidGenerator.generateUUID()).thenReturn(TestConstants.DEFAULT_UUID);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(radioSignalRequest.getCellUUID()).thenReturn(TestConstants.DEFAULT_UUID);
        when(radioSignalRequest.getNetworkTypeId()).thenReturn(TestConstants.DEFAULT_NETWORK_TYPE_ID);
        when(radioSignalRequest.getBitErrorRate()).thenReturn(TestConstants.DEFAULT_BIT_ERROR_RATE);
        when(radioSignalRequest.getWifiLinkSpeed()).thenReturn(TestConstants.DEFAULT_WIFI_LINK_SPEED_FIRST);
        when(radioSignalRequest.getLteCQI()).thenReturn(TestConstants.DEFAULT_LTE_CQI_FIRST);
        when(radioSignalRequest.getLteRSSNR()).thenReturn(TestConstants.DEFAULT_LTE_RSSNR);
        when(radioSignalRequest.getLteRSRP()).thenReturn(TestConstants.DEFAULT_LTE_RSRP_FIRST);
        when(radioSignalRequest.getLteRSRQ()).thenReturn(TestConstants.DEFAULT_LTE_RSRQ_FIRST);
        when(radioSignalRequest.getSignal()).thenReturn(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST);
        when(radioSignalRequest.getTimingAdvance()).thenReturn(TestConstants.DEFAULT_TIMING_ADVANCE);
        when(radioSignalRequest.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(radioSignalRequest.getTimeNsLast()).thenReturn(TestConstants.DEFAULT_TIME_NS_LAST);

        var response = radioSignalMapper.radioSignalRequestToRadioSignal(radioSignalRequest, test);

        assertEquals(TestConstants.DEFAULT_UUID, response.getCellUUID());
        assertEquals(TestConstants.DEFAULT_NETWORK_TYPE_ID, response.getNetworkTypeId());
        assertEquals(TestConstants.DEFAULT_BIT_ERROR_RATE, response.getBitErrorRate());
        assertEquals(TestConstants.DEFAULT_WIFI_LINK_SPEED_FIRST, response.getWifiLinkSpeed());
        assertEquals(TestConstants.DEFAULT_LTE_CQI_FIRST, response.getLteCQI());
        assertEquals(TestConstants.DEFAULT_LTE_RSSNR, response.getLteRSSNR());
        assertEquals(TestConstants.DEFAULT_LTE_RSRP_FIRST, response.getLteRSRP());
        assertEquals(TestConstants.DEFAULT_LTE_RSRQ_FIRST, response.getLteRSRQ());
        assertEquals(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST, response.getSignalStrength());
        assertEquals(TestConstants.DEFAULT_TIMING_ADVANCE, response.getTimingAdvance());
        assertEquals(TestConstants.DEFAULT_TIME_NS, response.getTimeNs());
        assertEquals(TestConstants.DEFAULT_TIME_NS_LAST, response.getTimeNsLast());
        assertEquals(TestConstants.DEFAULT_UUID, response.getRadioSignalUUID());
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getOpenTestUUID());
    }
}