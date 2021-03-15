package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.NetworkTypeRepository;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
            3
    );

    @Before
    public void setUp() {
        resultService = new ResultServiceImpl(testRepository, geoLocationService, radioCellService,
                radioSignalService, cellLocationService, signalService, networkTypeRepository,
                pingService, speedService, applicationProperties, testMapper);
    }

    @Test
    public void processResultRequest_whenCommonRequest_expectTestSaved() {
        when(resultRequest.getTestToken()).thenReturn(TestConstants.DEFAULT_TEST_TOKEN);
        when(testRepository.findByUuidOrOpenTestUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(resultRequest.getClientVersion()).thenReturn(TestConstants.DEFAULT_CLIENT_VERSION);
        when(resultRequest.getClientName()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE);
        when(resultRequest.getDownloadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        when(resultRequest.getUploadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        when(resultRequest.getPingShortest()).thenReturn(TestConstants.DEFAULT_RESULT_PING_SHORTEST);
        when(httpServletRequest.getRemoteAddr()).thenReturn(TestConstants.DEFAULT_IP);
        when(test.getNetworkType()).thenReturn(TestConstants.DEFAULT_NETWORK_TYPE_ID);

        resultService.processResultRequest(httpServletRequest, resultRequest);

        verify(testRepository).save(test);
        verify(testMapper).updateTestWithResultRequest(resultRequest, test);
    }
}
