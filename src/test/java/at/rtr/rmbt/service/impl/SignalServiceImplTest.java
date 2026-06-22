package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.HeaderConstants;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.repository.*;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Geometry;
import org.mockito.Mock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.servlet.http.HttpServletRequest;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static at.rtr.rmbt.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class SignalServiceImplTest {
    private SignalService signalService;

    @MockitoBean
    private TestRepository testRepository;
    @MockitoBean
    private ProviderRepository providerRepository;
    @MockitoBean
    private UUIDGenerator uuidGenerator;
    @MockitoBean
    private ClientRepository clientRepository;
    @MockitoBean
    private SignalMapper signalMapper;
    @MockitoBean
    private RadioSignalRepository radioSignalRepository;
    @MockitoBean
    private GeoLocationRepository geoLocationRepository;
    @MockitoBean
    private TestMapper testMapper;
    @MockitoBean
    private GeoLocationService geoLocationService;
    @MockitoBean
    private RadioCellService radioCellService;
    @MockitoBean
    private RadioSignalService radioSignalService;
    @MockitoBean
    private SignalRepository signalRepository;
    @MockitoBean
    private FencesService fencesService;
    @MockitoBean
    private TestServerService testServerService;
    @MockitoBean
    private SettingsRepository settingsRepository;
    @MockitoBean
    private LoopModeSettingsService loopModeSettingsService;
    @MockitoBean
    private CellLocationService cellLocationService;
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
    private at.rtr.rmbt.model.Test test;
    @Mock
    private RadioCell radioCell;
    @Mock
    private RadioSignal radioSignal;
    @Mock
    private GeoLocation geoLocationFirst;
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

    @Before
    public void setUp() {
        signalService = new SignalServiceImpl(testRepository, providerRepository,
                uuidGenerator, clientRepository, signalMapper, radioSignalRepository, geoLocationRepository, testMapper,
                geoLocationService, radioCellService, radioSignalService, signalRepository, fencesService,
                testServerService, settingsRepository, loopModeSettingsService, cellLocationService);
    }

    @Test
    public void fencesContainPing_whenNoFences_returnsFalse() {
        assertFalse(SignalServiceImpl.fencesContainPing(null));
        assertFalse(SignalServiceImpl.fencesContainPing(Collections.emptyList()));
    }

    @Test
    public void fencesContainPing_whenAllFencesWithoutPing_returnsFalse() {
        List<FencesRequest> fences = List.of(
                FencesRequest.builder().build(),
                FencesRequest.builder().avgPingMs(null).build());
        assertFalse(SignalServiceImpl.fencesContainPing(fences));
    }

    @Test
    public void fencesContainPing_whenAtLeastOneFenceHasPing_returnsTrue() {
        List<FencesRequest> fences = List.of(
                FencesRequest.builder().build(),
                FencesRequest.builder().avgPingMs(4.42).build());
        assertTrue(SignalServiceImpl.fencesContainPing(fences));
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
    public void processSignalMeasurementResult_whenTestExists_movesToCoverageAndSaves() {
        SignalMeasurementResultRequest signalMeasurementResultRequest = mock(SignalMeasurementResultRequest.class);
        when(signalMeasurementResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(signalMeasurementResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_MEASUREMENT_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        Map<String, String> headers = Map.of(HeaderConstants.IP, "127.0.0.1");

        signalService.processSignalMeasurementResult(signalMeasurementResultRequest, httpServletRequest, headers);

        verify(test).setStatus(TestStatus.COVERAGE);
        verify(testMapper).updateTestWithSignalMeasurementResultRequest(signalMeasurementResultRequest, test);
        verify(testMapper).updateTestLocation(test);
        verify(testRepository).saveAndFlush(test);
    }

    @Test
    public void processSignalMeasurementResult_whenFirstFencePresent_locationTakenFromFirstFence() {
        SignalMeasurementResultRequest request = mock(SignalMeasurementResultRequest.class);
        when(request.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(request.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_MEASUREMENT_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);

        FencesRequest firstFence = FencesRequest.builder()
                .location(SimpleLocationRequest.builder().latitude(48.2).longitude(16.3).accuracy(9.5).provider("network").build())
                .offsetMs(5000L).durationMs(1L).radius(10.0).build();
        FencesRequest secondFence = FencesRequest.builder()
                .location(SimpleLocationRequest.builder().latitude(1.0).longitude(2.0).accuracy(5.0).provider("gps").build())
                .offsetMs(100L).durationMs(1L).radius(10.0).build();
        when(request.getFences()).thenReturn(List.of(firstFence, secondFence));
        Map<String, String> headers = Map.of(HeaderConstants.IP, "127.0.0.1");

        signalService.processSignalMeasurementResult(request, httpServletRequest, headers);

        // A geo_location with a server-generated UUID is created from the first fence (incl. its
        // accuracy/provider and a timestamp derived from test time + fence offset) and assigned to
        // the test.
        var expectedTime = TestConstants.DEFAULT_ZONED_DATE_TIME.plus(5000L, ChronoUnit.MILLIS);
        verify(geoLocationService).createAndAssignGeoLocation(test, 48.2, 16.3, 9.5, "network", expectedTime);
    }

    @Test
    public void processSignalMeasurementResult_whenFenceHasNoAccuracyOrProvider_passesNull() {
        SignalMeasurementResultRequest request = mock(SignalMeasurementResultRequest.class);
        when(request.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(request.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(testRepository.findByUuidAndStatusesInLocked(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_MEASUREMENT_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);

        FencesRequest fence = FencesRequest.builder()
                .location(SimpleLocationRequest.builder().latitude(48.2).longitude(16.3).build())
                .offsetMs(0L).durationMs(1L).radius(10.0).build();
        when(request.getFences()).thenReturn(List.of(fence));
        Map<String, String> headers = Map.of(HeaderConstants.IP, "127.0.0.1");

        signalService.processSignalMeasurementResult(request, httpServletRequest, headers);

        // No accuracy/provider on the fence -> stored as NULL (no invented default).
        var expectedTime = TestConstants.DEFAULT_ZONED_DATE_TIME.plus(0L, ChronoUnit.MILLIS);
        verify(geoLocationService).createAndAssignGeoLocation(test, 48.2, 16.3, null, null, expectedTime);
    }

    @Test
    public void getLongSettingOrDefault_whenSettingPresent_returnsParsedValue() {
        Settings setting = mock(Settings.class);
        when(setting.getValue()).thenReturn("5000");
        when(settingsRepository.findFirstByKeyAndLangIsNullOrKeyAndLangOrderByLang(
                "max_coverage_session_seconds", "max_coverage_session_seconds", null))
                .thenReturn(Optional.of(setting));

        long value = ((SignalServiceImpl) signalService)
                .getLongSettingOrDefault("max_coverage_session_seconds", 99L);

        assertEquals(5000L, value);
    }

    @Test
    public void getLongSettingOrDefault_whenSettingAbsent_returnsDefault() {
        when(settingsRepository.findFirstByKeyAndLangIsNullOrKeyAndLangOrderByLang(
                "missing_key", "missing_key", null))
                .thenReturn(Optional.empty());

        long value = ((SignalServiceImpl) signalService)
                .getLongSettingOrDefault("missing_key", 99L);

        assertEquals(99L, value);
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
}
