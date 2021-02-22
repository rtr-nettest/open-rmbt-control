package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.HeaderConstants;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.mapper.RadioCellMapper;
import at.rtr.rmbt.mapper.RadioSignalMapper;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.repository.*;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.SignalMeasurementResponse;
import at.rtr.rmbt.response.SignalSettingsResponse;
import at.rtr.rmbt.service.SignalService;
import at.rtr.rmbt.utils.HelperFunctions;
import com.google.common.net.InetAddresses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static at.rtr.rmbt.constant.URIConstants.SIGNAL_RESULT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SignalServiceImplTest {
    private SignalService signalService;

    @MockBean
    private TestRepository testRepository;
    @MockBean
    private RTRProviderRepository providerRepository;
    @MockBean
    private UUIDGenerator uuidGenerator;
    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private SignalMapper signalMapper;
    @MockBean
    private RadioCellRepository radioCellRepository;
    @MockBean
    private RadioSignalRepository radioSignalRepository;
    @MockBean
    private GeoLocationRepository geoLocationRepository;
    @MockBean
    private GeoLocationMapper geoLocationMapper;
    @MockBean
    private RadioCellMapper radioCellMapper;
    @MockBean
    private RadioSignalMapper radioSignalMapper;

    @Mock
    private SignalRequest signalRequest;
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

    @Before
    public void setUp() {
        signalService = new SignalServiceImpl(testRepository, providerRepository,
                uuidGenerator, clientRepository, signalMapper, radioCellRepository,
                radioSignalRepository, geoLocationRepository, geoLocationMapper, radioCellMapper, radioSignalMapper);
    }

    @Test
    public void registerSignal_whenCommonRequest_expectSignalResponse() {
        var expectedResponse = getRegisterSignalResponse();
        when(httpServletRequest.getRemoteAddr()).thenReturn(TestConstants.DEFAULT_IP);
        when(httpServletRequest.getHeader(HeaderConstants.URL)).thenReturn(TestConstants.DEFAULT_URL);
        when(signalRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(providerRepository.getProviderNameByTestId(TestConstants.DEFAULT_UID)).thenReturn(TestConstants.DEFAULT_PROVIDER);
        when(testRepository.save(any())).thenReturn(savedTest);
        when(savedTest.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(savedTest.getUuid()).thenReturn(TestConstants.DEFAULT_UUID);

        var actualResponse = signalService.registerSignal(signalRequest, httpServletRequest);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getSignalsHistory_correctInvocation_expectPageWithResponse() {
        when(testRepository.findAllByStatusIn(eq(Collections.singletonList(TestStatus.SIGNAL_STARTED)), eq(pageable)))
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
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getLastSequenceNumber()).thenReturn(1);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));

        var response = signalService.processSignalResult(signalResultRequest);

        verify(test).setLastSequenceNumber(2);
        verify(testRepository).save(test);
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
    }

    @Test
    public void processSignalResult_whenTestNotExist_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(0L);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        when(uuidGenerator.generateUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);

        var response = signalService.processSignalResult(signalResultRequest);

        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
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
        when(radioCellMapper.radioCellRequestToRadioCell(radioCellRequest)).thenReturn(radioCell);
        when(radioSignalMapper.radioSignalRequestToRadioSignal(radioSignalRequest)).thenReturn(radioSignal);
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getLastSequenceNumber()).thenReturn(1);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_UUID);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));

        var response = signalService.processSignalResult(signalResultRequest);

        verify(test).setLastSequenceNumber(2);
        verify(radioCell).setOpenTestUUID(TestConstants.DEFAULT_UUID);
        verify(radioSignal).setOpenTestUUID(TestConstants.DEFAULT_UUID);
        verify(testRepository).save(test);
        verify(radioCellRepository).saveAll(List.of(radioCell));
        verify(radioSignalRepository).saveAll(List.of(radioSignal));
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
    }

    @Test
    public void processSignalResult_whenTestExistAndGeoLocation_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(2L);
        when(signalResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(signalResultRequest.getGeoLocations()).thenReturn(List.of(geoLocationRequestFirst, geoLocationRequestSecond));
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getLastSequenceNumber()).thenReturn(1);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_UUID);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
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
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst)).thenReturn(geoLocationFirst);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestSecond)).thenReturn(geoLocationSecond);

        var response = signalService.processSignalResult(signalResultRequest);

        verify(geoLocationFirst).setOpenTestUUID(TestConstants.DEFAULT_UUID);
        verify(geoLocationSecond).setOpenTestUUID(TestConstants.DEFAULT_UUID);
        verify(test).setLastSequenceNumber(2);
        verify(test).setGeoLocationUuid(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        verify(test).setGeoProvider(TestConstants.DEFAULT_PROVIDER);
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_FIRST);
        verify(test).setLongitude(TestConstants.DEFAULT_LONGITUDE);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE);
        verify(testRepository).save(test);
        verify(geoLocationRepository).saveAll(List.of(geoLocationFirst, geoLocationSecond));
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
    }

    @Test
    public void processSignalResult_whenTestExistAndIpAddress_expectSignalResultResponse() {
        when(signalResultRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(signalResultRequest.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(signalResultRequest.getSequenceNumber()).thenReturn(2L);
        when(signalResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(signalResultRequest.getTestIpLocal()).thenReturn(TestConstants.DEFAULT_IP);
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.SIGNAL_RESULT_STATUSES))
                .thenReturn(Optional.of(test));
        when(test.getLastSequenceNumber()).thenReturn(1);
        when(test.getClientPublicIp()).thenReturn(TestConstants.DEFAULT_IP);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));
        InetAddress defaultIpLocalAddress = InetAddresses.forString(TestConstants.DEFAULT_IP);
        InetAddress defaultIpPublicAddress = InetAddresses.forString(TestConstants.DEFAULT_IP);

        var response = signalService.processSignalResult(signalResultRequest);

        verify(test).setLastSequenceNumber(2);
        verify(test).setClientIpLocal(InetAddresses.toAddrString(defaultIpLocalAddress));
        verify(test).setClientIpLocalAnonymized(HelperFunctions.anonymizeIp(defaultIpLocalAddress));
        verify(test).setClientIpLocalType(HelperFunctions.IpType(defaultIpLocalAddress));
        verify(test).setNatType(HelperFunctions.getNatType(defaultIpLocalAddress, defaultIpPublicAddress));
        verify(testRepository).save(test);
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
    }

    private SignalSettingsResponse getRegisterSignalResponse() {
        return SignalSettingsResponse.builder()
                .resultUrl(String.join(TestConstants.DEFAULT_URL, SIGNAL_RESULT))
                .clientRemoteIp(TestConstants.DEFAULT_IP)
                .provider(TestConstants.DEFAULT_PROVIDER)
                .testUUID(TestConstants.DEFAULT_UUID)
                .build();
    }
}
