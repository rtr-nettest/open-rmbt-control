package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.dto.LteFrequencyDto;
import at.rtr.rmbt.dto.TestDistance;
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
import static org.mockito.Mockito.when;

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
    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private TestResponse testResponse;
    @Mock
    private TestResultDetailRequest testResultDetailRequest;

    private final ApplicationProperties applicationProperties = new ApplicationProperties(
            new ApplicationProperties.LanguageProperties(Set.of("en", "de"), "en"),
            Set.of("RMBT", "RMBTjs", "Open-RMBT", "RMBTws", "HW-PROBE"),
            "1.2",
            1,
            2,
            3,
            10000
    );

    @Before
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath:SystemMessages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        messageSource = reloadableResourceBundleMessageSource;
        testService = new TestServiceImpl(testRepository, testMapper, applicationProperties, geoAnalyticsService, messageSource);
    }

    @Test
    public void getDeviceHistory_whenCommonData_expectDevicesNameList() {
        var devices = Lists.newArrayList(TestConstants.DEFAULT_HISTORY_DEVICE);
        when(testRepository.getDistinctModelByClientId(TestConstants.DEFAULT_UID)).thenReturn(devices);

        var response = testService.getDeviceHistory(TestConstants.DEFAULT_UID);

        assertEquals(devices, response);
    }

    @Test
    public void getDeviceHistory_whenUnknownDevice_expectDevicesNameList() {
        List<String> devices = new ArrayList<>();
        devices.add(null);

        when(testRepository.getDistinctModelByClientId(TestConstants.DEFAULT_UID)).thenReturn(devices);

        var response = testService.getDeviceHistory(TestConstants.DEFAULT_UID);

        assertEquals(List.of(Constants.UNKNOWN_DEVICE), response);
    }

    @Test
    public void getGroupNameByClientId_whenCommonData_expectGroupNameList() {
        when(testRepository.getDistinctGroupNameByClientId(TestConstants.DEFAULT_UID)).thenReturn(List.of(TestConstants.DEFAULT_HISTORY_NETWORK));

        var response = testService.getGroupNameByClientId(TestConstants.DEFAULT_UID);

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
                .roamingType(TestConstants.DEFAULT_TEST_ROAMING_TYPE)
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
                getTestResultDetailContainerResponse("key_network_type", TestConstants.DEFAULT_TEST_RESULT_DETAIL_NETWORK_TYPE_VALUE),
                getTestResultDetailContainerResponse("key_network_sim_operator_name", TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME),
                getTestResultDetailContainerResponse("key_network_sim_operator", TestConstants.DEFAULT_TEST_NETWORK_SIM_OPERATOR),
                getTestResultDetailContainerResponse("key_roaming", TestConstants.DEFAULT_TEST_RESULT_DETAIL_ROAMING_TYPE),
                getTestResultDetailContainerResponse("key_network_operator_name", TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME)
        ));
        return TestResultDetailResponse.builder()
                .testResultDetailContainerResponse(properties)
                .build();
    }

    private TestResultDetailResponse getTimeTestResultDetailResponse() {
        var timeResponse = TestResultDetailContainerResponse.builder()
                .value(TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME)
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
