package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.constant.ErrorMessage;
import at.rtr.rmbt.dto.LteFrequencyDto;
import at.rtr.rmbt.dto.QoeClassificationThresholds;
import at.rtr.rmbt.enums.NetworkGroupName;
import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.enums.TestPlatform;
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
import at.rtr.rmbt.utils.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;
    private final ApplicationProperties applicationProperties;
    private final GeoAnalyticsService geoAnalyticsService;
    private final MessageSource messageSource;
    private final SettingsRepository settingsRepository;
    private final QoeClassificationService qoeClassificationService;
    private final ClientRepository clientRepository;
    private final TestHistoryRepository testHistoryRepository;
    private final TestHistoryMapper testHistoryMapper;
    private final GeoLocationService geoLocationService;

    @Override
    public Test save(Test test) {
        return testRepository.save(test);
    }

    @Override
    public String getRmbtSetProviderFromAs(Long testUid) {
        return testRepository.getRmbtSetProviderFromAs(testUid);
    }

    @Override
    public Integer getRmbtNextTestSlot(Long testUid) {
        return testRepository.getRmbtNextTestSlot(testUid);
    }

    @Override
    public List<String> getDeviceHistory(List<Long> clientIds) {
        var resultList = testRepository.getDistinctModelByClientIdIn(clientIds);
        resultList.replaceAll(t -> Objects.isNull(t) ? Constants.UNKNOWN_DEVICE : t);
        return resultList;
    }

    @Override
    public List<String> getGroupNameByClientIds(List<Long> clientIds) {
        return testRepository.getDistinctGroupNameByClientIdIn(clientIds);
    }

    @Override
    public TestResponse getTestByUUID(UUID testUUID) {
        return testRepository.findByUuid(testUUID)
                .map(testMapper::testToTestResponse)
                .orElseThrow(() -> new TestNotFoundException(String.format(ErrorMessage.TEST_NOT_FOUND, testUUID)));
    }

    @Override
    public Optional<Test> getByOpenTestUuid(UUID openTestUUID) {
        return testRepository.findByOpenTestUuidAndImplausibleIsFalseAndDeletedIsFalse(openTestUUID);
    }

    @Override
    public Optional<Test> getByOpenTestUuidAndClientId(UUID openTestUUID, UUID clientUUID) {
        return testRepository.findByOpenTestUuidAndClientUuidAndImplausibleIsFalseAndDeletedIsFalse(openTestUUID, clientUUID);
    }

    @Override
    public TestResultDetailResponse getTestResultDetailByTestUUID(TestResultDetailRequest testResultDetailRequest) {
        final Locale locale = MessageUtils.getLocaleFormLanguage(testResultDetailRequest.getLanguage(), applicationProperties.getLanguage());
        List<TestResultDetailContainerResponse> propertiesList = new ArrayList<>();
        Test test = testRepository.findByUuidAndStatusesInAndActive(testResultDetailRequest.getTestUUID(), Config.TEST_RESULT_DETAIL_STATUSES)
                .orElseThrow(() -> new TestNotFoundException(String.format(ErrorMessage.TEST_NOT_FOUND, testResultDetailRequest.getTestUUID())));
        TestNdt testNdt = test.getTestNdt();

        addOpenUUID(propertiesList, test, locale);
        addOpenTestUUID(propertiesList, test, locale);
        addTime(propertiesList, test, locale);
        addTestFields(propertiesList, locale, test);
        addNdtFields(propertiesList, locale, testNdt);

        return TestResultDetailResponse.builder()
                .testResultDetailContainerResponse(propertiesList)
                .build();
    }

    @Override
    public TestResultContainerResponse getTestResult(TestResultRequest testResultRequest) {
        Test test = testRepository.findByUuidAndStatusesIn(testResultRequest.getTestUUID(), Config.TEST_RESULT_STATUSES)
                .orElseThrow(() -> new TestNotFoundException(String.format(ErrorMessage.TEST_NOT_FOUND, testResultRequest.getTestUUID())));
        Locale locale = MessageUtils.getLocaleFormLanguage(testResultRequest.getLanguage(), applicationProperties.getLanguage());
        String timeString = TimeUtils.getTimeStringFromTest(test, locale);
        TestResultResponse.TestResultResponseBuilder testResultResponseBuilder = TestResultResponse.builder()
                .time(test.getTime().toInstant().toEpochMilli())
                .timezone(test.getTimezone())
                .measurementResult(getMeasurementResult(test, testResultRequest.getCapabilitiesRequest()))
                .measurement(getMeasurements(test, locale, testResultRequest.getCapabilitiesRequest()))
                .openTestUUID(String.format(Constants.TEST_RESULT_DETAIL_OPEN_TEST_UUID_TEMPLATE, test.getOpenTestUuid()))
                .openUUID(String.format(Constants.TEST_RESULT_DETAIL_OPEN_UUID_TEMPLATE, test.getOpenUuid()))
                .shareSubject(MessageFormat.format(getStringFromBundle("RESULT_SHARE_SUBJECT", locale), timeString))
                .shareText(getShareText(test, timeString, locale))
                .timeString(timeString)
                .qoeClassificationResponses(getQoeClassificationResponse(test));
        setGeoLocationFields(testResultResponseBuilder, test, locale);
        setNetFields(testResultResponseBuilder, test, locale);
        return TestResultContainerResponse.builder()
                .testResultResponses(List.of(testResultResponseBuilder.build()))
                .build();
    }

    @Override
    public HistoryResponse getHistory(HistoryRequest historyRequest) {
        Locale locale = MessageUtils.getLocaleFormLanguage(ObjectUtils.defaultIfNull(historyRequest.getLanguage(), StringUtils.EMPTY), applicationProperties.getLanguage());
        RtrClient client = clientRepository.findByUuid(historyRequest.getClientUUID())
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, historyRequest.getClientUUID())));
        Integer count = Optional.ofNullable(historyRequest.getCapabilities())
                .map(CapabilitiesRequest::getClassification)
                .map(ClassificationRequest::getCount)
                .orElse(0);
        List<HistoryItemResponse> historyItemResponses = testHistoryRepository.getTestHistoryByDevicesAndNetworksAndClient(historyRequest.getResultLimit(), historyRequest.getResultOffset(), historyRequest.getDevices(), historyRequest.getNetworks(), client).stream()
                .map(testHistory -> testHistoryMapper.testHistoryToHistoryItemResponse(testHistory, count, locale))
                .collect(Collectors.toList());
        return HistoryResponse.builder()
                .history(historyItemResponses)
                .build();
    }

    @Override
    public ResultUpdateResponse updateTestResult(ResultUpdateRequest resultUpdateRequest) {
        Test test = testRepository.findByUuid(resultUpdateRequest.getTestUUID())
                .orElseThrow(() -> new TestNotFoundException(String.format(ErrorMessage.TEST_NOT_FOUND, resultUpdateRequest.getTestUUID())));
        RtrClient client = clientRepository.findByUuid(resultUpdateRequest.getUuid())
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, resultUpdateRequest.getUuid())));
        if (!Objects.equals(client, test.getClient())) {
            throw new IllegalArgumentException(ErrorMessage.CLIENT_DOES_MATCH_TEST);
        }
        if (resultUpdateRequest.isAborted()) {
            test.setStatus(TestStatus.ABORTED);
        } else {
            geoLocationService.updateGeoLocation(test, resultUpdateRequest);
            test.setLocation(GeometryUtils.getGeometryFromLongitudeAndLatitude(test.getLongitude(), test.getLatitude()));
        }
        testRepository.save(test);
        return ResultUpdateResponse.builder()
                .build();
    }

    @Override
    public ImplausibleResponse setImplausible(ImplausibleRequest implausibleRequest) {
        if (StringUtils.isBlank(implausibleRequest.getComment()) || StringUtils.isBlank(implausibleRequest.getUuid())) {
            throw new IllegalArgumentException(ErrorMessage.REQUIRED_FIELDS_MISSING);
        }
        String comment = implausibleRequest.getComment().concat(Constants.WEB_COMMENT);
        boolean implausible = ObjectUtils.defaultIfNull(implausibleRequest.getImplausible(), true);
        String uuidField = getUUIDField(implausibleRequest.getUuid());
        UUID uuid = UUID.fromString(implausibleRequest.getUuid().substring(Constants.UUID_PREFIX_SIZE));
        Integer affectedRows = testRepository.updateImplausible(implausible, comment, uuidField, uuid);
        return ImplausibleResponse.builder()
                .affectedRows(affectedRows)
                .status(Constants.STATUS_OK)
                .build();
    }

    private String getUUIDField(String uuid) {
        switch (uuid.charAt(0)) {
            case 'P':
                return "open_uuid";
            case 'O':
                return "open_test_uuid";
            case 'T':
                return "t.uuid";
            case 'U':
                return "c.uuid";
            default:
                throw new IllegalArgumentException(ErrorMessage.INVALID_UUID_TYPE);
        }
    }


    private List<QoeClassificationResponse> getQoeClassificationResponse(Test test) {
        long pingNs = Optional.ofNullable(test.getPingMedian())
                .orElse(NumberUtils.LONG_ZERO);
        long downKBps = Optional.ofNullable(test.getDownloadSpeed())
                .map(Integer::longValue)
                .orElse(NumberUtils.LONG_ZERO);
        long upKbps = Optional.ofNullable(test.getUploadSpeed())
                .map(Integer::longValue)
                .orElse(NumberUtils.LONG_ZERO);
        List<QoeClassificationThresholds> qoeClassificationThresholds = qoeClassificationService.getQoeClassificationThreshold();
        return ClassificationUtils.classify(pingNs, downKBps, upKbps, qoeClassificationThresholds);
    }

    private void setNetFields(TestResultResponse.TestResultResponseBuilder testResultResponseBuilder, Test test, Locale locale) {
        List<NetItemResponse> netItemResponses = new ArrayList<>();
        NetworkInfoResponse.NetworkInfoResponseBuilder networkInfoResponseBuilder = NetworkInfoResponse.builder();
        boolean dualSim = MeasurementUtils.isDualSim(test.getNetworkType(), test.getDualSim());
        boolean useSignal = MeasurementUtils.isUseSignal(test.getSimCount(), dualSim);
        if (useSignal) {
            String networkTypeName = HelperFunctions.getNetworkTypeName(test.getNetworkType());
            addNetItemResponse(locale, netItemResponses, networkTypeName, "RESULT_NETWORK_TYPE");
            networkInfoResponseBuilder.networkTypeLabel(networkTypeName);
        } else {
            addNetItemResponse(locale, netItemResponses, getStringFromBundle("RESULT_DUAL_SIM", locale), "RESULT_NETWORK_TYPE");
            networkInfoResponseBuilder.networkTypeLabel(getStringFromBundle("RESULT_DUAL_SIM", locale));
        }
        if (test.getNetworkType() == 98 || test.getNetworkType() == 99) // mobile wifi or browser
        {
            Optional.ofNullable(test.getProvider())
                    .map(Provider::getShortName)
                    .ifPresent(providerName -> {
                        addNetItemResponse(locale, netItemResponses, providerName, "RESULT_OPERATOR_NAME");
                        networkInfoResponseBuilder.providerName(providerName);
                    });
            if (test.getNetworkType() == 99)  // mobile wifi
            {
                Optional.ofNullable(test.getWifiSsid())
                        .ifPresent(wifiSSID -> {
                            addNetItemResponse(locale, netItemResponses, wifiSSID, "RESULT_WIFI_SSID");
                            networkInfoResponseBuilder.wifiSSID(wifiSSID);
                        });
            }
        } else {
            if (!dualSim) {
                Optional.ofNullable(test.getNetworkOperatorName())
                        .ifPresent(networkOperatorName -> {
                            addNetItemResponse(locale, netItemResponses, networkOperatorName, "RESULT_OPERATOR_NAME");
                            networkInfoResponseBuilder.providerName(networkOperatorName);
                        });

                Optional.ofNullable(test.getRoamingType())
                        .filter(roamingType -> roamingType > Constants.INTERNATIONAL_ROAMING_TYPE_BARRIER)
                        .ifPresent(roamingType -> {
                            String roamingTypeName = HelperFunctions.getRoamingType(messageSource, roamingType, locale);
                            addNetItemResponse(locale, netItemResponses, roamingTypeName, "RESULT_ROAMING");
                            networkInfoResponseBuilder.roamingTypeLabel(roamingTypeName);
                        });
            }
        }
        testResultResponseBuilder.netItemResponses(netItemResponses);
        testResultResponseBuilder.networkInfoResponse(networkInfoResponseBuilder.build());
        testResultResponseBuilder.networkType(test.getNetworkType());
    }

    private void addNetItemResponse(Locale locale, List<NetItemResponse> netItemResponses, String providerName, String titleKey) {
        NetItemResponse netItemResponse = NetItemResponse.builder()
                .title(getStringFromBundle(titleKey, locale))
                .value(providerName)
                .build();
        netItemResponses.add(netItemResponse);
    }

    private void setGeoLocationFields(TestResultResponse.TestResultResponseBuilder testResultResponseBuilder, Test test, Locale locale) {
        if (Objects.nonNull(test.getLatitude()) && Objects.nonNull(test.getLongitude()) && Objects.nonNull(test.getGeoAccuracy())) {
            if (test.getGeoAccuracy() < applicationProperties.getAccuracyButtonLimit()) {
                testResultResponseBuilder
                        .geoLat(test.getLatitude())
                        .geoLong(test.getLongitude());
            }
            testResultResponseBuilder.location(getShareLocationString(test, locale));
        }
    }

    private MeasurementResultResponse getMeasurementResult(Test test, CapabilitiesRequest capabilitiesRequest) {
        MeasurementResultResponse.MeasurementResultResponseBuilder measurementResultResponseBuilder = MeasurementResultResponse.builder();
        setDownloadFields(test, measurementResultResponseBuilder, capabilitiesRequest);
        setUploadFields(test, measurementResultResponseBuilder, capabilitiesRequest);
        setPingFields(test, measurementResultResponseBuilder, capabilitiesRequest);
        setSignalFields(test, measurementResultResponseBuilder, capabilitiesRequest);
        return measurementResultResponseBuilder
                .build();
    }

    private void setSignalFields(Test test, MeasurementResultResponse.MeasurementResultResponseBuilder measurementResultResponseBuilder, CapabilitiesRequest capabilitiesRequest) {
        boolean dualSim = MeasurementUtils.isDualSim(test.getNetworkType(), test.getDualSim());
        boolean useSignal = MeasurementUtils.isUseSignal(test.getSimCount(), dualSim);
        if (useSignal) {
            if (Objects.nonNull(test.getSignalStrength())) {
                int[] threshold = ClassificationUtils.getThresholdForSignal(test.getNetworkType());
                measurementResultResponseBuilder
                        .signalStrength(test.getSignalStrength())
                        .signalClassification(ClassificationUtils.classify(threshold, test.getSignalStrength(), capabilitiesRequest.getClassification().getCount()));
            }
            if (Objects.nonNull(test.getLteRsrp())) {
                measurementResultResponseBuilder
                        .lteRSRP(test.getLteRsrp())
                        .signalClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_SIGNAL_RSRP, test.getLteRsrp(), capabilitiesRequest.getClassification().getCount()));
            }
        }
    }

    private void setPingFields(Test test, MeasurementResultResponse.MeasurementResultResponseBuilder measurementResultResponseBuilder, CapabilitiesRequest capabilitiesRequest) {
        Optional.ofNullable(test.getPingMedian())
                .ifPresent(pingMedian -> measurementResultResponseBuilder
                        .pingClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_PING, pingMedian, capabilitiesRequest.getClassification().getCount()))
                        .pingMs(getPingMsFromPingMedian(pingMedian)));
    }

    private void setUploadFields(Test test, MeasurementResultResponse.MeasurementResultResponseBuilder measurementResultResponseBuilder, CapabilitiesRequest capabilitiesRequest) {
        Optional.ofNullable(test.getUploadSpeed())
                .ifPresent(uploadSpeed -> measurementResultResponseBuilder
                        .uploadClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_UPLOAD, uploadSpeed, capabilitiesRequest.getClassification().getCount()))
                        .uploadKBit(uploadSpeed));
    }

    private void setDownloadFields(Test test, MeasurementResultResponse.MeasurementResultResponseBuilder measurementResultResponseBuilder, CapabilitiesRequest capabilitiesRequest) {
        Optional.ofNullable(test.getDownloadSpeed())
                .ifPresent(downloadSpeed -> measurementResultResponseBuilder
                        .downloadClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_DOWNLOAD, downloadSpeed, capabilitiesRequest.getClassification().getCount()))
                        .downloadKBit(downloadSpeed));
    }

    private double getPingMsFromPingMedian(Long x) {
        return x / Constants.PING_CONVERSION_MULTIPLICATOR;
    }

    private List<TestResultMeasurementResponse> getMeasurements(Test test, Locale locale, CapabilitiesRequest capabilitiesRequest) {
        List<TestResultMeasurementResponse> measurementResponses = new ArrayList<>();
        addDownloadTestResultMeasurementResponse(measurementResponses, test, locale, capabilitiesRequest);
        addUploadTestResultMeasurementResponse(measurementResponses, test, locale, capabilitiesRequest);
        addPingTestResultMeasurementResponse(measurementResponses, test, locale, capabilitiesRequest);
        addSignalTestResultMeasurementResponse(measurementResponses, test, locale, capabilitiesRequest);
        return measurementResponses;
    }

    private void addSignalTestResultMeasurementResponse(List<TestResultMeasurementResponse> measurementResponses, Test test, Locale locale, CapabilitiesRequest capabilitiesRequest) {
        boolean dualSim = MeasurementUtils.isDualSim(test.getNetworkType(), test.getDualSim());
        boolean useSignal = MeasurementUtils.isUseSignal(test.getSimCount(), dualSim);

        if (useSignal) {
            if (Objects.nonNull(test.getSignalStrength()) || Objects.nonNull(test.getLteRsrp())) {
                TestResultMeasurementResponse.TestResultMeasurementResponseBuilder signalResponseBuilder = TestResultMeasurementResponse.builder()
                        .value(getSignalString(test, locale, useSignal));
                if (Objects.nonNull(test.getSignalStrength())) {
                    int[] threshold = ClassificationUtils.getThresholdForSignal(test.getNetworkType());
                    signalResponseBuilder
                            .classification(ClassificationUtils.classify(threshold, test.getSignalStrength(), capabilitiesRequest.getClassification().getCount()))
                            .title(getStringFromBundle("RESULT_SIGNAL", locale));
                } else {
                    signalResponseBuilder
                            .classification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_SIGNAL_RSRP, test.getLteRsrp(), capabilitiesRequest.getClassification().getCount()))
                            .title(getStringFromBundle("RESULT_SIGNAL_RSRP", locale));
                }

                TestResultMeasurementResponse signalResponse = signalResponseBuilder.build();
                measurementResponses.add(signalResponse);
            }
        }
    }

    private void addPingTestResultMeasurementResponse(List<TestResultMeasurementResponse> measurementResponses, Test test, Locale locale, CapabilitiesRequest capabilitiesRequest) {
        Optional.ofNullable(test.getPingMedian())
                .ifPresent(pingMedian -> {
                    TestResultMeasurementResponse pingResponse = TestResultMeasurementResponse.builder()
                            .title(getStringFromBundle("RESULT_PING", locale))
                            .value(getPingString(test, locale))
                            .classification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_PING, pingMedian, capabilitiesRequest.getClassification().getCount()))
                            .build();
                    measurementResponses.add(pingResponse);
                });
    }

    private void addUploadTestResultMeasurementResponse(List<TestResultMeasurementResponse> measurementResponses, Test test, Locale locale, CapabilitiesRequest capabilitiesRequest) {
        Optional.ofNullable(test.getUploadSpeed())
                .ifPresent(uploadSpeed -> {
                    TestResultMeasurementResponse uploadResponse = TestResultMeasurementResponse.builder()
                            .title(getStringFromBundle("RESULT_UPLOAD", locale))
                            .value(getUploadString(test, locale))
                            .classification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_UPLOAD, uploadSpeed, capabilitiesRequest.getClassification().getCount()))
                            .build();
                    measurementResponses.add(uploadResponse);
                });
    }

    private void addDownloadTestResultMeasurementResponse(List<TestResultMeasurementResponse> measurementResponses, Test test, Locale locale, CapabilitiesRequest capabilitiesRequest) {
        Optional.ofNullable(test.getDownloadSpeed())
                .ifPresent(downloadSpeed -> {
                    TestResultMeasurementResponse downloadResponse = TestResultMeasurementResponse.builder()
                            .title(getStringFromBundle("RESULT_DOWNLOAD", locale))
                            .value(getDownloadString(test, locale))
                            .classification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_DOWNLOAD, downloadSpeed, capabilitiesRequest.getClassification().getCount()))
                            .build();
                    measurementResponses.add(downloadResponse);
                });
    }

    private String getShareText(Test test, String timeString, Locale locale) {
        boolean dualSim = MeasurementUtils.isDualSim(test.getNetworkType(), test.getDualSim());
        boolean useSignal = MeasurementUtils.isUseSignal(test.getSimCount(), dualSim);
        String signalString = getSignalString(test, locale, useSignal);
        String shareLocationString = getShareLocationString(test, locale);
        String downloadString = getDownloadString(test, locale);
        String uploadString = getUploadString(test, locale);
        String pingString = getPingString(test, locale);
        String shareTextField4 = getShareTextField4(test, locale, signalString);
        String platformString = getPlatformString(test);
        String modelString = getModelString(test);
        String networkTypeString = getNetworkTypeString(test);
        String providerString = getProviderString(test, locale);
        String mobileNetworkString = getMobileNetworkString(test, locale);
        String urlShareString = getUrlShareString(test, locale);

        if (dualSim) {
            return MessageFormat.format(getStringFromBundle("RESULT_SHARE_TEXT", locale),
                    timeString,
                    downloadString,
                    uploadString,
                    pingString,
                    shareTextField4,
                    getStringFromBundle("RESULT_DUAL_SIM", locale),
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    platformString,
                    modelString,
                    shareLocationString,
                    urlShareString);
        } else {
            return MessageFormat.format(getStringFromBundle("RESULT_SHARE_TEXT", locale),
                    timeString,
                    downloadString,
                    uploadString,
                    pingString,
                    shareTextField4,
                    networkTypeString,
                    providerString,
                    mobileNetworkString,
                    platformString,
                    modelString,
                    shareLocationString,
                    urlShareString);
        }
    }

    private String getUrlShareString(Test test, Locale locale) {
        return settingsRepository.findAllByLangOrLangIsNullAndKeyIn(locale.getLanguage(), List.of(Config.URL_SHARE_KEY)).stream()
                .findFirst()
                .map(Settings::getValue)
                .map(value -> String.join(StringUtils.EMPTY, value, test.getOpenTestUuid().toString()))
                .orElse(StringUtils.EMPTY);
    }

    private String getProviderString(Test test, Locale locale) {
        return Optional.ofNullable(test.getProvider())
                .map(Provider::getShortName)
                .map(provider -> MessageFormat.format(getStringFromBundle("RESULT_SHARE_TEXT_PROVIDER_ADD", locale),
                        provider))
                .orElse(StringUtils.EMPTY);
    }

    private String getNetworkTypeString(Test test) {
        return Optional.ofNullable(test.getNetworkType())
                .map(HelperFunctions::getNetworkTypeName)
                .orElse(StringUtils.EMPTY);
    }

    private String getModelString(Test test) {
        return Optional.ofNullable(test.getModel())
                .orElse(StringUtils.EMPTY);
    }

    private String getPlatformString(Test test) {
        return Optional.ofNullable(test.getPlatform())
                .map(TestPlatform::getLabel)
                .orElse(StringUtils.EMPTY);
    }

    private String getShareLocationString(Test test, Locale locale) {
        return Optional.ofNullable(test.getTestLocation())
                .map(testLocation -> MessageFormat.format(getStringFromBundle("RESULT_SHARE_TEXT_LOCATION_ADD", locale), getGeoLocationString(testLocation, locale)))
                .orElse(StringUtils.EMPTY);
    }

    private String getSignalString(Test test, Locale locale, boolean useSignal) {
        if (useSignal) {
            if (Objects.nonNull(test.getSignalStrength())) {
                return FormatUtils.formatValueAndUnit(test.getSignalStrength(), getStringFromBundle("RESULT_SIGNAL_UNIT", locale));
            }
            if (Objects.nonNull(test.getLteRsrp())) {
                return FormatUtils.formatValueAndUnit(test.getLteRsrp(), getStringFromBundle("RESULT_SIGNAL_UNIT", locale));
            }
        }
        return null;
    }

    private String getPingString(Test test, Locale locale) {
        return Optional.ofNullable(test.getPingMedian())
                .map(this::getPingMsFromPingMedian)
                .map(pingMs -> FormatUtils.formatValueAndUnit(pingMs, getStringFromBundle("RESULT_PING_UNIT", locale), locale))
                .orElse(StringUtils.EMPTY);
    }

    private String getUploadString(Test test, Locale locale) {
        return Optional.ofNullable(test.getUploadSpeed())
                .map(x -> x / Constants.BYTES_UNIT_CONVERSION_MULTIPLICATOR)
                .map(downloadSpeed -> FormatUtils.formatValueAndUnit(downloadSpeed, getStringFromBundle("RESULT_UPLOAD_UNIT", locale), locale))
                .orElse(StringUtils.EMPTY);
    }

    private String getDownloadString(Test test, Locale locale) {
        return Optional.ofNullable(test.getDownloadSpeed())
                .map(x -> x / Constants.BYTES_UNIT_CONVERSION_MULTIPLICATOR)
                .map(downloadSpeed -> FormatUtils.formatValueAndUnit(downloadSpeed, getStringFromBundle("RESULT_DOWNLOAD_UNIT", locale), locale))
                .orElse(StringUtils.EMPTY);
    }

    private String getMobileNetworkString(Test test, Locale locale) {
        if (Objects.nonNull(test.getNetworkOperator())) {
            String mobileString = Optional.ofNullable(test.getMobileProvider())
                    .map(Provider::getShortName)
                    .map(providerName -> String.format(Constants.PARENTHESES_TEMPLATE, providerName, test.getNetworkOperator()))
                    .orElse(test.getNetworkOperator());
            return MessageFormat.format(getStringFromBundle("RESULT_SHARE_TEXT_MOBILE_ADD", locale), mobileString);
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String getShareTextField4(Test test, Locale locale, String signalString) {
        if (Objects.nonNull(signalString)) {
            if (Objects.isNull(test.getLteRsrp())) {
                return MessageFormat.format(getStringFromBundle("RESULT_SHARE_TEXT_SIGNAL_ADD", locale), signalString);
            } else {
                return MessageFormat.format(getStringFromBundle("RESULT_SHARE_TEXT_RSRP_ADD", locale), signalString);
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    private void addGeoLocation(List<TestResultDetailContainerResponse> propertiesList, Locale locale, TestLocation testLocation, UUID openTestUUID) {
        if (Objects.nonNull(testLocation.getGeoLat()) && Objects.nonNull(testLocation.getGeoLong()) && Objects.nonNull(testLocation.getGeoAccuracy())) {
            if (testLocation.getGeoAccuracy() < applicationProperties.getAccuracyDetailLimit()) {
                final String geoString = getGeoLocationString(testLocation, locale);
                addString(propertiesList, locale, "location", geoString);

                Optional.ofNullable(geoAnalyticsService.getTestDistance(openTestUUID))
                        .filter(testDistance -> testDistance.getTotalDistance() > NumberUtils.DOUBLE_ZERO)
                        .filter(testDistance -> testDistance.getTotalDistance() < applicationProperties.getAccuracyDetailLimit())
                        .ifPresent(x -> addLongAndUnitString(propertiesList, locale, "motion", Math.round(x.getTotalDistance()), "RESULT_METER_UNIT"));
            }
            addString(propertiesList, locale, "country_location", testLocation.getCountryLocation());
        }
        Optional.ofNullable(testLocation.getGeoLocation())
                .ifPresent(geoLocation -> {
                    Optional.ofNullable(geoLocation.getAltitude())
                            .filter(altitude -> !altitude.equals(NumberUtils.DOUBLE_ZERO))
                            .map(Math::round)
                            .ifPresent(altitude -> addLongAndUnitString(propertiesList, locale, "geo_altitude", altitude, "RESULT_METER_UNIT"));
                    Optional.ofNullable(geoLocation.getSpeed())
                            .filter(geoSpeed -> geoSpeed > 0.1d)
                            .map(x -> Math.round(Constants.METERS_PER_SECOND_TO_KILOMETERS_PER_HOURS_MULTIPLICATOR * x))
                            .ifPresent(speed -> addLongAndUnitString(propertiesList, locale, "geo_speed", speed, "RESULT_KILOMETER_PER_HOUR_UNIT"));
                });
        Optional.ofNullable(testLocation.getDtmLevel())
                .ifPresent(dtmLevel -> addIntegerAndUnitString(propertiesList, locale, "dtm_level", dtmLevel, "RESULT_METER_UNIT"));
    }

    private String getGeoLocationString(TestLocation testLocation, Locale locale) {
        Double latField = testLocation.getGeoLat();
        Double longField = testLocation.getGeoLong();
        Double accuracyField = testLocation.getGeoAccuracy();
        String providerField = testLocation.getGeoProvider();
        final StringBuilder geoString = new StringBuilder(HelperFunctions.geoToString(latField,
                longField));
        geoString.append(" (");
        if (Objects.nonNull(providerField)) {
            String provider = providerField.toUpperCase(Locale.US);
            switch (provider) {
                case "NETWORK":
                    provider = getStringFromBundle("key_geo_source_network", locale);
                    break;
                case "GPS":
                    provider = getStringFromBundle("key_geo_source_gps", locale);
                    break;
            }
            geoString.append(provider);
            geoString.append(", ");
        }
        geoString.append(String.format(Locale.US, "+/- %.0f m", accuracyField));
        geoString.append(")");
        return geoString.toString();
    }

    private void addOpenUUID(List<TestResultDetailContainerResponse> propertiesList, Test test, Locale locale) {
        TestResultDetailContainerResponse timeResponse = TestResultDetailContainerResponse.builder()
                .title(getStringFromBundleWithKeyPrefix("open_uuid", locale))
                .value(String.format(Constants.TEST_RESULT_DETAIL_OPEN_UUID_TEMPLATE, test.getOpenUuid()))
                .openUUID(String.format(Constants.TEST_RESULT_DETAIL_OPEN_UUID_TEMPLATE, test.getOpenUuid()))
                .build();
        propertiesList.add(timeResponse);
    }

    private void addOpenTestUUID(List<TestResultDetailContainerResponse> propertiesList, Test test, Locale locale) {
        TestResultDetailContainerResponse timeResponse = TestResultDetailContainerResponse.builder()
                .title(getStringFromBundleWithKeyPrefix("open_test_uuid", locale))
                .value(String.format(Constants.TEST_RESULT_DETAIL_OPEN_TEST_UUID_TEMPLATE, test.getOpenTestUuid()))
                .openTestUUID(String.format(Constants.TEST_RESULT_DETAIL_OPEN_TEST_UUID_TEMPLATE, test.getOpenTestUuid()))
                .build();
        propertiesList.add(timeResponse);
    }

    private void addTime(List<TestResultDetailContainerResponse> propertiesList, Test test, Locale locale) {
        if (Objects.nonNull(test.getTime()) && Objects.nonNull(test.getTimezone())) {
            Date date = Date.from(test.getTime().toInstant());
            TimeZone timeZone = TimeZone.getTimeZone(test.getTimezone());
            TestResultDetailContainerResponse timeResponse = buildTimeResponse(test, date, timeZone, locale);
            TestResultDetailContainerResponse timezoneResponse = buildTimezoneResponse(timeZone, date.getTime(), locale);
            propertiesList.add(timeResponse);
            propertiesList.add(timezoneResponse);
        }
    }

    private TestResultDetailContainerResponse buildTimezoneResponse(TimeZone timeZone, long time, Locale locale) {
        return TestResultDetailContainerResponse.builder()
                .title(getStringFromBundleWithKeyPrefix("timezone", locale))
                .value(getTimezoneValue(timeZone, time, locale))
                .build();
    }

    private TestResultDetailContainerResponse buildTimeResponse(Test test, Date date, TimeZone timeZone, Locale locale) {
        return TestResultDetailContainerResponse.builder()
                .time(date.getTime())
                .timezone(test.getTimezone())
                .title(getStringFromBundleWithKeyPrefix("time", locale))
                .value(TimeUtils.getTimeString(date, timeZone, locale))
                .build();
    }

    private String getTimezoneValue(TimeZone timezone, long time, Locale locale) {
        Format timeZoneFormat = new DecimalFormat(Constants.TIMEZONE_PATTERN, new DecimalFormatSymbols(locale));
        double offset = timezone.getOffset(time) / Constants.MILLISECONDS_TO_HOURS;
        return String.format(Constants.TIMEZONE_TEMPLATE, timeZoneFormat.format(offset));
    }

    private void addTestFields(List<TestResultDetailContainerResponse> propertiesList, Locale locale, Test test) {
        if (!MeasurementUtils.isDualSim(test.getNetworkType(), test.getDualSim())) {
            addIntegerAndUnitString(propertiesList, locale, "signal_strength", test.getSignalStrength(), "RESULT_SIGNAL_UNIT");
            addIntegerAndUnitString(propertiesList, locale, "signal_rsrp", test.getLteRsrp(), "RESULT_SIGNAL_UNIT");
            addIntegerAndUnitString(propertiesList, locale, "signal_rsrq", test.getLteRsrq(), "RESULT_DB_UNIT");
            Optional.ofNullable(test.getNetworkType())
                    .map(HelperFunctions::getNetworkTypeName)
                    .ifPresent(networkType -> addString(propertiesList, locale, "network_type", networkType));
            addString(propertiesList, locale, "network_sim_operator_name", test.getNetworkSimOperatorName());
            addString(propertiesList,
                    locale,
                    "network_sim_operator",
                    test.getNetworkSimOperator());
            Optional.ofNullable(test.getRoamingType())
                    .map(roamingType -> HelperFunctions.getRoamingType(messageSource, roamingType, locale))
                    .ifPresent(roamingName -> addString(propertiesList, locale, "roaming", roamingName));
            Optional.of(test)
                    .map(Test::getMobileProvider)
                    .map(Provider::getShortName)
                    .ifPresentOrElse(mobileProviderShortName -> addNetworkOperator(propertiesList, locale, test, mobileProviderShortName), () -> addString(propertiesList, locale, "network_operator_name", test.getNetworkOperatorName()));
        }
        Optional.ofNullable(test.getDownloadSpeed())
                .map(x -> x / Constants.BYTES_UNIT_CONVERSION_MULTIPLICATOR)
                .ifPresent(downloadSpeed -> addDoubleAndUnitString(propertiesList, locale, "speed_download", downloadSpeed, "RESULT_DOWNLOAD_UNIT"));
        Optional.ofNullable(test.getUploadSpeed())
                .map(x -> x / Constants.BYTES_UNIT_CONVERSION_MULTIPLICATOR)
                .ifPresent(uploadSpeed -> addDoubleAndUnitString(propertiesList, locale, "speed_upload", uploadSpeed, "RESULT_UPLOAD_UNIT"));
        Optional.ofNullable(test.getPingMedian())
                .map(this::getPingMsFromPingMedian)
                .ifPresent(pingMedian -> addDoubleAndUnitString(propertiesList, locale, "ping_median", pingMedian, "RESULT_PING_UNIT"));
        addString(propertiesList, locale, "country_asn", test.getCountryAsn());
        addString(propertiesList, locale, "country_geoip", test.getCountryGeoip());
        addString(propertiesList, locale, "client_public_ip", test.getClientPublicIp());
        addLong(propertiesList, locale, "client_public_ip_as", test.getPublicIpAsn());
        addString(propertiesList, locale, "client_public_ip_as_name", test.getPublicIpAsName());
        addString(propertiesList, locale, "client_public_ip_rdns", test.getPublicIpRdns());
        Optional.ofNullable(test.getProvider())
                .map(Provider::getShortName)
                .ifPresent(providerShortName -> addString(propertiesList, locale, "provider", providerShortName));
        addString(propertiesList, locale, "client_local_ip", test.getClientIpLocalType());
        addString(propertiesList, locale, "nat_type", test.getNatType());
        addString(propertiesList, locale, "wifi_ssid", test.getWifiSsid());
        addString(propertiesList, locale, "wifi_bssid", test.getWifiBssid());
        addIntegerAndUnitString(propertiesList, locale, "wifi_link_speed", test.getWifiLinkSpeed(), "RESULT_WIFI_LINK_SPEED_UNIT");
        Optional.of(test)
                .map(Test::getTestLocation)
                .ifPresent(testLocation -> addTestLocationFields(propertiesList, locale, testLocation, test.getOpenTestUuid()));
        Optional.of(test)
                .map(Test::getRadioBand)
                .ifPresent(bandField -> addBandAndFrequency(propertiesList, locale, test, bandField));
        addSumOfTwoFields(propertiesList, locale, test.getTotalBytesDownload(), test.getTotalBytesUpload(), "total_bytes");
        addSumOfTwoFields(propertiesList, locale, test.getTestIfBytesDownload(), test.getTestIfBytesUpload(), "total_if_bytes");
        addBytes(propertiesList, locale, "testdl_if_bytes_download", test.getTestdlIfBytesDownload());
        addBytes(propertiesList, locale, "testdl_if_bytes_upload", test.getTestdlIfBytesUpload());
        addBytes(propertiesList, locale, "testul_if_bytes_download", test.getTestulIfBytesDownload());
        addBytes(propertiesList, locale, "testul_if_bytes_upload", test.getTestulIfBytesUpload());
        addSeconds(propertiesList, locale, "time_dl", test.getDownloadTimeNanoSeconds());
        addSeconds(propertiesList, locale, "time_ul", test.getUploadTimeNanoSeconds());
        addSeconds(propertiesList, locale, "duration_dl", test.getNsecDownload());
        addSeconds(propertiesList, locale, "duration_ul", test.getNsecUpload());
        Optional.ofNullable(test.getTestServer())
                .map(TestServer::getName)
                .ifPresent(testServerName -> addString(propertiesList, locale, "server_name", testServerName));
        Optional.ofNullable(test.getPlatform())
                .map(TestPlatform::getLabel)
                .ifPresent(platformLabel -> addString(propertiesList, locale, "plattform", platformLabel));
        addString(propertiesList, locale, "os_version", test.getOsVersion());
        addString(propertiesList, locale, "model", test.getModel());
        Optional.ofNullable(test.getClientName())
                .map(ServerType::getLabel)
                .ifPresent(serverTypeLabel -> addString(propertiesList, locale, "client_name", serverTypeLabel));
        addString(propertiesList, locale, "client_software_version", test.getClientSoftwareVersion());
        addString(propertiesList, locale, "client_version", test.getClientVersion());
        addIntegerAndUnitString(propertiesList, locale, "duration", test.getDuration(), "RESULT_DURATION_UNIT");
        addInteger(propertiesList, locale, "num_threads", test.getNumberOfThreads());
        addInteger(propertiesList, locale, "num_threads_ul", test.getNumberOfThreadsUpload());
        addString(propertiesList, locale, "tag", test.getTag());
    }

    private void addSumOfTwoFields(List<TestResultDetailContainerResponse> propertiesList, Locale locale, Long totalBytesDownload, Long totalBytesUpload, String title) {
        if (Objects.nonNull(totalBytesDownload) && Objects.nonNull(totalBytesUpload)) {
            addBytes(propertiesList, locale, title, totalBytesDownload + totalBytesUpload);
        }
    }

    private void addTestLocationFields(List<TestResultDetailContainerResponse> propertiesList, Locale locale, TestLocation testLocation, UUID openTestUUID) {
        addGeoLocation(propertiesList, locale, testLocation, openTestUUID);
        addInteger(propertiesList, locale, "gkz_bev", testLocation.getGkzBev());
        addInteger(propertiesList, locale, "gkz_sa", testLocation.getGkzSa());
        addLandCoverString(propertiesList, locale, testLocation.getLandCover());
        addSettlementType(propertiesList, locale, testLocation.getSettlementType());
        addString(propertiesList, locale, "link_name", testLocation.getLinkName());
        addInteger(propertiesList, locale, "link_distance", testLocation.getLinkDistance());
        addInteger(propertiesList, locale, "edge_id", testLocation.getEdgeId());
        addInteger(propertiesList, locale, "link_frc", testLocation.getFrc());
        Optional.ofNullable(testLocation.getLinknet())
                .ifPresent(linknet -> addLinkNetFields(propertiesList, locale, linknet));
        Optional.ofNullable(testLocation.getAdministrativeBoundaries())
                .ifPresent(administrativeBoundaries -> addAdministrativeBoundariesFields(propertiesList, locale, administrativeBoundaries));
    }

    private void addLinkNetFields(List<TestResultDetailContainerResponse> propertiesList, Locale locale, Linknet linknet) {
        addLong(propertiesList, locale, "link_id", linknet.getLinkId());
        addString(propertiesList, locale, "link_name1", linknet.getName1());
        addString(propertiesList, locale, "link_name2", linknet.getName2());
    }

    private void addAdministrativeBoundariesFields(List<TestResultDetailContainerResponse> propertiesList, Locale locale, AdministrativeBoundaries administrativeBoundaries) {
        addString(propertiesList, locale, "locality", administrativeBoundaries.getLocality());
        addString(propertiesList, locale, "community", administrativeBoundaries.getCommunity());
        addString(propertiesList, locale, "district", administrativeBoundaries.getDistrict());
        addString(propertiesList, locale, "province", administrativeBoundaries.getProvince());
        addLong(propertiesList, locale, "kg_nr", administrativeBoundaries.getKgNrInt());
    }

    private void addNetworkOperator(List<TestResultDetailContainerResponse> propertiesList, Locale locale, Test test, String mobileProviderShortName) {
        if (Objects.isNull(test.getNetworkOperatorName()))
            addString(propertiesList, locale, "network_operator", mobileProviderShortName);
        else
            addNetworkOperatorString(propertiesList, locale, mobileProviderShortName, test.getNetworkOperatorName());
    }

    private void addBandAndFrequency(List<TestResultDetailContainerResponse> propertiesList, Locale locale, Test test, Integer bandField) {
        Integer channelNumber = null;
        NetworkGroupName technology = null;
        boolean channelNumberChanged = false;
        for (LteFrequencyDto lteFrequencyDto : testRepository.findLteFrequencyByOpenTestUUID(test.getOpenTestUuid())) {
            if (channelNumber == null && !channelNumberChanged) {
                channelNumber = lteFrequencyDto.getChannelNumber();
                technology = lteFrequencyDto.getTechnology();
            } else {
                channelNumberChanged = true;
                channelNumber = null;
            }
        }
        if (channelNumber != null) {
            BandCalculationUtil.FrequencyInformation fi = BandCalculationUtil.getFrequencyInformationFromChannelNumberAndTechnology(channelNumber, technology);
            if (fi != null) {
                addDoubleAndUnitString(propertiesList, locale, "frequency_dl", fi.getFrequencyDL(), "RESULT_FREQUENCY_UNIT");
                if (fi.getInformalName() != null) {
                    addParenthesesString(propertiesList, locale, "radio_band", fi.getBand(), fi.getInformalName());
                } else {
                    addInteger(propertiesList, locale, "radio_band", bandField);
                }
            } else {
                addInteger(propertiesList, locale, "radio_band", bandField);
            }
        }
    }

    private void addSeconds(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String title, Long nanos) {
        if (Objects.nonNull(nanos)) {
            addDoubleAndUnitString(propertiesList, locale, title, nanos / Constants.NANOS_TO_SECONDS_MULTIPLICATOR, "RESULT_DURATION_UNIT");
        }
    }

    private void addBytes(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String title, Long value) {
        Optional.ofNullable(value)
                .filter(x -> x > NumberUtils.LONG_ZERO)
                .map(x -> x / (Constants.BYTES_UNIT_CONVERSION_MULTIPLICATOR * Constants.BYTES_UNIT_CONVERSION_MULTIPLICATOR))
                .ifPresent(x -> addDoubleAndUnitString(propertiesList, locale, title, x, "RESULT_TOTAL_BYTES_UNIT"));
    }

    private void addSettlementType(List<TestResultDetailContainerResponse> propertiesList, Locale locale, Integer settlementType) {
        if (Objects.nonNull(settlementType)) {
            switch (settlementType) {
                case 1:
                    // No settlement area
                    addParenthesesString(propertiesList, locale, "settlement_type", settlementType, getStringFromBundle("value_no_settlement_area", locale));
                    break;
                case 2:
                    // Habitable area
                    addParenthesesString(propertiesList, locale, "settlement_type", settlementType, getStringFromBundle("value_habitable_area", locale));
                    break;
                case 3:
                    // Settlement area
                    addParenthesesString(propertiesList, locale, "settlement_type", settlementType, getStringFromBundle("value_settlement_area", locale));
                    break;
            }
        }
    }

    private void addNetworkOperatorString(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String firstValue, String secondValue) {
        addString(propertiesList, locale, "network_operator", String.format(Constants.PARENTHESES_TEMPLATE, firstValue, secondValue));
    }

    private void addParenthesesString(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String title, Integer firstValue, String secondValue) {
        addString(propertiesList, locale, title, String.format(Constants.PARENTHESES_TEMPLATE, firstValue, secondValue));
    }

    private void addNdtFields(List<TestResultDetailContainerResponse> propertiesList, Locale locale, TestNdt testNdt) {
        Optional.ofNullable(testNdt)
                .ifPresent(testNdt1 -> {
                    addDoubleAndUnitString(propertiesList, locale, "speed_download_ndt", testNdt.getS2cspd(), "RESULT_DOWNLOAD_UNIT");
                    addDoubleAndUnitString(propertiesList, locale, "speed_upload_ndt", testNdt.getC2sspd(), "RESULT_UPLOAD_UNIT");
                    addString(propertiesList, locale, "ndt_details_main", testNdt.getMain());
                    addString(propertiesList, locale, "ndt_details_stat", testNdt.getStat());
                    addString(propertiesList, locale, "ndt_details_diag", testNdt.getDiag());
                });
    }

    private void addString(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String key, String value) {
        if (value != null && !value.isEmpty()) {
            TestResultDetailContainerResponse newItem = TestResultDetailContainerResponse.builder()
                    .title(getStringFromBundleWithKeyPrefix(key, locale))
                    .value(value)
                    .build();
            propertiesList.add(newItem);
        }
    }

    private void addLong(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String key, Long value) {
        if (value != null) {
            TestResultDetailContainerResponse newItem = TestResultDetailContainerResponse.builder()
                    .title(getStringFromBundleWithKeyPrefix(key, locale))
                    .value(value.toString())
                    .build();
            propertiesList.add(newItem);
        }
    }

    private void addInteger(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String key, Integer value) {
        if (value != null) {
            TestResultDetailContainerResponse newItem = TestResultDetailContainerResponse.builder()
                    .title(getStringFromBundleWithKeyPrefix(key, locale))
                    .value(value.toString())
                    .build();
            propertiesList.add(newItem);
        }
    }

    private void addLongAndUnitString(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String title, Long value, String unitKey) {
        String unit = getStringFromBundle(unitKey, locale);
        if (Objects.nonNull(value)) {
            addString(propertiesList, locale, title, FormatUtils.formatValueAndUnit(value, unit));
        }
    }

    private void addDoubleAndUnitString(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String title, Double value, String unitKey) {
        String unit = getStringFromBundle(unitKey, locale);
        if (Objects.nonNull(value)) {
            addString(propertiesList, locale, title, FormatUtils.formatValueAndUnit(value, unit, locale));
        }
    }

    private void addIntegerAndUnitString(List<TestResultDetailContainerResponse> propertiesList, Locale locale, String title, Integer value, String unitKey) {
        String unit = getStringFromBundle(unitKey, locale);
        if (Objects.nonNull(value)) {
            addString(propertiesList, locale, title, FormatUtils.formatValueAndUnit(value, unit));
        }
    }

    private void addLandCoverString(List<TestResultDetailContainerResponse> propertiesList, Locale locale, Integer value) {
        if (Objects.nonNull(value)) {
            addParenthesesString(propertiesList, locale, "land_cover", value, getStringFromBundle("value_corine_" + value, locale));
        }
    }

    private String getStringFromBundle(String value, Locale locale) {
        try {
            return messageSource.getMessage(value, null, locale);
        } catch (final MissingResourceException e) {
            return value;
        }
    }

    private String getStringFromBundleWithKeyPrefix(String key, Locale locale) {
        try {
            return messageSource.getMessage("key_" + key, null, locale);
        } catch (final MissingResourceException e) {
            return key;
        }
    }
}
