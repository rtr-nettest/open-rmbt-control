package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.NetworkTypeRepository;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.request.RadioCellRequest;
import at.rtr.rmbt.request.RadioInfoRequest;
import at.rtr.rmbt.request.RadioSignalRequest;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ResultServiceImplTest {
    private ResultService resultService;

    @MockBean
    private TestRepository testRepository;
    @MockBean
    private GeoLocationService geoLocationService;
    @MockBean
    private RadioCellService radioCellService;
    @MockBean
    private RadioSignalService radioSignalService;
    @MockBean
    private CellLocationService cellLocationService;
    @MockBean
    private SignalService signalService;
    @MockBean
    private NetworkTypeRepository networkTypeRepository;
    @MockBean
    private PingService pingService;
    @MockBean
    private SpeedService speedService;
    @MockBean
    private TestMapper testMapper;

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private ResultRequest resultRequest;
    @Mock
    private at.rtr.rmbt.model.Test test;

    private final ApplicationProperties applicationProperties = new ApplicationProperties(
            new ApplicationProperties.LanguageProperties(Set.of("en", "de"), "en"),
            Set.of("RMBT", "RMBTjs", "Open-RMBT", "RMBTws", "HW-PROBE"),
            "0.1.0 || 0.3.0 || ^1.0.0",
            1,
            2,
            3,
            10000,
            2000
    );
    private final Map<String, String> headers = new HashMap<>();
    @Mock
    private RadioInfoRequest radioInfoRequest;
    @Mock
    private RadioSignalRequest radioSignalRequest;
    @Mock
    private RadioCellRequest radioCellRequest;

    @Before
    public void setUp() {
        resultService = new ResultServiceImpl(testRepository, geoLocationService, radioCellService,
                radioSignalService, cellLocationService, signalService, networkTypeRepository,
                pingService, speedService, applicationProperties, testMapper);
    }

    @Test
    public void processResultRequest_whenCommonRequest_expectTestSaved() {
        defaultMock();
        when(resultRequest.getTestIpLocal()).thenReturn(TestConstants.DEFAULT_IP_V4);
        when(resultRequest.getTestIpServer()).thenReturn(TestConstants.DEFAULT_IP_V4);
        when(resultRequest.getTestStatus()).thenReturn("SUCCESS");
        when(test.getNetworkType()).thenReturn(TestConstants.DEFAULT_NETWORK_TYPE_ID);
        when(test.getClientPublicIp()).thenReturn(TestConstants.DEFAULT_IP_V4);
        when(resultRequest.getRadioInfo()).thenReturn(radioInfoRequest);
        when(radioInfoRequest.getSignals()).thenReturn(List.of(radioSignalRequest));
        when(radioInfoRequest.getCells()).thenReturn(List.of(radioCellRequest));

        resultService.processResultRequest(httpServletRequest, resultRequest, headers);

        verify(testRepository).save(test);
        verify(testMapper).updateTestWithResultRequest(resultRequest, test);
        verify(radioCellService).processRadioCellRequests(List.of(radioCellRequest), test);
        verify(radioSignalService).saveRadioSignalRequests(List.of(radioSignalRequest), test);
    }

    @Test
    public void processResultRequest_whenRadioCellRequestNull_expectTestSaved() {
        defaultMock();
        when(resultRequest.getRadioInfo()).thenReturn(radioInfoRequest);
        when(radioInfoRequest.getSignals()).thenReturn(List.of(radioSignalRequest));
        when(radioInfoRequest.getCells()).thenReturn(null);

        resultService.processResultRequest(httpServletRequest, resultRequest, headers);

        verify(testRepository).save(test);
        verify(testMapper).updateTestWithResultRequest(resultRequest, test);
        verifyNoInteractions(radioCellService);
        verify(radioSignalService).saveRadioSignalRequests(List.of(radioSignalRequest), test);
    }

    @Test
    public void processResultRequest_whenRadioSignalRequestNull_expectTestSaved() {
        defaultMock();
        when(resultRequest.getRadioInfo()).thenReturn(radioInfoRequest);
        when(radioInfoRequest.getSignals()).thenReturn(null);
        when(radioInfoRequest.getCells()).thenReturn(List.of(radioCellRequest));

        resultService.processResultRequest(httpServletRequest, resultRequest, headers);

        verify(testRepository).save(test);
        verify(testMapper).updateTestWithResultRequest(resultRequest, test);
        verify(radioCellService).processRadioCellRequests(List.of(radioCellRequest), test);
        verifyNoInteractions(radioSignalService);
    }


    private void defaultMock() {
        when(resultRequest.getTestToken()).thenReturn(TestConstants.DEFAULT_TEST_TOKEN);
        when(testRepository.findByUuidOrOpenTestUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(resultRequest.getClientVersion()).thenReturn(TestConstants.DEFAULT_CLIENT_VERSION);
        when(resultRequest.getClientName()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE);
        when(httpServletRequest.getLocalAddr()).thenReturn(TestConstants.DEFAULT_IP_V4);
        when(resultRequest.getDownloadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        when(resultRequest.getUploadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        when(resultRequest.getPingShortest()).thenReturn(TestConstants.DEFAULT_RESULT_PING_SHORTEST);
    }
}
