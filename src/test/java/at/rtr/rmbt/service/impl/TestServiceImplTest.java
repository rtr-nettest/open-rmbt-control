package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.dto.LteFrequencyDto;
import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.dto.TestDistance;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.exception.ClientNotFoundException;
import at.rtr.rmbt.exception.TestNotFoundException;
import at.rtr.rmbt.mapper.TestHistoryMapper;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.ClientRepository;
import at.rtr.rmbt.repository.SettingsRepository;
import at.rtr.rmbt.repository.TestHistoryRepository;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.GeoAnalyticsService;
import at.rtr.rmbt.service.GeoLocationService;
import at.rtr.rmbt.service.QoeClassificationService;
import at.rtr.rmbt.service.TestService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class TestServiceImplTest {
    private final static int GEO_FIELDS_COUNT = 24;
    private final static int DEFAULT_TEST_FIELDS_COUNT = 38;
    private final static int NDT_TEST_FIELDS_COUNT = 7;
    private final static int BAND_TEST_FIELDS_COUNT = 4;
    private final static int TIME_TEST_FIELDS_COUNT = 4;
    private TestService testService;
    private MessageSource messageSource;

    @MockBean
    private TestRepository testRepository;
    @MockBean
    private TestMapper testMapper;
    @MockBean
    private GeoAnalyticsService geoAnalyticsService;
    @MockBean
    private SettingsRepository settingsRepository;
    @MockBean
    private QoeClassificationService qoeClassificationService;
    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private TestHistoryRepository testHistoryRepository;
    @MockBean
    private TestHistoryMapper testHistoryMapper;
    @MockBean
    private GeoLocationService geoLocationService;
    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private TestResponse testResponse;
    @Mock
    private TestResultDetailRequest testResultDetailRequest;
    @Mock
    private TestResultRequest testResultRequest;
    @Mock
    private CapabilitiesRequest capabilitiesRequest;
    @Mock
    private ClassificationRequest classificationRequest;
    @Mock
    private QoeClassificationThresholds qoeClassificationThresholds;
    @Mock
    private HistoryRequest historyRequest;
    @Mock
    private HistoryItemResponse historyItemResponse;
    @Mock
    private TestHistory testHistory;
    @Mock
    private RtrClient client;
    @Mock
    private ResultUpdateRequest resultUpdateRequest;
    @Mock
    private ImplausibleRequest implausibleRequest;

    private final ApplicationProperties applicationProperties = new ApplicationProperties(
            new ApplicationProperties.LanguageProperties(Set.of("en", "de"), "en"),
            Set.of("RMBT", "RMBTjs", "Open-RMBT", "RMBTws", "HW-PROBE"),
            "1.2",
            1,
            2,
            3,
            10000,
            2000
    );

    @Before
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath:SystemMessages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        messageSource = reloadableResourceBundleMessageSource;
        testService = new TestServiceImpl(testRepository, testMapper, applicationProperties, geoAnalyticsService,
                messageSource, settingsRepository, qoeClassificationService, clientRepository, testHistoryRepository,
                testHistoryMapper, geoLocationService);
    }

    @Test
    public void getDeviceHistory_whenCommonData_expectDevicesNameList() {
        var devices = Lists.newArrayList(TestConstants.DEFAULT_HISTORY_DEVICE);
        List<Long> uidList = List.of(TestConstants.DEFAULT_UID);

        when(testRepository.getDistinctModelByClientIdIn(uidList)).thenReturn(devices);

        var response = testService.getDeviceHistory(uidList);

        assertEquals(devices, response);
    }

    @Test
    public void getDeviceHistory_whenUnknownDevice_expectDevicesNameList() {
        List<String> devices = new ArrayList<>();
        devices.add(null);
        List<Long> uidList = List.of(TestConstants.DEFAULT_UID);

        when(testRepository.getDistinctModelByClientIdIn(uidList)).thenReturn(devices);

        var response = testService.getDeviceHistory(uidList);

        assertEquals(List.of(Constants.UNKNOWN_DEVICE), response);
    }

    @Test
    public void getGroupNameByClientId_whenCommonData_expectGroupNameList() {
        List<Long> uidList = List.of(TestConstants.DEFAULT_UID);
        when(testRepository.getDistinctGroupNameByClientIdIn(uidList)).thenReturn(List.of(TestConstants.DEFAULT_HISTORY_NETWORK));

        var response = testService.getGroupNameByClientIds(uidList);

        assertEquals(List.of(TestConstants.DEFAULT_HISTORY_NETWORK), response);
    }

    @Test
    public void getTestByUUID_whenTestExist_expectTestResponse() {
        when(testRepository.findByUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(testMapper.testToTestResponse(test)).thenReturn(testResponse);

        var response = testService.getTestByUUID(TestConstants.DEFAULT_TEST_UUID);

        assertEquals(testResponse, response);
    }

    @Test(expected = TestNotFoundException.class)
    public void getTestByUUID_whenTestNotExist_expectException() {
        when(testRepository.findByUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.empty());
        testService.getTestByUUID(TestConstants.DEFAULT_TEST_UUID);
    }

    @Test
    public void getTestResultDetailByTestUUID_whenTestExistAndTime_expectTestResultDetailResponse() {
        at.rtr.rmbt.model.Test test = getTimeTest();
        when(testResultDetailRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(testResultDetailRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testRepository.findByUuidAndStatusesInAndActive(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_DETAIL_STATUSES))
                .thenReturn(Optional.of(test));

        var result = testService.getTestResultDetailByTestUUID(testResultDetailRequest);

        assertEquals(TIME_TEST_FIELDS_COUNT, result.getTestResultDetailContainerResponse().size());
        for (int i = 0; i < TIME_TEST_FIELDS_COUNT; i++) {
            assertEquals(getTimeTestResultDetailResponse().getTestResultDetailContainerResponse().get(i), result.getTestResultDetailContainerResponse().get(i));
        }
        assertEquals(getTimeTestResultDetailResponse(), result);
    }

    @Test
    public void getTestResultDetailByTestUUID_whenTestExistAndDualSimFalse_expectTestResultDetailResponse() {
        at.rtr.rmbt.model.Test test = getDualSimTest();
        when(testResultDetailRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(testResultDetailRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testRepository.findByUuidAndStatusesInAndActive(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_DETAIL_STATUSES))
                .thenReturn(Optional.of(test));

        var result = testService.getTestResultDetailByTestUUID(testResultDetailRequest);

        assertEquals(getDualSimResult(), result);
    }

    @Test
    public void getTestResultDetailByTestUUID_whenTestExist_expectTestResultDetailResponse() {
        at.rtr.rmbt.model.Test test = getDefaultTest();
        when(testResultDetailRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(testResultDetailRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testRepository.findByUuidAndStatusesInAndActive(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_DETAIL_STATUSES))
                .thenReturn(Optional.of(test));

        var result = testService.getTestResultDetailByTestUUID(testResultDetailRequest);

        assertEquals(DEFAULT_TEST_FIELDS_COUNT, result.getTestResultDetailContainerResponse().size());
        for (int i = 0; i < DEFAULT_TEST_FIELDS_COUNT; i++) {
            assertEquals(getDefaultTestResultResponse().getTestResultDetailContainerResponse().get(i), result.getTestResultDetailContainerResponse().get(i));
        }
        assertEquals(getDefaultTestResultResponse(), result);
    }

    @Test
    public void getTestResultDetailByTestUUID_whenTestExistAndGeoLocation_expectTestResultDetailResponse() {
        at.rtr.rmbt.model.Test test = getGeoTest();
        when(testResultDetailRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(testResultDetailRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testRepository.findByUuidAndStatusesInAndActive(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_DETAIL_STATUSES))
                .thenReturn(Optional.of(test));
        when(geoAnalyticsService.getTestDistance(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID)).thenReturn(getTestDistance());
        var result = testService.getTestResultDetailByTestUUID(testResultDetailRequest);

        assertEquals(GEO_FIELDS_COUNT, result.getTestResultDetailContainerResponse().size());
        for (int i = 0; i < GEO_FIELDS_COUNT; i++) {
            assertEquals(getGeoLocationTestResultResponse().getTestResultDetailContainerResponse().get(i), result.getTestResultDetailContainerResponse().get(i));
        }
        assertEquals(getGeoLocationTestResultResponse(), result);
    }

    @Test
    public void getTestResultDetailByTestUUID_whenTestExistAndNdt_expectTestResultDetailResponse() {
        at.rtr.rmbt.model.Test test = getNdtTest();
        when(testResultDetailRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(testResultDetailRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testRepository.findByUuidAndStatusesInAndActive(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_DETAIL_STATUSES))
                .thenReturn(Optional.of(test));

        var result = testService.getTestResultDetailByTestUUID(testResultDetailRequest);

        assertEquals(NDT_TEST_FIELDS_COUNT, result.getTestResultDetailContainerResponse().size());
        for (int i = 0; i < NDT_TEST_FIELDS_COUNT; i++) {
            assertEquals(getNdtTestResultDetailResponse().getTestResultDetailContainerResponse().get(i), result.getTestResultDetailContainerResponse().get(i));
        }
        assertEquals(getNdtTestResultDetailResponse(), result);
    }

    @Test
    public void getTestResultDetailByTestUUID_whenTestExistAndBand_expectTestResultDetailResponse() {
        at.rtr.rmbt.model.Test test = getBandTest();
        when(testResultDetailRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(testResultDetailRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testRepository.findByUuidAndStatusesInAndActive(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_DETAIL_STATUSES))
                .thenReturn(Optional.of(test));
        when(testRepository.findLteFrequencyByOpenTestUUID(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID)).thenReturn(List.of(getLteFrequencyDto()));
        var result = testService.getTestResultDetailByTestUUID(testResultDetailRequest);

        assertEquals(BAND_TEST_FIELDS_COUNT, result.getTestResultDetailContainerResponse().size());
        for (int i = 0; i < BAND_TEST_FIELDS_COUNT; i++) {
            assertEquals(getBandTestResultDetailResponse().getTestResultDetailContainerResponse().get(i), result.getTestResultDetailContainerResponse().get(i));
        }
        assertEquals(getBandTestResultDetailResponse(), result);
    }

    @Test
    public void getTestResult_whenSignalStrengthNotNullAndUseSignalTrueAndDualSimTrue_expectTestResultContainerResponse() {
        when(testResultRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(testResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testResultRequest.getCapabilitiesRequest()).thenReturn(capabilitiesRequest);
        when(capabilitiesRequest.getClassification()).thenReturn(classificationRequest);
        when(classificationRequest.getCount()).thenReturn(TestConstants.DEFAULT_CLASSIFICATION_COUNT);
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_STATUSES)).thenReturn(Optional.of(test));
        when(test.getDualSim()).thenReturn(Boolean.TRUE);
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID);
        when(test.getSimCount()).thenReturn(TestConstants.DEFAULT_TEST_SIM_COUNT);
        when(test.getDownloadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        when(test.getUploadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_UPLOAD_SPEED);
        when(test.getPingMedian()).thenReturn(TestConstants.DEFAULT_TEST_PING_MEDIAN);
        when(test.getSignalStrength()).thenReturn(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST);
        when(test.getLteRsrp()).thenReturn(null);

        var result = testService.getTestResult(testResultRequest);
        assertEquals(1, result.getTestResultResponses().size());
        var testResultResponse = result.getTestResultResponses().get(0);
        assertEquals(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID, testResultResponse.getOpenTestUUID());
        assertEquals(TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME_STRING, testResultResponse.getTimeString());
        assertEquals(TestConstants.DEFAULT_TEST_RESULT_RESPONSE_SHARE_SUBJECT, testResultResponse.getShareSubject());
        assertEquals(TestConstants.DEFAULT_TEST_RESULT_RESPONSE_SHARE_TEXT_DUAL_SIM_TRUE_SIGNAL_STRENGTH_NOT_NULL, testResultResponse.getShareText());
        assertEquals(getMeasurementIfSignalStrengthNotNull(), testResultResponse.getMeasurement());
    }

    @Test
    public void getTestResult_whenLteRSRPNotNullAndUseSignalTrueAndDualSimFalse_expectTestResultContainerResponse() {
        when(testResultRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(testResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testResultRequest.getCapabilitiesRequest()).thenReturn(capabilitiesRequest);
        when(capabilitiesRequest.getClassification()).thenReturn(classificationRequest);
        when(classificationRequest.getCount()).thenReturn(TestConstants.DEFAULT_CLASSIFICATION_COUNT);
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_STATUSES)).thenReturn(Optional.of(test));
        when(test.getDualSim()).thenReturn(Boolean.FALSE);
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID);
        when(test.getSimCount()).thenReturn(TestConstants.DEFAULT_TEST_SIM_COUNT);
        when(test.getDownloadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        when(test.getUploadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_UPLOAD_SPEED);
        when(test.getPingMedian()).thenReturn(TestConstants.DEFAULT_TEST_PING_MEDIAN);
        when(test.getSignalStrength()).thenReturn(null);
        when(test.getLteRsrp()).thenReturn(TestConstants.DEFAULT_LTE_RSRP_FIRST);
        when(test.getNetworkOperatorName()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME);
        when(test.getRoamingType()).thenReturn(TestConstants.DEFAULT_ROAMING_TYPE_ID);

        var result = testService.getTestResult(testResultRequest);
        assertEquals(1, result.getTestResultResponses().size());
        var testResultResponse = result.getTestResultResponses().get(0);
        assertEquals(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID, testResultResponse.getOpenTestUUID());
        assertEquals(TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME_STRING, testResultResponse.getTimeString());
        assertEquals(TestConstants.DEFAULT_TEST_RESULT_RESPONSE_SHARE_SUBJECT, testResultResponse.getShareSubject());
        assertEquals(TestConstants.DEFAULT_TEST_RESULT_RESPONSE_SHARE_TEXT_DUAL_SIM_FALSE_LTE_RSRP_NOT_NULL, testResultResponse.getShareText());
        assertEquals(getMeasurementIfLteRSRPNotNull(), testResultResponse.getMeasurement());
    }

    @Test
    public void getTestResult_whenDualSimFalseAndBrowser_expectTestResultContainerResponse() {
        when(testResultRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(testResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testResultRequest.getCapabilitiesRequest()).thenReturn(capabilitiesRequest);
        when(capabilitiesRequest.getClassification()).thenReturn(classificationRequest);
        when(classificationRequest.getCount()).thenReturn(TestConstants.DEFAULT_CLASSIFICATION_COUNT);
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_STATUSES)).thenReturn(Optional.of(test));
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(test.getDualSim()).thenReturn(Boolean.TRUE);
        when(test.getNetworkType()).thenReturn(TestConstants.DEFAULT_NETWORK_TYPE_WLAN_ID);
        when(test.getSimCount()).thenReturn(null);
        when(test.getWifiSsid()).thenReturn(TestConstants.DEFAULT_WIFI_SSID);
        when(test.getProvider()).thenReturn(getProvider());

        var result = testService.getTestResult(testResultRequest);

        assertEquals(1, result.getTestResultResponses().size());
        var testResultResponse = result.getTestResultResponses().get(0);
        assertEquals(TestConstants.DEFAULT_NETWORK_TYPE_WLAN_VALUE, testResultResponse.getNetworkInfoResponse().getNetworkTypeLabel());
        assertEquals(TestConstants.DEFAULT_WIFI_SSID, testResultResponse.getNetworkInfoResponse().getWifiSSID());
        assertEquals(TestConstants.DEFAULT_TEST_PROVIDER_NAME, testResultResponse.getNetworkInfoResponse().getProviderName());
        assertEquals(3, testResultResponse.getNetItemResponses().size());
        assertEquals(getNetItemResponses(TestConstants.DEFAULT_NETWORK_TYPE_WLAN_VALUE, TestConstants.DEFAULT_NET_ITEM_RESPONSE_NETWORK_TYPE_TITLE), testResultResponse.getNetItemResponses().get(0));
        assertEquals(getNetItemResponses(TestConstants.DEFAULT_TEST_PROVIDER_NAME, TestConstants.DEFAULT_NET_ITEM_RESPONSE_OPERATOR_NAME_TITLE), testResultResponse.getNetItemResponses().get(1));
        assertEquals(getNetItemResponses(TestConstants.DEFAULT_WIFI_SSID, TestConstants.DEFAULT_NET_ITEM_RESPONSE_WIFI_SSID_TITLE), testResultResponse.getNetItemResponses().get(2));


    }

    @Test
    public void getTestResult_whenDualSimFalseAndNotBrowserAndNotWLAN_expectTestResultContainerResponse() {
        when(testResultRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(testResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testResultRequest.getCapabilitiesRequest()).thenReturn(capabilitiesRequest);
        when(capabilitiesRequest.getClassification()).thenReturn(classificationRequest);
        when(classificationRequest.getCount()).thenReturn(TestConstants.DEFAULT_CLASSIFICATION_COUNT);
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_STATUSES)).thenReturn(Optional.of(test));
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(test.getDualSim()).thenReturn(Boolean.FALSE);
        when(test.getNetworkOperatorName()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME);
        when(test.getRoamingType()).thenReturn(TestConstants.DEFAULT_ROAMING_TYPE_ID);

        var result = testService.getTestResult(testResultRequest);

        assertEquals(1, result.getTestResultResponses().size());
        var testResultResponse = result.getTestResultResponses().get(0);
        assertEquals(TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME, testResultResponse.getNetworkInfoResponse().getProviderName());
        assertEquals(TestConstants.DEFAULT_ROAMING_TYPE_VALUE, testResultResponse.getNetworkInfoResponse().getRoamingTypeLabel());
        assertEquals(3, testResultResponse.getNetItemResponses().size());
        assertEquals(getNetItemResponses(TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME, TestConstants.DEFAULT_NET_ITEM_RESPONSE_OPERATOR_NAME_TITLE), testResultResponse.getNetItemResponses().get(1));
        assertEquals(getNetItemResponses(TestConstants.DEFAULT_ROAMING_TYPE_VALUE, TestConstants.DEFAULT_NET_ITEM_RESPONSE_ROAMING_TITLE), testResultResponse.getNetItemResponses().get(2));

    }

    @Test
    public void getTestResult_whenQoeClassificaitonNotNull_expectTestResultContainerResponse() {
        when(testResultRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(testResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testResultRequest.getCapabilitiesRequest()).thenReturn(capabilitiesRequest);
        when(capabilitiesRequest.getClassification()).thenReturn(classificationRequest);
        when(classificationRequest.getCount()).thenReturn(TestConstants.DEFAULT_CLASSIFICATION_COUNT);
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_STATUSES)).thenReturn(Optional.of(test));
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(test.getPingMedian()).thenReturn(TestConstants.DEFAULT_TEST_PING_MEDIAN);
        when(test.getDownloadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        when(test.getUploadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_UPLOAD_SPEED);
        when(qoeClassificationService.getQoeClassificationThreshold()).thenReturn(List.of(qoeClassificationThresholds));
        when(qoeClassificationThresholds.getQoeCategory()).thenReturn(TestConstants.DEFAULT_QOE_CATEGORY);

        var result = testService.getTestResult(testResultRequest);

        assertEquals(1, result.getTestResultResponses().size());
        var testResultResponse = result.getTestResultResponses().get(0);
        assertEquals(1, testResultResponse.getQoeClassificationResponses().size());
        var qoeClassificationResponse = testResultResponse.getQoeClassificationResponses().get(0);
        assertEquals(TestConstants.DEFAULT_QOE_CATEGORY.getValue(), qoeClassificationResponse.getCategory());
        assertEquals(TestConstants.DEFAULT_QOE_CLASSIFICATION, qoeClassificationResponse.getClassification());
        assertEquals(TestConstants.DEFAULT_QUALITY, qoeClassificationResponse.getQuality());
    }

    @Test
    public void getTestResult_whenDEFAULT_expectTestResultContainerResponse() {
        when(testResultRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(testResultRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(testResultRequest.getCapabilitiesRequest()).thenReturn(capabilitiesRequest);
        when(capabilitiesRequest.getClassification()).thenReturn(classificationRequest);
        when(classificationRequest.getCount()).thenReturn(TestConstants.DEFAULT_CLASSIFICATION_COUNT);
        when(testRepository.findByUuidAndStatusesIn(TestConstants.DEFAULT_TEST_UUID, Config.TEST_RESULT_STATUSES)).thenReturn(Optional.of(test));
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);

        var result = testService.getTestResult(testResultRequest);

        assertEquals(1, result.getTestResultResponses().size());
    }

    @Test
    public void save_whenCommonData_expectSaved() {
        testService.save(test);

        verify(testRepository).save(test);
    }

    @Test
    public void getRmbtSetProviderFromAs_whenCommonData_expectGetRmbtSetProviderFromAsCalled() {
        when(testRepository.getRmbtNextTestSlot(TestConstants.DEFAULT_UID)).thenReturn(TestConstants.DEFAULT_NEXT_TEST_SLOT);

        var response = testService.getRmbtNextTestSlot(TestConstants.DEFAULT_UID);
        assertEquals(TestConstants.DEFAULT_NEXT_TEST_SLOT, response);
    }

    @Test
    public void getRmbtNextTestSlot_whenCommonData_expectGetRmbtNextTestSlotCalled() {
        when(testRepository.getRmbtSetProviderFromAs(TestConstants.DEFAULT_UID)).thenReturn(TestConstants.DEFAULT_PROVIDER);

        var response = testService.getRmbtSetProviderFromAs(TestConstants.DEFAULT_UID);
        assertEquals(TestConstants.DEFAULT_PROVIDER, response);

    }

    @Test
    public void getHistory_whenCommonData_expectHistoryResponse() {
        when(historyRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(historyRequest.getClientUUID()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(historyRequest.getResultLimit()).thenReturn(TestConstants.DEFAULT_RESULT_LIMIT);
        when(historyRequest.getResultOffset()).thenReturn(TestConstants.DEFAULT_RESULT_OFFSET);
        when(historyRequest.getDevices()).thenReturn(List.of(TestConstants.DEFAULT_DEVICE));
        when(historyRequest.getNetworks()).thenReturn(List.of(TestConstants.DEFAULT_NETWORK_NAME));
        when(historyRequest.getCapabilities()).thenReturn(capabilitiesRequest);
        when(capabilitiesRequest.getClassification()).thenReturn(classificationRequest);
        when(classificationRequest.getCount()).thenReturn(TestConstants.DEFAULT_CLASSIFICATION_COUNT);
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(client));
        when(testHistoryRepository
                .getTestHistoryByDevicesAndNetworksAndClient(
                        TestConstants.DEFAULT_RESULT_LIMIT,
                        TestConstants.DEFAULT_RESULT_OFFSET,
                        List.of(TestConstants.DEFAULT_DEVICE),
                        List.of(TestConstants.DEFAULT_NETWORK_NAME),
                        client))
                .thenReturn(List.of(testHistory));
        when(testHistoryMapper.testHistoryToHistoryItemResponse(testHistory, TestConstants.DEFAULT_CLASSIFICATION_COUNT, Locale.ENGLISH)).thenReturn(historyItemResponse);

        var response = testService.getHistory(historyRequest);
        assertEquals(List.of(historyItemResponse), response.getHistory());
    }

    @Test(expected = ClientNotFoundException.class)
    public void getHistory_whenClientNotFound_expectException() {
        testService.getHistory(historyRequest);
    }

    @Test
    public void updateTestResult_whenCommonData_expectTestUpdated() {
        when(resultUpdateRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(resultUpdateRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(testRepository.findByUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(client));
        when(test.getClient()).thenReturn(client);

        testService.updateTestResult(resultUpdateRequest);

        verify(geoLocationService).updateGeoLocation(test, resultUpdateRequest);
        verify(testRepository).save(test);
    }

    @Test
    public void updateTestResult_whenTestIsAborted_expectTestUpdated() {
        when(resultUpdateRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(resultUpdateRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(resultUpdateRequest.isAborted()).thenReturn(true);
        when(testRepository.findByUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(client));
        when(test.getClient()).thenReturn(client);

        testService.updateTestResult(resultUpdateRequest);

        verify(test).setStatus(TestStatus.ABORTED);
        verifyNoInteractions(geoLocationService);
        verify(testRepository).save(test);
    }

    @Test(expected = TestNotFoundException.class)
    public void updateTestResult_whenTestNotFound_expectTestNotFoundException() {
        when(resultUpdateRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);

        testService.updateTestResult(resultUpdateRequest);
    }

    @Test(expected = ClientNotFoundException.class)
    public void updateTestResult_whenClientNotFound_expectClientNotFoundException() {
        when(resultUpdateRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(resultUpdateRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(testRepository.findByUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));

        testService.updateTestResult(resultUpdateRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateTestResult_whenTestClientNotMatchRequestClient_expectIllegalArgumentException() {
        when(resultUpdateRequest.getTestUUID()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(resultUpdateRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(testRepository.findByUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(client));

        testService.updateTestResult(resultUpdateRequest);
    }

    @Test
    public void setImplausible_whenPrefixP_expectUpdateImplausible() {
        String uuid = "P".concat(TestConstants.DEFAULT_UUID.toString());
        when(implausibleRequest.getUuid()).thenReturn(uuid);
        when(implausibleRequest.getComment()).thenReturn(TestConstants.DEFAULT_COMMENT);
        when(implausibleRequest.getImplausible()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testRepository.updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_P_UUID_FIELD, TestConstants.DEFAULT_UUID)).thenReturn(TestConstants.DEFAULT_AFFECTED_ROWS);

        var response = testService.setImplausible(implausibleRequest);

        verify(testRepository).updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_P_UUID_FIELD, TestConstants.DEFAULT_UUID);
        assertEquals(TestConstants.DEFAULT_AFFECTED_ROWS, response.getAffectedRows());
        assertEquals(Constants.STATUS_OK, response.getStatus());
    }

    @Test
    public void setImplausible_whenPrefixO_expectUpdateImplausible() {
        String uuid = "O".concat(TestConstants.DEFAULT_UUID.toString());
        when(implausibleRequest.getUuid()).thenReturn(uuid);
        when(implausibleRequest.getComment()).thenReturn(TestConstants.DEFAULT_COMMENT);
        when(implausibleRequest.getImplausible()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testRepository.updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_O_UUID_FIELD, TestConstants.DEFAULT_UUID)).thenReturn(TestConstants.DEFAULT_AFFECTED_ROWS);

        var response = testService.setImplausible(implausibleRequest);

        verify(testRepository).updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_O_UUID_FIELD, TestConstants.DEFAULT_UUID);
        assertEquals(TestConstants.DEFAULT_AFFECTED_ROWS, response.getAffectedRows());
        assertEquals(Constants.STATUS_OK, response.getStatus());
    }

    @Test
    public void setImplausible_whenPrefixT_expectUpdateImplausible() {
        String uuid = "T".concat(TestConstants.DEFAULT_UUID.toString());
        when(implausibleRequest.getUuid()).thenReturn(uuid);
        when(implausibleRequest.getComment()).thenReturn(TestConstants.DEFAULT_COMMENT);
        when(implausibleRequest.getImplausible()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testRepository.updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_T_UUID_FIELD, TestConstants.DEFAULT_UUID)).thenReturn(TestConstants.DEFAULT_AFFECTED_ROWS);

        var response = testService.setImplausible(implausibleRequest);

        verify(testRepository).updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_T_UUID_FIELD, TestConstants.DEFAULT_UUID);
        assertEquals(TestConstants.DEFAULT_AFFECTED_ROWS, response.getAffectedRows());
        assertEquals(Constants.STATUS_OK, response.getStatus());
    }

    @Test
    public void setImplausible_whenPrefixU_expectUpdateImplausible() {
        String uuid = "U".concat(TestConstants.DEFAULT_UUID.toString());
        when(implausibleRequest.getUuid()).thenReturn(uuid);
        when(implausibleRequest.getComment()).thenReturn(TestConstants.DEFAULT_COMMENT);
        when(implausibleRequest.getImplausible()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testRepository.updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_U_UUID_FIELD, TestConstants.DEFAULT_UUID)).thenReturn(TestConstants.DEFAULT_AFFECTED_ROWS);

        var response = testService.setImplausible(implausibleRequest);

        verify(testRepository).updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_U_UUID_FIELD, TestConstants.DEFAULT_UUID);
        assertEquals(TestConstants.DEFAULT_AFFECTED_ROWS, response.getAffectedRows());
        assertEquals(Constants.STATUS_OK, response.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setImplausible_whenPrefixQ_expectException() {
        String uuid = "Q".concat(TestConstants.DEFAULT_UUID.toString());
        when(implausibleRequest.getUuid()).thenReturn(uuid);
        when(implausibleRequest.getComment()).thenReturn(TestConstants.DEFAULT_COMMENT);
        when(implausibleRequest.getImplausible()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testRepository.updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_U_UUID_FIELD, TestConstants.DEFAULT_UUID)).thenReturn(TestConstants.DEFAULT_AFFECTED_ROWS);

        testService.setImplausible(implausibleRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setImplausible_whenEmptyComment_expectException() {
        String uuid = "Q".concat(TestConstants.DEFAULT_UUID.toString());
        when(implausibleRequest.getUuid()).thenReturn(uuid);
        when(implausibleRequest.getComment()).thenReturn(null);
        when(implausibleRequest.getImplausible()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(testRepository.updateImplausible(TestConstants.DEFAULT_FLAG_TRUE, TestConstants.DEFAULT_COMMENT_FORMATTED, TestConstants.DEFAULT_U_UUID_FIELD, TestConstants.DEFAULT_UUID)).thenReturn(TestConstants.DEFAULT_AFFECTED_ROWS);

        testService.setImplausible(implausibleRequest);
    }

    private NetItemResponse getNetItemResponses(String value, String title) {
        return NetItemResponse.builder()
                .title(title)
                .value(value)
                .build();
    }

    private List<TestResultMeasurementResponse> getMeasurementIfSignalStrengthNotNull() {
        var speedDownloadMeasurementResponse = getSpeedDownloadMeasurementResponse();
        var speedUploadMeasurementResponse = getSpeedUploadMeasurementResponse();
        var pingMeasurementResponse = getPingMeasurementResponse();
        var signalMeasurementResponse = getSignalStrengthMeasurementResponse();
        return new ArrayList<>(List.of(
                speedDownloadMeasurementResponse,
                speedUploadMeasurementResponse,
                pingMeasurementResponse,
                signalMeasurementResponse
        ));
    }

    private List<TestResultMeasurementResponse> getMeasurementIfLteRSRPNotNull() {
        var speedDownloadMeasurementResponse = getSpeedDownloadMeasurementResponse();
        var speedUploadMeasurementResponse = getSpeedUploadMeasurementResponse();
        var pingMeasurementResponse = getPingMeasurementResponse();
        var signalMeasurementResponse = getSignalLteRSRPMeasurementResponse();
        return new ArrayList<>(List.of(
                speedDownloadMeasurementResponse,
                speedUploadMeasurementResponse,
                pingMeasurementResponse,
                signalMeasurementResponse
        ));
    }

    private TestResultMeasurementResponse getSignalLteRSRPMeasurementResponse() {
        return TestResultMeasurementResponse.builder()
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_SIGNAL_RSRP_VALUE)
                .title(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_LTE_RSRP_TITLE)
                .classification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .build();
    }

    private TestResultMeasurementResponse getSignalStrengthMeasurementResponse() {
        return TestResultMeasurementResponse.builder()
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_SIGNAL_STRENGTH_VALUE)
                .title(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_STRENGTH_TITLE)
                .classification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_STRENGTH_CLASSIFICATION)
                .build();
    }

    private TestResultMeasurementResponse getPingMeasurementResponse() {
        return TestResultMeasurementResponse.builder()
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_PING_MEDIAN_VALUE)
                .title(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_PING_TITLE)
                .classification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .build();
    }

    private TestResultMeasurementResponse getSpeedUploadMeasurementResponse() {
        return TestResultMeasurementResponse.builder()
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_SPEED_UPLOAD_VALUE)
                .title(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_UPLOAD_TITLE)
                .classification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .build();
    }

    private TestResultMeasurementResponse getSpeedDownloadMeasurementResponse() {
        return TestResultMeasurementResponse.builder()
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_SPEED_DOWNLOAD_VALUE)
                .title(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_DOWNLOAD_TITLE)
                .classification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .build();
    }


    private at.rtr.rmbt.model.Test getTimeTest() {
        return getDefaultTestBuilder()
                .time(TestConstants.DEFAULT_ZONED_DATE_TIME)
                .timezone(TestConstants.DEFAULT_TIMEZONE)
                .build();
    }

    private LteFrequencyDto getLteFrequencyDto() {
        return LteFrequencyDto.builder()
                .channelNumber(TestConstants.DEFAULT_CHANNEL_NUMBER_FIRST)
                .technology(TestConstants.DEFAULT_TECHNOLOGY_FIRST)
                .build();
    }

    private TestResultDetailResponse getBandTestResultDetailResponse() {
        var properties = getDefaultProperties();
        properties.addAll(List.of(
                getTestResultDetailContainerResponse("key_frequency_dl", TestConstants.DEFAULT_TEST_RESULT_DETAIL_FREQUENCY_DL),
                getTestResultDetailContainerResponse("key_radio_band", TestConstants.DEFAULT_TEST_RESULT_DETAIL_RADIO_BAND)
        ));
        return TestResultDetailResponse.builder()
                .testResultDetailContainerResponse(properties)
                .build();
    }

    private at.rtr.rmbt.model.Test getBandTest() {
        return getDefaultTestBuilder()
                .radioBand(TestConstants.DEFAULT_BAND)
                .build();
    }

    private TestResultDetailResponse getNdtTestResultDetailResponse() {
        var properties = getDefaultProperties();
        properties.addAll(List.of(
                getTestResultDetailContainerResponse("key_speed_download_ndt", TestConstants.DEFAULT_TEST_RESULT_DETAIL_SPEED_DOWNLOAD_NDT),
                getTestResultDetailContainerResponse("key_speed_upload_ndt", TestConstants.DEFAULT_TEST_RESULT_DETAIL_SPEED_UPLOAD_NDT),
                getTestResultDetailContainerResponse("key_ndt_details_main", TestConstants.DEFAULT_TEST_NDT_MAIN),
                getTestResultDetailContainerResponse("key_ndt_details_stat", TestConstants.DEFAULT_TEST_NDT_STAT),
                getTestResultDetailContainerResponse("key_ndt_details_diag", TestConstants.DEFAULT_TEST_NDT_DIAG)
        ));
        return TestResultDetailResponse.builder()
                .testResultDetailContainerResponse(properties)
                .build();
    }

    private at.rtr.rmbt.model.Test getNdtTest() {
        var testNdt = getTestNdt();
        return getDefaultTestBuilder()
                .testNdt(testNdt)
                .build();
    }

    private TestNdt getTestNdt() {
        return TestNdt.builder()
                .s2cspd(TestConstants.DEFAULT_TEST_NDT_S2CSPD)
                .c2sspd(TestConstants.DEFAULT_TEST_NDT_C2SSPD)
                .main(TestConstants.DEFAULT_TEST_NDT_MAIN)
                .stat(TestConstants.DEFAULT_TEST_NDT_STAT)
                .diag(TestConstants.DEFAULT_TEST_NDT_DIAG)
                .build();
    }

    private at.rtr.rmbt.model.Test getDualSimTest() {
        return getDefaultTestBuilder()
                .dualSim(false)
                .signalStrength(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST)
                .lteRsrp(TestConstants.DEFAULT_LTE_RSRP_FIRST)
                .lteRsrq(TestConstants.DEFAULT_LTE_RSRQ_FIRST)
                .networkType(TestConstants.DEFAULT_NETWORK_TYPE_ID)
                .networkSimOperatorName(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME)
                .networkSimOperator(TestConstants.DEFAULT_TEST_NETWORK_SIM_OPERATOR)
                .roamingType(TestConstants.DEFAULT_ROAMING_TYPE_ID)
                .networkOperatorName(TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME)
                .build();
    }

    private TestLocation getTestLocation() {
        Linknet linknet = getLinknet();
        AdministrativeBoundaries administrativeBoundaries = getAdministrativeBoundaries();
        GeoLocation geoLocation = getGeoLocation();
        return TestLocation.builder()
                .geoLat(TestConstants.DEFAULT_LATITUDE)
                .geoLong(TestConstants.DEFAULT_LONGITUDE)
                .geoLocation(geoLocation)
                .geoAccuracy(TestConstants.DEFAULT_ACCURACY_FIRST)
                .geoProvider(TestConstants.DEFAULT_PROVIDER)
                .countryLocation(TestConstants.DEFAULT_COUNTRY_LOCATION)
                .gkzBev(TestConstants.DEFAULT_GKZ_BEV)
                .gkzSa(TestConstants.DEFAULT_GKZ_SA)
                .landCover(TestConstants.DEFAULT_LAND_COVER)
                .settlementType(TestConstants.DEFAULT_SETTLEMENT_TYPE)
                .linkName(TestConstants.DEFAULT_TEST_LOCATION_LINK_NAME)
                .linkDistance(TestConstants.DEFAULT_TEST_LOCATION_LINK_DISTANCE)
                .edgeId(TestConstants.DEFAULT_TEST_LOCATION_EDGE_ID)
                .frc(TestConstants.DEFAULT_TEST_LOCATION_FRC)
                .linknet(linknet)
                .administrativeBoundaries(administrativeBoundaries)
                .dtmLevel(TestConstants.DEFAULT_TEST_LOCATION_DTM_LEVEL)
                .build();
    }

    private TestDistance getTestDistance() {
        return TestDistance.builder()
                .maxAccuracy(TestConstants.DEFAULT_TEST_DISTANCE_MAX_ACCURACY_FIRST)
                .totalDistance(TestConstants.DEFAULT_TEST_DISTANCE_TOTAL_DISTANCE_FIRST)
                .build();
    }

    private Linknet getLinknet() {
        return Linknet.builder()
                .linkId(TestConstants.DEFAULT_LINKNET_LINK_ID)
                .name1(TestConstants.DEFAULT_LINKNET_NAME1)
                .name2(TestConstants.DEFAULT_LINKNET_NAME2)
                .build();
    }

    private AdministrativeBoundaries getAdministrativeBoundaries() {
        return AdministrativeBoundaries.builder()
                .locality(TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_LOCALITY)
                .community(TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_COMMUNITY)
                .district(TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_DISTRICT)
                .province(TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_PROVINE)
                .kgNrInt(TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_KG_NR)
                .build();
    }

    private GeoLocation getGeoLocation() {
        return GeoLocation.builder()
                .altitude(TestConstants.DEFAULT_ALTITUDE)
                .speed(TestConstants.DEFAULT_SPEED)
                .build();
    }

    private at.rtr.rmbt.model.Test getGeoTest() {
        var testLocation = getTestLocation();
        return getDefaultTestBuilder()
                .testLocation(testLocation)
                .build();
    }

    private at.rtr.rmbt.model.Test getDefaultTest() {
        Provider provider = getProvider();
        TestServer testServer = getTestServer();
        return getDefaultTestBuilder()
                .downloadSpeed(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED)
                .uploadSpeed(TestConstants.DEFAULT_RESULT_UPLOAD_SPEED)
                .pingMedian(TestConstants.DEFAULT_TEST_PING_MEDIAN)
                .countryAsn(TestConstants.DEFAULT_TEST_COUNTRY_ASN)
                .countryGeoip(TestConstants.DEFAULT_TEST_GEO_IP)
                .clientPublicIp(TestConstants.DEFAULT_TEST_CLIENT_PUBLIC_IP)
                .publicIpAsn(TestConstants.DEFAULT_TEST_PUBLIC_IP_ASN)
                .publicIpAsName(TestConstants.DEFAULT_TEST_PUBLIC_IP_AS_NAME)
                .publicIpRdns(TestConstants.DEFAULT_TEST_PUBLIC_IP_RDNS)
                .provider(provider)
                .clientIpLocalType(TestConstants.DEFAULT_TEST_CLIENT_IP_LOCAL_TYPE)
                .natType(TestConstants.DEFAULT_TEST_NAT_TYPE)
                .wifiSsid(TestConstants.DEFAULT_WIFI_SSID)
                .wifiBssid(TestConstants.DEFAULT_WIFI_BSSID)
                .wifiLinkSpeed(TestConstants.DEFAULT_WIFI_LINK_SPEED_FIRST)
                .totalBytesDownload(TestConstants.DEFAULT_TEST_TOTAL_BYTES_DOWNLOAD)
                .totalBytesUpload(TestConstants.DEFAULT_TEST_TOTAL_BYTES_UPLOAD)
                .testIfBytesDownload(TestConstants.DEFAULT_TEST_IF_BYTES_DOWNLOAD)
                .testIfBytesUpload(TestConstants.DEFAULT_TEST_IF_BYTES_UPLOAD)
                .testdlIfBytesDownload(TestConstants.DEFAULT_TEST_DL_IF_BYTES_DOWNLOAD)
                .testdlIfBytesUpload(TestConstants.DEFAULT_TEST_DL_IF_BYTES_UPLOAD)
                .testulIfBytesDownload(TestConstants.DEFAULT_TEST_UL_IF_BYTES_DOWNLOAD)
                .testulIfBytesUpload(TestConstants.DEFAULT_TEST_UL_IF_BYTES_UPLOAD)
                .downloadTimeNanoSeconds(TestConstants.DEFAULT_TIME_DOWNLOAD_OFFSET_NANOS)
                .uploadTimeNanoSeconds(TestConstants.DEFAULT_TIME_UPLOAD_OFFSET_NANOS)
                .nsecDownload(TestConstants.DEFAULT_DOWNLOAD_DURATION_NANOS)
                .nsecUpload(TestConstants.DEFAULT_UPLOAD_DURATION_NANOS)
                .testServer(testServer)
                .platform(TestConstants.DEFAULT_TEST_PLATFORM)
                .osVersion(TestConstants.DEFAULT_OS_VERSION)
                .model(TestConstants.DEFAULT_MODEL)
                .clientName(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE)
                .clientSoftwareVersion(TestConstants.DEFAULT_CLIENT_SOFTWARE_VERSION)
                .clientVersion(TestConstants.DEFAULT_CLIENT_VERSION)
                .duration(TestConstants.DEFAULT_TEST_DURATION)
                .numberOfThreads(TestConstants.DEFAULT_TEST_NUM_THREADS)
                .numberOfThreadsUpload(TestConstants.DEFAULT_TEST_NUM_THREADS_UPLOAD)
                .tag(TestConstants.DEFAULT_TAG)
                .build();
    }

    private TestServer getTestServer() {
        return TestServer.builder()
                .name(TestConstants.DEFAULT_TEST_SERVER_NAME)
                .build();
    }

    private Provider getProvider() {
        return Provider.builder()
                .shortName(TestConstants.DEFAULT_TEST_PROVIDER_NAME)
                .build();
    }

    private TestResultDetailResponse getGeoLocationTestResultResponse() {
        var properties = getDefaultProperties();
        properties.addAll(List.of(
                getTestResultDetailContainerResponse("key_location", TestConstants.DEFAULT_TEST_RESULT_DETAIL_LOCATION),
                getTestResultDetailContainerResponse("key_motion", TestConstants.DEFAULT_TEST_RESULT_DETAIL_MOTION),
                getTestResultDetailContainerResponse("key_country_location", TestConstants.DEFAULT_COUNTRY_LOCATION),
                getTestResultDetailContainerResponse("key_geo_altitude", TestConstants.DEFAULT_TEST_RESULT_DETAIL_GEO_ALTITUDE),
                getTestResultDetailContainerResponse("key_geo_speed", TestConstants.DEFAULT_TEST_RESULT_DETAIL_GEO_SPEED),
                getTestResultDetailContainerResponse("key_dtm_level", TestConstants.DEFAULT_TEST_RESULT_DETAIL_DTM_LEVEL),
                getTestResultDetailContainerResponse("key_gkz_bev", TestConstants.DEFAULT_GKZ_BEV.toString()),
                getTestResultDetailContainerResponse("key_gkz_sa", TestConstants.DEFAULT_GKZ_SA.toString()),
                getTestResultDetailContainerResponse("key_land_cover", TestConstants.DEFAULT_TEST_RESULT_DETAIL_LAND_COVER),
                getTestResultDetailContainerResponse("key_settlement_type", TestConstants.DEFAULT_TEST_RESULT_DETAIL_SETTLEMENT_TYPE),
                getTestResultDetailContainerResponse("key_link_name", TestConstants.DEFAULT_TEST_LOCATION_LINK_NAME),
                getTestResultDetailContainerResponse("key_link_distance", TestConstants.DEFAULT_TEST_LOCATION_LINK_DISTANCE.toString()),
                getTestResultDetailContainerResponse("key_edge_id", TestConstants.DEFAULT_TEST_LOCATION_EDGE_ID.toString()),
                getTestResultDetailContainerResponse("key_link_frc", TestConstants.DEFAULT_TEST_LOCATION_FRC.toString()),
                getTestResultDetailContainerResponse("key_link_id", TestConstants.DEFAULT_LINKNET_LINK_ID.toString()),
                getTestResultDetailContainerResponse("key_link_name1", TestConstants.DEFAULT_LINKNET_NAME1),
                getTestResultDetailContainerResponse("key_link_name2", TestConstants.DEFAULT_LINKNET_NAME2),
                getTestResultDetailContainerResponse("key_locality", TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_LOCALITY),
                getTestResultDetailContainerResponse("key_community", TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_COMMUNITY),
                getTestResultDetailContainerResponse("key_district", TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_DISTRICT),
                getTestResultDetailContainerResponse("key_province", TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_PROVINE),
                getTestResultDetailContainerResponse("key_kg_nr", TestConstants.DEFAULT_ADMINISTRATIVE_BOUNDARIES_KG_NR.toString())
        ));
        return TestResultDetailResponse.builder()
                .testResultDetailContainerResponse(properties)
                .build();
    }

    private TestResultDetailResponse getDefaultTestResultResponse() {
        var properties = getDefaultProperties();
        properties.addAll(List.of(
                getTestResultDetailContainerResponse("key_speed_download", TestConstants.DEFAULT_TEST_RESULT_DETAIL_SPEED_DOWNLOAD_VALUE),
                getTestResultDetailContainerResponse("key_speed_upload", TestConstants.DEFAULT_TEST_RESULT_DETAIL_SPEED_UPLOAD_VALUE),
                getTestResultDetailContainerResponse("key_ping_median", TestConstants.DEFAULT_TEST_RESULT_DETAIL_PING_MEDIAN_VALUE),
                getTestResultDetailContainerResponse("key_country_asn", TestConstants.DEFAULT_TEST_COUNTRY_ASN),
                getTestResultDetailContainerResponse("key_country_geoip", TestConstants.DEFAULT_TEST_GEO_IP),
                getTestResultDetailContainerResponse("key_client_public_ip", TestConstants.DEFAULT_TEST_CLIENT_PUBLIC_IP),
                getTestResultDetailContainerResponse("key_client_public_ip_as", TestConstants.DEFAULT_TEST_PUBLIC_IP_ASN.toString()),
                getTestResultDetailContainerResponse("key_client_public_ip_as_name", TestConstants.DEFAULT_TEST_PUBLIC_IP_AS_NAME),
                getTestResultDetailContainerResponse("key_client_public_ip_rdns", TestConstants.DEFAULT_TEST_PUBLIC_IP_RDNS),
                getTestResultDetailContainerResponse("key_provider", TestConstants.DEFAULT_TEST_PROVIDER_NAME),
                getTestResultDetailContainerResponse("key_client_local_ip", TestConstants.DEFAULT_TEST_CLIENT_IP_LOCAL_TYPE),
                getTestResultDetailContainerResponse("key_nat_type", TestConstants.DEFAULT_TEST_NAT_TYPE),
                getTestResultDetailContainerResponse("key_wifi_ssid", TestConstants.DEFAULT_WIFI_SSID),
                getTestResultDetailContainerResponse("key_wifi_bssid", TestConstants.DEFAULT_WIFI_BSSID),
                getTestResultDetailContainerResponse("key_wifi_link_speed", TestConstants.DEFAULT_TEST_RESULT_DETAIL_WIFI_LINK_SPEED),
                getTestResultDetailContainerResponse("key_total_bytes", TestConstants.DEFAULT_TEST_RESULT_DETAIL_TOTAL_BYTES),
                getTestResultDetailContainerResponse("key_total_if_bytes", TestConstants.DEFAULT_TEST_RESULT_DETAIL_TOTAL_BYTES_IF),
                getTestResultDetailContainerResponse("key_testdl_if_bytes_download", TestConstants.DEFAULT_TEST_RESULT_DETAIL_TEST_DL_IF_BYTES_DOWNLOAD),
                getTestResultDetailContainerResponse("key_testdl_if_bytes_upload", TestConstants.DEFAULT_TEST_RESULT_DETAIL_TEST_DL_IF_BYTES_UPLOAD),
                getTestResultDetailContainerResponse("key_testul_if_bytes_download", TestConstants.DEFAULT_TEST_RESULT_DETAIL_TEST_UL_IF_BYTES_DOWNLOAD),
                getTestResultDetailContainerResponse("key_testul_if_bytes_upload", TestConstants.DEFAULT_TEST_RESULT_DETAIL_TEST_UL_IF_BYTES_UPLOAD),
                getTestResultDetailContainerResponse("key_time_dl", TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME_DL),
                getTestResultDetailContainerResponse("key_time_ul", TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME_UL),
                getTestResultDetailContainerResponse("key_duration_dl", TestConstants.DEFAULT_TEST_RESULT_DETAIL_DURATION_DL),
                getTestResultDetailContainerResponse("key_duration_ul", TestConstants.DEFAULT_TEST_RESULT_DETAIL_DURATION_UL),
                getTestResultDetailContainerResponse("key_server_name", TestConstants.DEFAULT_TEST_SERVER_NAME),
                getTestResultDetailContainerResponse("key_plattform", TestConstants.DEFAULT_PLATFORM),
                getTestResultDetailContainerResponse("key_os_version", TestConstants.DEFAULT_OS_VERSION),
                getTestResultDetailContainerResponse("key_model", TestConstants.DEFAULT_MODEL),
                getTestResultDetailContainerResponse("key_client_name", TestConstants.DEFAULT_CLIENT_NAME),
                getTestResultDetailContainerResponse("key_client_software_version", TestConstants.DEFAULT_CLIENT_SOFTWARE_VERSION),
                getTestResultDetailContainerResponse("key_client_version", TestConstants.DEFAULT_CLIENT_VERSION),
                getTestResultDetailContainerResponse("key_duration", TestConstants.DEFAULT_TEST_RESULT_DETAIL_DURATION),
                getTestResultDetailContainerResponse("key_num_threads", TestConstants.DEFAULT_TEST_NUM_THREADS.toString()),
                getTestResultDetailContainerResponse("key_num_threads_ul", TestConstants.DEFAULT_TEST_NUM_THREADS_UPLOAD.toString()),
                getTestResultDetailContainerResponse("key_tag", TestConstants.DEFAULT_TAG)
        ));
        return TestResultDetailResponse.builder()
                .testResultDetailContainerResponse(properties)
                .build();
    }

    private TestResultDetailContainerResponse getTestResultDetailContainerResponse(String key, String value) {
        return TestResultDetailContainerResponse.builder()
                .title(messageSource.getMessage(key, null, Locale.ENGLISH))
                .value(value)
                .build();
    }

    private TestResultDetailResponse getDualSimResult() {
        var properties = getDefaultProperties();
        properties.addAll(List.of(
                getTestResultDetailContainerResponse("key_signal_strength", TestConstants.DEFAULT_TEST_RESULT_DETAIL_SIGNAL_STRENGTH_VALUE),
                getTestResultDetailContainerResponse("key_signal_rsrp", TestConstants.DEFAULT_TEST_RESULT_DETAIL_SIGNAL_RSRP_VALUE),
                getTestResultDetailContainerResponse("key_signal_rsrq", TestConstants.DEFAULT_TEST_RESULT_DETAIL_SIGNAL_RSRQ_VALUE),
                getTestResultDetailContainerResponse("key_network_type", TestConstants.DEFAULT_NETWORK_TYPE_VALUE),
                getTestResultDetailContainerResponse("key_network_sim_operator_name", TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME),
                getTestResultDetailContainerResponse("key_network_sim_operator", TestConstants.DEFAULT_TEST_NETWORK_SIM_OPERATOR),
                getTestResultDetailContainerResponse("key_roaming", TestConstants.DEFAULT_ROAMING_TYPE_VALUE),
                getTestResultDetailContainerResponse("key_network_operator_name", TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME)
        ));
        return TestResultDetailResponse.builder()
                .testResultDetailContainerResponse(properties)
                .build();
    }

    private TestResultDetailResponse getTimeTestResultDetailResponse() {
        var timeResponse = TestResultDetailContainerResponse.builder()
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME_STRING)
                .title(messageSource.getMessage("key_time", null, Locale.ENGLISH))
                .timezone(TestConstants.DEFAULT_TIMEZONE)
                .time(TestConstants.DEFAULT_TIME_INSTANT)
                .build();
        var timezoneResponse = TestResultDetailContainerResponse.builder()
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIMEZONE)
                .title(messageSource.getMessage("key_timezone", null, Locale.ENGLISH))
                .build();
        var properties = getDefaultProperties();
        properties.addAll(List.of(timeResponse, timezoneResponse));
        return TestResultDetailResponse.builder()
                .testResultDetailContainerResponse(properties)
                .build();
    }

    private List<TestResultDetailContainerResponse> getDefaultProperties() {
        var openUUIDProperty = TestResultDetailContainerResponse.builder()
                .openUUID(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_UUID)
                .title(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_UUID_TITLE)
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_UUID)
                .build();
        var openTestUUIDProperty = TestResultDetailContainerResponse.builder()
                .openTestUUID(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID)
                .title(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID_TITLE)
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID)
                .build();
        List<TestResultDetailContainerResponse> properties = new ArrayList<>();
        properties.add(openUUIDProperty);
        properties.add(openTestUUIDProperty);
        return properties;
    }

    private at.rtr.rmbt.model.Test.TestBuilder getDefaultTestBuilder() {
        return at.rtr.rmbt.model.Test.builder()
                .openUuid(TestConstants.DEFAULT_TEST_OPEN_UUID)
                .openTestUuid(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID);
    }
}
