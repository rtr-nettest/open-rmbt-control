package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.enums.NetworkGroupName;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.model.LoopModeSettings;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.request.SignalRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static at.rtr.rmbt.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SignalMapperImplTest {
    private SignalMapper mapper;

    @Mock
    private RtrClient client;
    @Mock
    private LoopModeSettings loopModeSettings;
    @Mock
    private RadioCell radioCell;
    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private SignalRequest signalRequest;

    @MockBean
    private UUIDGenerator uuidGenerator;

    @Before
    public void setUp() {
        mapper = new SignalMapperImpl(uuidGenerator);
    }

    @Test
    public void signalToSignalMeasurementResponse_correctParameters_expectResponse() {
        var test = buildTest(NetworkGroupName.G2, null);
        var actual = mapper.signalToSignalMeasurementResponse(test);
        assertEquals(5, actual.getDuration());
        assertEquals(DEFAULT_ZONED_DATE_TIME, actual.getTime());
        assertEquals(DEFAULT_LOCATION, actual.getLocation());
        assertEquals(NetworkGroupName.G2.getLabelEn(), actual.getTechnology());
        assertEquals("Regular", actual.getTestType());
        assertEquals(DEFAULT_CLIENT_UUID, actual.getUserUuid());
        assertEquals(DEFAULT_UUID, actual.getTestUuid());
    }

    @Test
    public void signalToSignalMeasurementResponse_correctParametersWithNull_expectResponse() {
        var test = buildTest(null, loopModeSettings);
        test.setRadioCell(Collections.singletonList(radioCell));
        when(radioCell.getTechnology()).thenReturn(NetworkGroupName.G2);
        var actual = mapper.signalToSignalMeasurementResponse(test);
        assertEquals(5, actual.getDuration());
        assertEquals(DEFAULT_ZONED_DATE_TIME, actual.getTime());
        assertEquals(DEFAULT_LOCATION, actual.getLocation());
        assertEquals(NetworkGroupName.G2.getLabelEn(), actual.getTechnology());
        assertEquals("Loop", actual.getTestType());
        assertEquals(DEFAULT_CLIENT_UUID, actual.getUserUuid());
        assertEquals(DEFAULT_UUID, actual.getTestUuid());
    }

    @Test
    public void signalToSignalMeasurementResponse_correctParametersWithNullInRadioCell_expectResponse() {
        var test = buildTest(null, loopModeSettings);
        test.setRadioCell(Collections.singletonList(radioCell));
        when(radioCell.getTechnology()).thenReturn(null);
        var actual = mapper.signalToSignalMeasurementResponse(test);
        assertEquals(5, actual.getDuration());
        assertEquals(DEFAULT_ZONED_DATE_TIME, actual.getTime());
        assertEquals(DEFAULT_LOCATION, actual.getLocation());
        assertNull(actual.getTechnology());
        assertEquals("Loop", actual.getTestType());
        assertEquals(DEFAULT_CLIENT_UUID, actual.getUserUuid());
        assertEquals(DEFAULT_UUID, actual.getTestUuid());
    }

    @Test
    public void signalRequestToSignal_whenCommonRequest_expectResponse() {
        when(uuidGenerator.generateUUID()).thenReturn(DEFAULT_UUID);
        when(test.getOpenTestUuid()).thenReturn(DEFAULT_TEST_UUID);
        when(test.getTimezone()).thenReturn(DEFAULT_TIMEZONE);
        when(uuidGenerator.generateUUID()).thenReturn(DEFAULT_UUID);
        when(signalRequest.getLteRSRQ()).thenReturn(DEFAULT_LTE_RSRQ_FIRST);
        when(signalRequest.getLteRSRP()).thenReturn(DEFAULT_LTE_RSRP_FIRST);
        when(signalRequest.getLteCQI()).thenReturn(DEFAULT_LTE_CQI_FIRST);
        when(signalRequest.getLteRSSNR()).thenReturn(DEFAULT_LTE_RSSNR);
        when(signalRequest.getNetworkTypeId()).thenReturn(DEFAULT_NETWORK_TYPE_ID);
        when(signalRequest.getSignalStrength()).thenReturn(DEFAULT_SIGNAL_STRENGTH_FIRST);
        when(signalRequest.getTime()).thenReturn(DEFAULT_TIME_INSTANT);
        when(signalRequest.getGsmBitErrorRate()).thenReturn(DEFAULT_GSM_BIT_ERROR_RATE);
        when(signalRequest.getTimeNs()).thenReturn(DEFAULT_TIME_NS);
        when(signalRequest.getWifiLinkSpeed()).thenReturn(DEFAULT_WIFI_LINK_SPEED_FIRST);
        when(signalRequest.getWifiRSSI()).thenReturn(DEFAULT_WIFI_RSSI_FIRST);


        var response = mapper.signalRequestToSignal(signalRequest, test);

        assertEquals(test, response.getTest());
        assertEquals(DEFAULT_TEST_UUID, response.getOpenTestUUID());
        assertEquals(DEFAULT_GSM_BIT_ERROR_RATE, response.getGsmBitErrorRate());
        assertEquals(DEFAULT_LTE_CQI_FIRST, response.getLteCQI());
        assertEquals(DEFAULT_LTE_RSRP_FIRST, response.getLteRSRP());
        assertEquals(DEFAULT_LTE_RSRQ_FIRST, response.getLteRSRQ());
        assertEquals(DEFAULT_LTE_RSSNR, response.getLteRSSNR());
        assertEquals(DEFAULT_NETWORK_TYPE_ID, response.getNetworkTypeId());
        assertEquals(DEFAULT_SIGNAL_STRENGTH_FIRST, response.getSignalStrength());
        assertEquals(DEFAULT_ZONED_DATE_TIME, response.getTime());
        assertEquals(DEFAULT_TIME_NS, response.getTimeNs());
        assertEquals(DEFAULT_WIFI_LINK_SPEED_FIRST, response.getWifiLinkSpeed());
        assertEquals(DEFAULT_WIFI_RSSI_FIRST, response.getWifiRSSI());
        assertEquals(DEFAULT_UUID, response.getSignalUUID());
    }

    private at.rtr.rmbt.model.Test buildTest(NetworkGroupName ngn, LoopModeSettings lms) {
        when(client.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        return at.rtr.rmbt.model.Test.builder()
                .duration(5)
                .time(DEFAULT_ZONED_DATE_TIME)
                .location(DEFAULT_LOCATION)
                .uuid(DEFAULT_UUID)
                .networkGroupName(ngn)
                .loopModeSettings(lms)
                .client(client)
                .build();
    }

}
