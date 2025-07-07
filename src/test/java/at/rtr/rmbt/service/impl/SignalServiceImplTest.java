package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.HeaderConstants;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.repository.*;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.*;
import at.rtr.rmbt.utils.HelperFunctions;
import com.google.common.net.InetAddresses;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Geometry;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.*;

import static at.rtr.rmbt.TestConstants.*;
import static at.rtr.rmbt.constant.URIConstants.SIGNAL_RESULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class SignalServiceImplTest {
    private SignalService signalService;

    @MockBean
    private TestRepository testRepository;
    @MockBean
    private ProviderRepository providerRepository;
    @MockBean
    private UUIDGenerator uuidGenerator;
    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private SignalMapper signalMapper;
    @MockBean
    private RadioCellService radioCellService;
    @MockBean
    private GeoLocationService geoLocationService;
    @MockBean
    private RadioSignalRepository radioSignalRepository;
    @MockBean
    private GeoLocationRepository geoLocationRepository;
    @MockBean
    private GeoLocationMapper geoLocationMapper;
    @MockBean
    private RadioSignalService radioSignalService;
    @MockBean
    private FencesService fencesService;
    @MockBean
    private TestServerService testServerService;
    @MockBean
    private TestMapper testMapper;
    @MockBean
    private SignalRepository signalRepository;

    @Mock
    private SignalRegisterRequest signalRegisterRequest;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private at.rtr.rmbt.model.Test savedTest;
    @Mock
    private RtrClient rtrClient;
    @Mock
    private Pageable pageable;
    @Mock
    private Page<at.rtr.rmbt.model.Test> page;
    @Mock
    private SignalMeasurementResponse signalMeasurementResponse;
    @Mock
    private SignalResultRequest signalResultRequest;
    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private RadioInfoRequest radioInfoRequest;
    @Mock
    private RadioCellRequest radioCellRequest;
    @Mock
    private RadioSignalRequest radioSignalRequest;
    @Mock
    private RadioCell radioCell;
    @Mock
    private RadioSignal radioSignal;
    @Mock
    private GeoLocationRequest geoLocationRequestFirst;
    @Mock
    private GeoLocationRequest geoLocationRequestSecond;
    @Mock
    private GeoLocation geoLocationFirst;
    @Mock
    private GeoLocation geoLocationSecond;
    @Mock
    private Geometry geometryLocation;
    @Mock
    private SignalRequest signalRequestFirst;
    @Mock
    private SignalRequest signalRequestSecond;
    @Mock
    private Signal signalFirst;
    @Mock
    private Signal signalSecond;
    @Captor
    private ArgumentCaptor<at.rtr.rmbt.model.Test> testArgumentCaptor;

    private final Map<String, String> headers = new HashMap<>();

    @Before
    public void setUp() {
        signalService = new SignalServiceImpl(testRepository, providerRepository,
                uuidGenerator, clientRepository, signalMapper, radioSignalRepository, geoLocationRepository, testMapper, geoLocationService,
                radioCellService, radioSignalService, signalRepository, fencesService, testServerService);
    }

    @Test
    // DZ: New implementation currently not supported
    @Ignore
        public void registerSignal_whenCommonRequest_expectSignalResponse() {
        var expectedResponse = getProcessSignalRequestResponse();
        when(httpServletRequest.getLocalAddr()).thenReturn(TestConstants.DEFAULT_IP_V4);
        when(httpServletRequest.getHeader(HeaderConstants.URL)).thenReturn(TestConstants.DEFAULT_URL);
        when(signalRegisterRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalRegisterRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(providerRepository.getProviderNameByTestId(TestConstants.DEFAULT_UID)).thenReturn(TestConstants.DEFAULT_PROVIDER);
        when(testRepository.saveAndFlush(any())).thenReturn(savedTest);
        when(savedTest.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(savedTest.getUuid()).thenReturn(TestConstants.DEFAULT_UUID);

        var actualResponse = signalService.processSignalRequest(signalRegisterRequest, httpServletRequest, headers);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getSignalsHistory_correctInvocation_expectPageWithResponse() {
        when(testRepository.findAllByRadioCellIsNotEmptyAndNetworkTypeNotIn(eq(pageable), eq(Collections.singletonList(99))))
                .thenReturn(new PageImpl<>(Collections.singletonList(savedTest)));
        when(page.getContent()).thenReturn(Collections.singletonList(savedTest));
        when(signalMapper.signalToSignalMeasurementResponse(savedTest)).thenReturn(signalMeasurementResponse);
        var actual = signalService.getSignalsHistory(pageable);
        assertEquals(signalMeasurementResponse, actual.getContent().get(0));
        assertEquals(1, actual.getContent().size());
    }

    @Test
    public void processSignalResult_whenTestExist_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(2L);
        when(signalResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getLastSequenceNumber()).thenReturn(1);
        when(test.getUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(test.getTimestamp()).thenReturn(DEFAULT_TEST_TIME);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));

        var response = signalService.processSignalResult(signalResultRequest);

        verify(test).setLastSequenceNumber(2);
        verify(testRepository).saveAndFlush(test);
        verify(testMapper).updateTestWithSignalResultRequest(signalResultRequest, test);
        assertNotEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
    }


    @Test
    public void processSignalResult_whenTestNotExist_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(0L);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(uuidGenerator.generateUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testRepository.saveAndFlush(any())).thenReturn(test);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(test.getUuid()).thenReturn(TestConstants.DEFAULT_UUID);
        when(test.getLastSequenceNumber()).thenReturn(-1);
        when(test.getTimestamp()).thenReturn(DEFAULT_TEST_TIME);

        var response = signalService.processSignalResult(signalResultRequest);

        assertNotEquals(TestConstants.DEFAULT_UUID, response.getTestUUID());
        verify(testRepository, times(2)).saveAndFlush(testArgumentCaptor.capture());
        verify(testMapper).updateTestWithSignalResultRequest(signalResultRequest, test);
        assertEquals(TestConstants.DEFAULT_TEST_UUID, testArgumentCaptor.getAllValues().get(0).getOpenTestUuid());
    }

    @Test
    public void processSignalResult_whenTestExistAndRadioInfo_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(2L);
        when(signalResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(signalResultRequest.getRadioInfo()).thenReturn(radioInfoRequest);
        when(radioInfoRequest.getCells()).thenReturn(List.of(radioCellRequest));
        when(radioInfoRequest.getSignals()).thenReturn(List.of(radioSignalRequest));
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getLastSequenceNumber()).thenReturn(1);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_UUID);
        when(test.getUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(test.getTimestamp()).thenReturn(DEFAULT_TEST_TIME);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));

        var response = signalService.processSignalResult(signalResultRequest);

        verify(test).setLastSequenceNumber(2);
        verify(testRepository).saveAndFlush(test);
        verify(testMapper).updateTestWithSignalResultRequest(signalResultRequest, test);
        verify(radioSignalService).saveRadioSignalRequests(radioInfoRequest, test);
        verify(radioCellService).processRadioCellRequests(List.of(radioCellRequest), test);
        assertNotEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
    }

    @Test
    public void processSignalResult_whenTestExistAndRadioSignalRequestIsNull_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(2L);
        when(signalResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(signalResultRequest.getRadioInfo()).thenReturn(radioInfoRequest);
        when(radioInfoRequest.getCells()).thenReturn(List.of(radioCellRequest));
        when(radioInfoRequest.getSignals()).thenReturn(null);
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getTimestamp()).thenReturn(DEFAULT_TEST_TIME);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));

        signalService.processSignalResult(signalResultRequest);

        verifyNoInteractions(radioSignalService);
        verify(radioCellService).processRadioCellRequests(List.of(radioCellRequest), test);
    }

    @Test
    public void processSignalResult_whenTestExistAndRadioCellRequestIsNull_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(2L);
        when(signalResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(signalResultRequest.getRadioInfo()).thenReturn(radioInfoRequest);
        when(radioInfoRequest.getCells()).thenReturn(null);
        when(radioInfoRequest.getSignals()).thenReturn(List.of(radioSignalRequest));
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getTimestamp()).thenReturn(DEFAULT_TEST_TIME);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));

        signalService.processSignalResult(signalResultRequest);

        verify(radioSignalService).saveRadioSignalRequests(radioInfoRequest, test);
        verifyNoInteractions(radioCellService);
    }

    @Test
    public void processSignalResult_whenTestExistAndGeoLocation_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(2L);
        when(signalResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(signalResultRequest.getGeoLocations()).thenReturn(List.of(geoLocationRequestFirst, geoLocationRequestSecond));
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getLastSequenceNumber()).thenReturn(1);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_UUID);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(test.getUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(test.getTimestamp()).thenReturn(DEFAULT_TEST_TIME);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(geoLocationRequestFirst.getTstamp()).thenReturn(TestConstants.DEFAULT_MILLIS);
        when(geoLocationRequestFirst.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationRequestFirst.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationRequestFirst.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(geoLocationRequestFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationFirst.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationFirst.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationFirst.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationFirst.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);
        when(geoLocationRequestSecond.getTstamp()).thenReturn(TestConstants.DEFAULT_MILLIS);
        when(geoLocationRequestSecond.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE_SECOND);
        when(geoLocationRequestSecond.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE_SECOND);
        when(geoLocationRequestSecond.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(geoLocationRequestSecond.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_SECOND);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestSecond, test)).thenReturn(geoLocationSecond);

        var response = signalService.processSignalResult(signalResultRequest);

        verify(test).setLastSequenceNumber(2);
        verify(testRepository).saveAndFlush(test);
        verify(testMapper).updateTestWithSignalResultRequest(signalResultRequest, test);
        verify(geoLocationService).processGeoLocationRequests(List.of(geoLocationRequestFirst, geoLocationRequestSecond), test);
        assertNotEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
    }

    @Test
    public void processSignalResult_whenTestExistAndIpAddress_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(2L);
        when(signalResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(signalResultRequest.getTestIpLocal()).thenReturn(TestConstants.DEFAULT_IP_V4);
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getLastSequenceNumber()).thenReturn(1);
        when(test.getClientPublicIp()).thenReturn(TestConstants.DEFAULT_IP_V4);
        when(test.getUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(test.getTimestamp()).thenReturn(DEFAULT_TEST_TIME);
        InetAddress defaultIpLocalAddress = InetAddresses.forString(TestConstants.DEFAULT_IP_V4);
        InetAddress defaultIpPublicAddress = InetAddresses.forString(TestConstants.DEFAULT_IP_V4);

        var response = signalService.processSignalResult(signalResultRequest);

        verify(test).setLastSequenceNumber(2);
        verify(test).setClientIpLocal(InetAddresses.toAddrString(defaultIpLocalAddress));
        verify(test).setClientIpLocalAnonymized(HelperFunctions.anonymizeIp(defaultIpLocalAddress));
        verify(test).setClientIpLocalType(HelperFunctions.IpType(defaultIpLocalAddress));
        verify(test).setNatType(HelperFunctions.getNatType(defaultIpLocalAddress, defaultIpPublicAddress));
        verify(testRepository).saveAndFlush(test);
        verify(testMapper).updateTestWithSignalResultRequest(signalResultRequest, test);
        assertNotEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
    }

    @Test
    public void getSignalStrength_whenCommonData_expectListSignalStrengthResponse() {
        var response = getSignalStrengthResponse();

        when(testRepository.findByUuid(TestConstants.DEFAULT_UUID))
                .thenReturn(Optional.of(test));
        when(test.getRadioCell()).thenReturn(List.of(radioCell));
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_TEST_TIME);
        when(radioCell.getUuid()).thenReturn(TestConstants.DEFAULT_RADIO_CELL_UUID);
        when(radioCell.getLocationId()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST);
        when(radioCell.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE_FIRST);
        when(radioCell.getPrimaryScramblingCode()).thenReturn(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE);
        when(radioCell.getChannelNumber()).thenReturn(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST);
        when(radioCell.getTechnology()).thenReturn(TestConstants.DEFAULT_TECHNOLOGY_FIRST);
        when(geoLocationRepository.findAllByTestOrderByTimeAsc(test)).thenReturn(List.of(geoLocationFirst));
        when(geoLocationFirst.getId()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST);
        when(geoLocationFirst.getSpeed()).thenReturn(TestConstants.DEFAULT_SPEED);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationFirst.getAltitude()).thenReturn(TestConstants.DEFAULT_ALTITUDE);
        when(geoLocationFirst.getBearing()).thenReturn(TestConstants.DEFAULT_BEARING);
        when(geoLocationFirst.getLocation()).thenReturn(geometryLocation);
        when(geoLocationFirst.getTimeNs()).thenReturn(DEFAULT_TIME_NS);
        when(radioSignalRepository.findAllByCellUUIDInOrderByTimeAsc(Set.of(TestConstants.DEFAULT_RADIO_CELL_UUID))).thenReturn(List.of(radioSignal));
        when(radioSignal.getTimeNs()).thenReturn(DEFAULT_TIME_NS);
        when(radioSignal.getCellUUID()).thenReturn(TestConstants.DEFAULT_RADIO_CELL_UUID);
        when(radioSignal.getSignalStrength()).thenReturn(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST);
        when(radioSignal.getTimingAdvance()).thenReturn(TestConstants.DEFAULT_TIMING_ADVANCE);
        when(radioSignal.getLteRSRQ()).thenReturn(TestConstants.DEFAULT_LTE_RSRQ_FIRST);
        when(radioSignal.getNetworkTypeId()).thenReturn(DEFAULT_NETWORK_ID);
        when(testMapper.testToTestResponse(test)).thenReturn(getTestResponse());

        var actual = signalService.getSignalStrength(TestConstants.DEFAULT_UUID);

        assertEquals(response, actual);
    }

    @Test
    public void processSignalRequests_whenCommonDataAndNetworkTypeIsNotWLAN_expectNewSignalSaved() {
        var requests = List.of(signalRequestFirst, signalRequestSecond);
        when(signalMapper.signalRequestToSignal(signalRequestFirst, test)).thenReturn(signalFirst);
        when(signalMapper.signalRequestToSignal(signalRequestSecond, test)).thenReturn(signalSecond);
        when(signalFirst.getSignalStrength()).thenReturn(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST);
        when(signalFirst.getLteRSRP()).thenReturn(TestConstants.DEFAULT_LTE_RSRP_FIRST);
        when(signalFirst.getLteRSRQ()).thenReturn(TestConstants.DEFAULT_LTE_RSRQ_FIRST);
        when(signalFirst.getWifiLinkSpeed()).thenReturn(TestConstants.DEFAULT_WIFI_LINK_SPEED_FIRST);
        when(signalSecond.getSignalStrength()).thenReturn(TestConstants.DEFAULT_SIGNAL_STRENGTH_SECOND);
        when(signalSecond.getLteRSRP()).thenReturn(TestConstants.DEFAULT_LTE_RSRP_SECOND);
        when(signalSecond.getLteRSRQ()).thenReturn(TestConstants.DEFAULT_LTE_RSRQ_SECOND);
        when(signalSecond.getWifiLinkSpeed()).thenReturn(TestConstants.DEFAULT_WIFI_LINK_SPEED_SECOND);
        signalService.processSignalRequests(requests, test);

        verify(signalRepository).saveAll(List.of(signalFirst, signalSecond));
        verify(test).setSignalStrength(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST);
        verify(test).setLteRsrp(TestConstants.DEFAULT_LTE_RSRP_FIRST);
        verify(test).setLteRsrq(TestConstants.DEFAULT_LTE_RSRQ_SECOND);
        verify(test).setWifiLinkSpeed(TestConstants.DEFAULT_WIFI_LINK_SPEED_FIRST);
    }

    @Test
    public void processSignalRequests_whenCommonDataAndNetworkTypeIsWLAN_expectNewSignalSaved() {
        var requests = List.of(signalRequestFirst, signalRequestSecond);
        when(test.getNetworkType()).thenReturn(99);
        when(signalMapper.signalRequestToSignal(signalRequestFirst, test)).thenReturn(signalFirst);
        when(signalMapper.signalRequestToSignal(signalRequestSecond, test)).thenReturn(signalSecond);
        when(signalFirst.getWifiRSSI()).thenReturn(TestConstants.DEFAULT_WIFI_RSSI_FIRST);
        when(signalFirst.getLteRSRP()).thenReturn(TestConstants.DEFAULT_LTE_RSRP_FIRST);
        when(signalFirst.getLteRSRQ()).thenReturn(TestConstants.DEFAULT_LTE_RSRQ_FIRST);
        when(signalFirst.getWifiLinkSpeed()).thenReturn(TestConstants.DEFAULT_WIFI_LINK_SPEED_FIRST);
        when(signalSecond.getWifiRSSI()).thenReturn(TestConstants.DEFAULT_WIFI_RSSI_SECOND);
        when(signalSecond.getLteRSRP()).thenReturn(TestConstants.DEFAULT_LTE_RSRP_SECOND);
        when(signalSecond.getLteRSRQ()).thenReturn(TestConstants.DEFAULT_LTE_RSRQ_SECOND);
        when(signalSecond.getWifiLinkSpeed()).thenReturn(TestConstants.DEFAULT_WIFI_LINK_SPEED_SECOND);
        signalService.processSignalRequests(requests, test);

        verify(signalRepository).saveAll(List.of(signalFirst, signalSecond));
        verify(test).setSignalStrength(TestConstants.DEFAULT_WIFI_RSSI_FIRST);
        verify(test).setLteRsrp(TestConstants.DEFAULT_LTE_RSRP_FIRST);
        verify(test).setLteRsrq(TestConstants.DEFAULT_LTE_RSRQ_SECOND);
        verify(test).setWifiLinkSpeed(TestConstants.DEFAULT_WIFI_LINK_SPEED_FIRST);
    }

    @Test
    public void getSignalStrength_whenCommonDataStrengthNull_expectListSignalStrengthResponse() {
        when(testRepository.findByUuid(TestConstants.DEFAULT_UUID))
                .thenReturn(Optional.of(test));
        when(test.getRadioCell()).thenReturn(List.of(radioCell));
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_TEST_TIME);
        when(radioCell.getUuid()).thenReturn(TestConstants.DEFAULT_RADIO_CELL_UUID);
        when(radioCell.getLocationId()).thenReturn(TestConstants.DEFAULT_LOCATION_ID_LONG);
        when(radioCell.getAreaCode()).thenReturn(TestConstants.DEFAULT_AREA_CODE_FIRST);
        when(radioCell.getPrimaryScramblingCode()).thenReturn(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE);
        when(radioCell.getChannelNumber()).thenReturn(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST);
        when(radioCell.getTechnology()).thenReturn(TestConstants.DEFAULT_TECHNOLOGY_FIRST);
        when(geoLocationRepository.findAllById(Set.of(TestConstants.DEFAULT_LOCATION_ID_LONG))).thenReturn(List.of(geoLocationFirst));
        when(geoLocationFirst.getId()).thenReturn(TestConstants.DEFAULT_LOCATION_ID_LONG);
        when(geoLocationFirst.getSpeed()).thenReturn(TestConstants.DEFAULT_SPEED);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationFirst.getAltitude()).thenReturn(TestConstants.DEFAULT_ALTITUDE);
        when(geoLocationFirst.getBearing()).thenReturn(TestConstants.DEFAULT_BEARING);
        when(geoLocationFirst.getLocation()).thenReturn(geometryLocation);
        when(radioSignalRepository.findAllByCellUUIDInOrderByTimeAsc(Set.of(TestConstants.DEFAULT_RADIO_CELL_UUID))).thenReturn(List.of(radioSignal));
        when(radioSignal.getTimeNs()).thenReturn(DEFAULT_TIME_NS);
        when(radioSignal.getCellUUID()).thenReturn(TestConstants.DEFAULT_RADIO_CELL_UUID);
        when(radioSignal.getLteRSRP()).thenReturn(TestConstants.DEFAULT_LTE_RSRP_FIRST);
        when(radioSignal.getSignalStrength()).thenReturn(null);
        when(radioSignal.getTimingAdvance()).thenReturn(TestConstants.DEFAULT_TIMING_ADVANCE);
        when(radioSignal.getLteRSRQ()).thenReturn(TestConstants.DEFAULT_LTE_RSRQ_FIRST);
        when(testMapper.testToTestResponse(test)).thenReturn(getTestResponse());

        var expectedResponse = signalService.getSignalStrength(TestConstants.DEFAULT_UUID);

        assertEquals("-5 dBm, TA: 32, RSRQ: -11 dB", expectedResponse.getSignalStrength().get(0).getSignalStrength());
    }

    private SignalDetailsResponse getSignalStrengthResponse() {
        return SignalDetailsResponse.builder()
                .signalStrength(Collections.singletonList(
                        SignalStrengthResponse.builder()
                                .technology(DEFAULT_NETWORK_NAME)
                                .band(TestConstants.DEFAULT_BAND)
                                .ci(TestConstants.DEFAULT_AREA_CODE_FIRST)
                                .earfcn(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST)
                                .frequency(TestConstants.DEFAULT_FREQUENCY)
                                .pci(TestConstants.DEFAULT_PRIMARY_SCRAMBLING_CODE)
                                .signalStrength(TestConstants.DEFAULT_SIGNAL_STRENGTH_RESPONSE)
                                .tac(TestConstants.DEFAULT_GEO_LOCATION_UID_FIRST)
                                .time(0.385)
                                .build()))
                .signalLocation(Collections.singletonList(
                        SignalLocationResponse.builder()
                                .bearing(TestConstants.DEFAULT_SIGNAL_STRENGTH_BEARING_RESPONSE)
                                .altitude(TestConstants.DEFAULT_SIGNAL_STRENGTH_ALTITUDE_RESPONSE)
                                .accuracy(TestConstants.DEFAULT_SIGNAL_STRENGTH_ACCURACY_RESPONSE)
                                .speed(TestConstants.DEFAULT_SIGNAL_STRENGTH_SPEED_RESPONSE)
                                .location(geometryLocation)
                                .time(0.385)
                                .build()))
                .testResponse(getTestResponse())
                .build();
    }

    private TestResponse getTestResponse() {
        return TestResponse.builder()
                .testUUID(TestConstants.DEFAULT_TEST_UUID)
                .time(TestConstants.DEFAULT_TEST_TIME)
                .build();
    }

    private SignalSettingsResponse getProcessSignalRequestResponse() {
        return SignalSettingsResponse.builder()
                .resultUrl(String.join(TestConstants.DEFAULT_URL, SIGNAL_RESULT))
                .clientRemoteIp(TestConstants.DEFAULT_IP_V4)
                .provider(TestConstants.DEFAULT_PROVIDER)
                .testUUID(TestConstants.DEFAULT_UUID)
                .build();
    }


}
