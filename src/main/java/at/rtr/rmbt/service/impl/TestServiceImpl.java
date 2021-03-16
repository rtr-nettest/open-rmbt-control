package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.constant.ErrorMessage;
import at.rtr.rmbt.dto.LteFrequencyDto;
import at.rtr.rmbt.enums.NetworkGroupName;
import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.enums.TestPlatform;
import at.rtr.rmbt.exception.TestNotFoundException;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.request.TestResultDetailRequest;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.response.TestResultDetailContainerResponse;
import at.rtr.rmbt.response.TestResultDetailResponse;
import at.rtr.rmbt.service.GeoAnalyticsService;
import at.rtr.rmbt.service.TestService;
import at.rtr.rmbt.utils.BandCalculationUtil;
import at.rtr.rmbt.utils.FormatUtils;
import at.rtr.rmbt.utils.HelperFunctions;
import at.rtr.rmbt.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;
    private final ApplicationProperties applicationProperties;
    private final GeoAnalyticsService geoAnalyticsService;
    private final MessageSource messageSource;

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
    public List<String> getDeviceHistory(Long clientId) {
        var resultList = testRepository.getDistinctModelByClientId(clientId);
        resultList.replaceAll(t -> Objects.isNull(t) ? Constants.UNKNOWN_DEVICE : t);
        return resultList;
    }

    @Override
    public List<String> getGroupNameByClientId(Long clientId) {
        return testRepository.getDistinctGroupNameByClientId(clientId);
    }

    @Override
    public TestResponse getTestByUUID(UUID testUUID) {
        return testRepository.findByUuid(testUUID)
                .map(testMapper::testToTestResponse)
                .orElseThrow(() -> new TestNotFoundException(String.format(ErrorMessage.TEST_NOT_FOUND, testUUID)));
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

    private void addGeoLocation(List<TestResultDetailContainerResponse> propertiesList, Locale locale, TestLocation testLocation, UUID openTestUUID) {
        Double latField = testLocation.getGeoLat();
        Double longField = testLocation.getGeoLong();
        Double accuracyField = testLocation.getGeoAccuracy();
        String providerField = testLocation.getGeoProvider();
        if (Objects.nonNull(latField) && Objects.nonNull(longField) && Objects.nonNull(accuracyField)) {
            final double accuracy = accuracyField;
            if (accuracy < applicationProperties.getAccuracyDetailLimit()) {
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
                geoString.append(String.format(Locale.US, "+/- %.0f m", accuracy));
                geoString.append(")");
                addString(propertiesList, locale, "location", geoString.toString());

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
                .value(getTimeValue(date, timeZone, locale))
                .build();
    }

    private String getTimezoneValue(TimeZone timezone, long time, Locale locale) {
        Format timeZoneFormat = new DecimalFormat(Constants.TIMEZONE_PATTERN, new DecimalFormatSymbols(locale));
        double offset = timezone.getOffset(time) / Constants.MILLISECONDS_TO_HOURS;
        return String.format(Constants.TIMEZONE_TEMPLATE, timeZoneFormat.format(offset));
    }

    private String getTimeValue(Date date, TimeZone timeZone, Locale locale) {
        final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM, locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    private void addTestFields(List<TestResultDetailContainerResponse> propertiesList, Locale locale, Test test) {
        if (Objects.nonNull(test.getDualSim()) && !test.getDualSim()) {
            addIntegerAndUnitString(propertiesList, locale, "signal_strength", test.getSignalStrength(), "RESULT_SIGNAL_UNIT");
            addIntegerAndUnitString(propertiesList, locale, "signal_rsrp", test.getLteRsrp(), "RESULT_SIGNAL_UNIT");
            addIntegerAndUnitString(propertiesList, locale, "signal_rsrq", test.getLteRsrq(), "RESULT_DB_UNIT");
            Optional.ofNullable(test.getNetworkType())
                    .map(HelperFunctions::getNetworkTypeName)
                    .ifPresent(networkType -> addString(propertiesList, locale, "network_type", networkType));
            addString(propertiesList, locale, "network_sim_operator_name", test.getNetworkSimOperatorName());
            addString(propertiesList, locale, "network_sim_operator", test.getNetworkSimOperator());
            addString(propertiesList, locale, "roaming", HelperFunctions.getRoamingType(messageSource, test.getRoamingType(), locale));
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
                .map(x -> x / Constants.PING_CONVERSION_MULTIPLICATOR)
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
