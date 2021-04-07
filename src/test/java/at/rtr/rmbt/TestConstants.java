package at.rtr.rmbt;

import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.enums.*;
import at.rtr.rmbt.utils.BandCalculationUtil;
import at.rtr.rmbt.utils.FormatUtils;
import at.rtr.rmbt.utils.TimeUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.UUID;

public interface TestConstants {
    String DEFAULT_LANGUAGE = "DEFAULT LANGUAGE";
    String LANGUAGE_EN = "en";
    String LANGUAGE_DE = "de";
    Long DEFAULT_LAST_NEWS_UID = 13L;
    String DEFAULT_PLATFORM = "Android";
    TestPlatform DEFAULT_TEST_PLATFORM = TestPlatform.ANDROID;
    Long DEFAULT_SOFTWARE_VERSION_CODE = 1L;
    UUID DEFAULT_NEWS_UUID = UUID.fromString("8fa5fb1c-5bf6-11eb-ae93-0242ac130002");
    UUID DEFAULT_RADIO_CELL_UUID = UUID.fromString("6fa5fb1c-5bf6-11eb-ae93-0242ac131232");
    UUID DEFAULT_UUID = UUID.fromString("88ab584e-5ef2-11eb-ae93-0242ac130002");
    UUID DEFAULT_TEST_UUID = UUID.fromString("77ab584e-6ef2-11eb-ae93-0242ac130252");
    UUID DEFAULT_TEST_OPEN_UUID = UUID.fromString("04cb9392-846b-11eb-8dcd-0242ac130003");
    UUID DEFAULT_TEST_OPEN_TEST_UUID = UUID.fromString("dd4ac406-8469-11eb-8dcd-0242ac130003");
    UUID DEFAULT_GEO_LOCATION_UUID = UUID.fromString("33ab584e-6ef2-11eb-ae93-0282ac130213");
    UUID DEFAULT_LOOP_UUID = UUID.fromString("2458713e-9362-11eb-a8b3-0242ac130003");
    Long DEFAULT_UID = 2L;
    Long DEFAULT_GEO_LOCATION_UID_FIRST = 7L;
    Long DEFAULT_GEO_LOCATION_UID_SECOND = 8L;
    String DEFAULT_NEWS_TITLE = "DEFAULT NEWS TITLE";
    String DEFAULT_NEWS_TEXT = "DEFAULT NEWS TEXT";
    Long DEFAULT_TEST_SERVER_UID = 5L;
    UUID DEFAULT_TEST_SERVER_UUID = UUID.fromString("f79b66b5-6420-4336-ab6d-69ad4158fa1b");
    String DEFAULT_TEST_SERVER_NAME = "DEFAULT TEST SERVER NAME";
    String DEFAULT_TEST_SERVER_WS_NAME = "DEFAULT TEST SERVER WS NAME";
    String DEFAULT_TEST_SERVER_QOS_NAME = "DEFAULT TEST SERVER QOS NAME";
    String DEFAULT_QOS_TEST_TYPE_DESC_NAME = "DEFAULT QOS TEST TYPE DESC NAME";
    TestType DEFAULT_TEST_TYPE = TestType.DNS;
    Long DEFAULT_TERM_AND_CONDITION_VERSION = 2L;
    Long DEFAULT_TERM_AND_CONDITION_VERSION_IOS = 3L;
    Long DEFAULT_TERM_AND_CONDITION_VERSION_ANDROID = 5L;
    String DEFAULT_TERM_AND_CONDITION_URL = "DEFAULT TERM AND CONDITION URL";
    String DEFAULT_TERM_AND_CONDITION_URL_ANDROID = "DEFAULT_TERM_AND_CONDITION_URL_ANDROID";
    String DEFAULT_TERM_AND_CONDITION_URL_IOS = "DEFAULT_TERM_AND_CONDITION_URL_IOS";
    String DEFAULT_TERM_AND_CONDITION_NDT_URL = "DEFAULT TERM AND CONDITION NDT URL";
    String DEFAULT_URLS_URL_SHARE = "DEFAULT URLS URL SHARE";
    String DEFAULT_URLS_URL_IPV6_CHECK = "DEFAULT URLS IPV6 CHECK";
    String DEFAULT_URLS_CONTROL_IPV4_ONLY = "DEFAULT URLS CONTROL IPV4 ONLY";
    String DEFAULT_URLS_OPEN_DATA_PREFIX = "DEFAULT URLS OPEN DATA PREFIX";
    String DEFAULT_URLS_URL_MAP_SERVER = "DEFAULT URLS URL MAP SERVER";
    String DEFAULT_URLS_URL_IPV4_CHECK = "DEFAULT URLS URL IPV4 CHECK";
    String DEFAULT_URLS_CONTROL_IPV6_ONLY = "DEFAULT URLS CONTROL IPV6 ONLY";
    String DEFAULT_URLS_STATISTICS = "DEFAULT URLS STATISTICS";
    String DEFAULT_SERVER_UUID = "DEFAULT_SERVER_UUID";
    String DEFAULT_SERVER_WS_UUID = "DEFAULT_SERVER_WS_UUID";
    String DEFAULT_SERVER_QOS_UUID = "DEFAULT_SERVER_QOS_UUID";
    String DEFAULT_HISTORY_DEVICE = "DEFAULT HISTORY DEVICE";
    String DEFAULT_HISTORY_NETWORK = "DEFAULT HISTORY NETWORK";
    UUID DEFAULT_CLIENT_UUID = UUID.fromString("9e1ccb36-59e3-11eb-ae93-0242ac130002");
    UUID DEFAULT_CLIENT_UUID_GENERATED = UUID.fromString("b5455118-5af9-11eb-ae93-0242ac130002");
    Long DEFAULT_MAP_SERVER_PORT = 443L;
    String DEFAULT_MAP_SERVER_HOST = "DEFAULT MAP SERVER HOST";
    boolean DEFAULT_FLAG_TRUE = true;
    String DEFAULT_IP_HEADER = "DEFAULT IP HEADER";
    String DEFAULT_CLIENT_NAME = "RMBT";
    String DEFAULT_TEXT = "DEFAULT TEXT";
    OffsetDateTime DEFAULT_OFFSET_DATE_TIME_TIMESTAMP = OffsetDateTime.of(LocalDateTime.of(2021, 1, 20, 7, 32), ZoneOffset.ofHoursMinutes(4, 0));
    String DEFAULT_TC_URL_ANDROID_VALUE = "DEFAULT_TC_URL_ANDROID_VALUE";
    String DEFAULT_TC_NDT_URL_ANDROID_VALUE = "DEFAULT_TC_NDT_URL_ANDROID_VALUE";
    String DEFAULT_TC_VERSION_ANDROID_VALUE = Long.valueOf(5).toString();
    String DEFAULT_TC_URL_ANDROID_V4_VALUE = "DEFAULT_TC_URL_ANDROID_V4_VALUE";
    String DEFAULT_TC_URL_IOS_VALUE = "DEFAULT_TC_URL_IOS_VALUE";
    String DEFAULT_TC_VERSION_IOS_VALUE = Long.valueOf(3).toString();
    String DEFAULT_TC_VERSION_VALUE = Long.valueOf(9).toString();
    String DEFAULT_TC_URL_VALUE = "DEFAULT_TC_URL_VALUE";
    String DEFAULT_ANDROID_PLATFORM = "ANDROID";
    String DEFAULT_IOS_PLATFORM = "IOS";
    String DEFAULT_CLIENT_TYPE_NAME = "DEFAULT_CLIENT_TYPE_NAME";
    String DEFAULT_NEWS_TITLE_DE = "DEFAULT_NEWS_TITLE_DE";
    String DEFAULT_NEWS_TEXT_DE = "DEFAULT_NEWS_TEXT_DE";
    String DEFAULT_NEWS_TITLE_EN = "DEFAULT_NEWS_TITLE_EN";
    String DEFAULT_NEWS_TEXT_EN = "DEFAULT_NEWS_TEXT_EN";
    String DEFAULT_INSTANT_EXPECTED = "2021-12-03T10:15:30.00Z";
    Long DEFAULT_MAX_SOFTWARE_VERSION = 5L;
    Long DEFAULT_MIN_SOFTWARE_VERSION = 3L;
    String DEFAULT_SETTINGS_KEY = "tc_url";
    Long DEFAULT_TIME = 110000L;
    String DEFAULT_TIMEZONE = "Europe/Bratislava";
    String DEFAULT_PROVIDER = "DEFAULT_PROVIDER";
    String DEFAULT_COUNTRY_LOCATION = "DEFAULT_COUNTRY_LOCATION";
    Integer DEFAULT_GKZ_BEV = 8;
    Integer DEFAULT_GKZ_SA = 13;
    Integer DEFAULT_LAND_COVER = 100;
    Integer DEFAULT_SETTLEMENT_TYPE = 3;
    String DEFAULT_IP_V4 = "37.57.0.215";
    String DEFAULT_IP_V6 = "2dfc:0:0:0:0217:cbff:fe8c:0";
    String DEFAULT_RESULT_URL = "DEFAULT_RESULT_URL";
    String DEFAULT_URL = "DEFAULT_RESULT_URL";
    Long DEFAULT_TIME_INSTANT = 1615164630L;
    ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.now();
    ZonedDateTime DEFAULT_ZONED_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(DEFAULT_TIME_INSTANT), ZoneId.of(DEFAULT_TIMEZONE));
    Date DEFAULT_DATE_TIME = Date.from(Instant.ofEpochMilli(DEFAULT_TIME_INSTANT));
    ZonedDateTime DEFAULT_TEST_TIME = ZonedDateTime.of(LocalDateTime.of(2021, 1, 20, 7, 32), ZoneId.of("UTC"));
    ZonedDateTime DEFAULT_SIGNAL_TIME = ZonedDateTime.of(LocalDateTime.of(2021, 1, 20, 7, 32), ZoneId.of("UTC"));
    String DEFAULT_TEST_SERVER_WEB_ADDRESS = "DEFAULT_TEST_SERVER_WEB_ADDRESS";
    Integer DEFAULT_TEST_SERVER_PORT = 443;
    Integer DEFAULT_TEST_SERVER_PORT_SSL = 443;
    String DEFAULT_TEST_SERVER_CITY = "DEFAULT_TEST_SERVER_CITY";
    String DEFAULT_TEST_SERVER_COUNTRY = "DEFAULT_TEST_SERVER_COUNTRY";
    Double DEFAULT_LATITUDE = 56.9;
    Double DEFAULT_LONGITUDE = 87.3;
    Double DEFAULT_ALTITUDE = 33.3;
    String DEFAULT_SIGNAL_STRENGTH_ALTITUDE_RESPONSE = FormatUtils.format(Constants.SIGNAL_STRENGTH_ALTITUDE_TEMPLATE, DEFAULT_ALTITUDE);
    Double DEFAULT_BEARING = 17.3;
    String DEFAULT_SIGNAL_STRENGTH_BEARING_RESPONSE = FormatUtils.format(Constants.SIGNAL_STRENGTH_BEARING_TEMPLATE, DEFAULT_BEARING);
    Double DEFAULT_LATITUDE_SECOND = 67.9;
    Double DEFAULT_LONGITUDE_SECOND = 77.3;
    Point DEFAULT_LOCATION = new GeometryFactory().createPoint(new Coordinate(DEFAULT_LONGITUDE, DEFAULT_LATITUDE));
    String DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4 = "DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V4";
    String DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6 = "DEFAULT_TEST_SERVER_WEB_ADDRESS_IP_V6";
    ServerType DEFAULT_TEST_SERVER_SERVER_TYPE = ServerType.RMBT;
    Integer DEFAULT_TEST_SERVER_PRIORITY = 5;
    Integer DEFAULT_TEST_SERVER_WEIGHT = 3;
    String DEFAULT_TEST_SERVER_KEY = "DEFAULT_TEST_SERVER_KEY";
    String DEFAULT_TEST_SERVER_NODE = "DEFAULT_TEST_SERVER_NODE";
    String DEFAULT_TEST_OPERATOR = "DEFAULT_TEST_OPERATOR";
    String DEFAULT_TEST_OPERATOR_NAME = "DEFAULT_TEST_OPERATOR_NAME";
    Integer DEFAULT_PAGE = 1;
    Integer DEFAULT_SIZE = 10;
    String DEFAULT_SORT = "uuid,desc";
    String DEFAULT_SORT_PROPERTY = "uuid";
    String DEFAULT_NETWORK_NAME = "2G (GSM)";
    Integer DEFAULT_NETWORK_ID = 16;
    Long DEFAULT_MILLIS = 1613064639000L;
    Long DEFAULT_TIME_NS = 385093800L;
    Long DEFAULT_TIME_NS_LAST = 415093800L;
    Double DEFAULT_ACCURACY_FIRST = 19.0;
    String DEFAULT_SIGNAL_STRENGTH_ACCURACY_RESPONSE = FormatUtils.format(Constants.SIGNAL_STRENGTH_ACCURACY_TEMPLATE, DEFAULT_ACCURACY_FIRST);
    Double DEFAULT_ACCURACY_SECOND = 20.0;
    Double DEFAULT_SPEED = 26.0;
    String DEFAULT_SIGNAL_STRENGTH_SPEED_RESPONSE = FormatUtils.format(Constants.SIGNAL_STRENGTH_SPEED_TEMPLATE, DEFAULT_SPEED);
    Long DEFAULT_MNC = 5L;
    Long DEFAULT_MCC = 1L;
    String DEFAULT_MCC_MNC = "1-05";
    String DEFAULT_COUNTRY_AT = "AT";
    String DEFAULT_COUNTRY_NO = "NO";
    Integer DEFAULT_LOCATION_ID = 4;
    Long DEFAULT_LOCATION_ID_LONG = 4L;
    Integer DEFAULT_AREA_CODE_FIRST = 3;
    Integer DEFAULT_AREA_CODE_SECOND = 65;
    Integer DEFAULT_PRIMARY_SCRAMBLING_CODE = 2147483647;
    NetworkGroupName DEFAULT_TECHNOLOGY_FIRST = NetworkGroupName.G2;
    NetworkGroupName DEFAULT_TECHNOLOGY_SECOND = NetworkGroupName.G3;
    Integer DEFAULT_CHANNEL_NUMBER_FIRST = 45;
    Integer DEFAULT_CHANNEL_NUMBER_SECOND = 1000;
    Double DEFAULT_FREQUENCY = BandCalculationUtil.getBandFromArfcn(DEFAULT_CHANNEL_NUMBER_FIRST).getFrequencyDL();
    Integer DEFAULT_BAND = BandCalculationUtil.getBandFromArfcn(DEFAULT_CHANNEL_NUMBER_FIRST).getBand();
    Double DEFAULT_SIGNAL_STRENGTH_TIME = TimeUtils.getDiffInSecondsFromTwoZonedDateTime(DEFAULT_TEST_TIME, DEFAULT_SIGNAL_TIME);
    Integer DEFAULT_NETWORK_TYPE_ID = 15;
    Integer DEFAULT_NETWORK_TYPE_WLAN_ID = 99;
    Integer DEFAULT_BIT_ERROR_RATE = 222;
    Integer DEFAULT_WIFI_LINK_SPEED_FIRST = 100;
    Integer DEFAULT_WIFI_LINK_SPEED_SECOND = 100;
    Integer DEFAULT_WIFI_RSSI_FIRST = -105;
    Integer DEFAULT_WIFI_RSSI_SECOND = -102;
    Integer DEFAULT_LTE_CQI_FIRST = 2;
    Integer DEFAULT_LTE_RSSNR = 5;
    Integer DEFAULT_LTE_RSRP_FIRST = -5;
    Integer DEFAULT_LTE_RSRP_SECOND = -4;
    Integer DEFAULT_LTE_RSRQ_FIRST = -11;
    Integer DEFAULT_LTE_RSRQ_SECOND = -12;
    Integer DEFAULT_SIGNAL_STRENGTH_FIRST = -111;
    Integer DEFAULT_SIGNAL_STRENGTH_SECOND = -90;
    Integer DEFAULT_GSM_BIT_ERROR_RATE = 10;
    Long DEFAULT_PING_VALUE = 1111L;
    Long DEFAULT_PING_VALUE_SERVER = 2222L;
    Long DEFAULT_SIGNAL_ADVANCE = 20L;
    Integer DEFAULT_TIMING_ADVANCE = 32;
    String DEFAULT_SIGNAL_STRENGTH_RESPONSE = String.join(Constants.SIGNAL_STRENGTH_DELIMITER,
            FormatUtils.format(Constants.SIGNAL_STRENGTH_DBM_TEMPLATE, DEFAULT_SIGNAL_STRENGTH_FIRST),
            FormatUtils.format(Constants.SIGNAL_STRENGTH_TIMING_ADVANCE_TEMPLATE, DEFAULT_TIMING_ADVANCE),
            FormatUtils.format(Constants.SIGNAL_STRENGTH_RSRQ_TEMPLATE, DEFAULT_LTE_RSRQ_FIRST));
    String DEFAULT_SIGNAL_TEST_REQUEST_RESULT_URL = "DEFAULT_SIGNAL_TEST_REQUEST_URL";
    String DEFAULT_TEST_REQUEST_RESULT_URL = "DEFAULT_TEST_REQUEST_RESULT_URL";
    String DEFAULT_TEST_REQUEST_RESULT_QOS_URL = "DEFAULT_TEST_REQUEST_RESULT_QOS_URL";
    String DEFAULT_TEST_REQUEST_TEST_DURATION = "DEFAULT_TEST_REQUEST_TEST_DURATION";
    String DEFAULT_TEST_REQUEST_TEST_NUM_THREADS = "DEFAULT_TEST_REQUEST_TEST_NUM_THREADS";
    String DEFAULT_TEST_REQUEST_TEST_NUM_PINGS = "DEFAULT_TEST_REQUEST_TEST_NUM_PINGS";
    String DEFAULT_GIT_BRANCH = "DEFAULT_GIT_BRANCH";
    String DEFAULT_GIT_COMMIT_ID_DESCRIBE = "DEFAULT_GIT_COMMIT_ID_DESCRIBE";
    String DEFAULT_CONTROL_SERVER_VERSION = String.format("%s_%s", DEFAULT_GIT_BRANCH, DEFAULT_GIT_COMMIT_ID_DESCRIBE);
    ZonedDateTime DEFAULT_LAST_TEST_ZONED_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(20000000), ZoneId.of(DEFAULT_TIMEZONE));
    ZonedDateTime DEFAULT_LAST_SUCCESSFUL_TEST_ZONED_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(19000000), ZoneId.of(DEFAULT_TIMEZONE));
    Timestamp DEFAULT_LAST_TEST_TIMESTAMP = Timestamp.valueOf(DEFAULT_LAST_TEST_ZONED_DATE_TIME.toLocalDateTime());
    Timestamp DEFAULT_LAST_SUCCESSFUL_TEST_TIMESTAMP = Timestamp.valueOf(DEFAULT_LAST_SUCCESSFUL_TEST_ZONED_DATE_TIME.toLocalDateTime());
    Integer DEFAULT_CONCURRENCY_GROUP = 5;
    String DEFAULT_QOS_PARAM_PORT = "DEFAULT_QOS_PARAM_PORT";
    String DEFAULT_QOS_PARAM_REQUEST = "DEFAULT_QOS_PARAM_REQUEST";
    String DEFAULT_QOS_PARAM_TIMEOUT = "DEFAULT_QOS_PARAM_TIMEOUT";
    String DEFAULT_QOS_PARAM_URL = "DEFAULT_QOS_PARAM_URL";
    String DEFAULT_QOS_PARAM_OUT_NUM_PACKETS = "DEFAULT_QOS_PARAM_OUT_NUM_PACKETS";
    String DEFAULT_QOS_PARAM_OUT_PORT = "DEFAULT_QOS_PARAM_OUT_PORT";
    String DEFAULT_QOS_PARAM_DOWNLOAD_TIMEOUT = "DEFAULT_QOS_PARAM_DOWNLOAD_TIMEOUT";
    String DEFAULT_QOS_PARAM_CONN_TIMEOUT = "DEFAULT_QOS_PARAM_CONN_TIMEOUT";
    String DEFAULT_QOS_PARAM_RECORD = "DEFAULT_QOS_PARAM_RECORD";
    String DEFAULT_QOS_PARAM_HOST = "DEFAULT_QOS_PARAM_HOST";
    String DEFAULT_QOS_PARAM_CALL_DURATION = "DEFAULT_QOS_PARAM_CALL_DURATION";
    String DEFAULT_QOS_PARAM_IN_PORT = "DEFAULT_QOS_PARAM_IN_PORT";
    String DEFAULT_QOS_PARAM_RESOLVER = "DEFAULT_QOS_PARAM_RESOLVER";
    String DEFAULT_QOS_PARAM_RANGE = "DEFAULT_QOS_PARAM_RANGE";
    String DEFAULT_QOS_PARAM_IN_NUM_PACKETS = "DEFAULT_QOS_PARAM_IN_NUM_PACKETS";
    String DEFAULT_TEST_RESULT_DETAIL_OPEN_UUID = "P04cb9392-846b-11eb-8dcd-0242ac130003";
    String DEFAULT_TEST_RESULT_DETAIL_OPEN_UUID_TITLE = "Open User ID";
    String DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID = "Odd4ac406-8469-11eb-8dcd-0242ac130003";
    String DEFAULT_TEST_RESULT_DETAIL_OPEN_TEST_UUID_TITLE = "Open Test ID";
    Long DEFAULT_SPEED_ITEM_BYTES_FIRST = 1000L;
    Long DEFAULT_SPEED_ITEM_BYTES_SECOND = 2000L;
    Long DEFAULT_SPEED_ITEM_BYTES_THIRD = 3000L;
    Long DEFAULT_SPEED_ITEM_TIME_FIRST = 155000L;
    Long DEFAULT_SPEED_ITEM_TIME_SECOND = 255000L;
    Long DEFAULT_SPEED_ITEM_TIME_THIRD = 355000L;
    Long DEFAULT_SPEED_ITEM_THREAD_FIRST = 1L;
    Long DEFAULT_SPEED_ITEM_THREAD_SECOND = 1L;
    Long DEFAULT_SPEED_ITEM_THREAD_THIRD = 0L;
    SpeedDirection DEFAULT_SPEED_DIRECTION_FIRST = SpeedDirection.UPLOAD;
    SpeedDirection DEFAULT_SPEED_DIRECTION_SECOND = SpeedDirection.UPLOAD;
    SpeedDirection DEFAULT_SPEED_DIRECTION_THIRD = SpeedDirection.DOWNLOAD;
    String DEFAULT_TEST_TOKEN = String.join("_", DEFAULT_TEST_UUID.toString(), DEFAULT_TEXT);
    String DEFAULT_CLIENT_VERSION = "0.1";
    Integer DEFAULT_RESULT_DOWNLOAD_SPEED = 2122000;
    Integer DEFAULT_RESULT_UPLOAD_SPEED = 1020032;
    Long DEFAULT_RESULT_PING_SHORTEST = 3000L;
    String DEFAULT_OS_VERSION = "DEFAULT_OS_VERSION";
    String DEFAULT_API_LEVEL = "DEFAULT_API_LEVEL";
    String DEFAULT_DEVICE = "DEFAULT_DEVICE";
    String DEFAULT_MODEL = "DEFAULT_MODEL";
    String DEFAULT_PRODUCT = "DEFAULT_PRODUCT";
    Integer DEFAULT_TELEPHONY_PHONE_TYPE = 5;
    Integer DEFAULT_TELEPHONY_DATA_STATE = 22;
    String DEFAULT_TELEPHONY_NETWORK_COUNTRY = "DEFAULT_TELEPHONY_NETWORK_COUNTRY";
    String DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME = "DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME";
    String DEFAULT_TELEPHONY_NETWORK_OPERATOR = "DEFAULT_TELEPHONY_NETWORK_OPERATOR";
    String DEFAULT_TELEPHONY_NETWORK_SIM_COUNTRY = "DEFAULT_TELEPHONY_NETWORK_SIM_COUNTRY";
    String DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME = "DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME";
    String DEFAULT_TEST_NETWORK_SIM_OPERATOR = "DEFAULT_TEST_NETWORK_SIM_OPERATOR";
    String DEFAULT_WIFI_SSID = "DEFAULT_WIFI_SSID";
    String DEFAULT_WIFI_BSSID = "DEFAULT_WIFI_BSSID";
    String DEFAULT_WIFI_NETWORK_ID = "DEFAULT_WIFI_NETWORK_ID";
    String DEFAULT_CLIENT_SOFTWARE_VERSION = "DEFAULT_CLIENT_SOFTWARE_VERSION";
    Boolean DEFAULT_TELEPHONY_NETWORK_IS_ROAMING = Boolean.TRUE;
    String DEFAULT_TEST_ERROR_CAUSE = "DEFAULT_TEST_ERROR_CAUSE";
    Integer DEFAULT_TEST_NUM_THREADS = 3;
    Integer DEFAULT_TEST_NUM_THREADS_UPLOAD = 5;
    Long DEFAULT_TEST_BYTES_DOWNLOAD = 300L;
    Long DEFAULT_TEST_BYTES_UPLOAD = 400L;
    Long DEFAULT_DOWNLOAD_DURATION_NANOS = 17773354667L;
    Long DEFAULT_UPLOAD_DURATION_NANOS = 16573354667L;
    Long DEFAULT_TEST_TOTAL_BYTES_DOWNLOAD = 222222L;
    Long DEFAULT_TEST_TOTAL_BYTES_UPLOAD = 333333L;
    Long DEFAULT_TEST_IF_BYTES_DOWNLOAD = 40000L;
    Long DEFAULT_TEST_IF_BYTES_UPLOAD = 70000L;
    Long DEFAULT_TEST_DL_IF_BYTES_DOWNLOAD = 130000L;
    Long DEFAULT_TEST_DL_IF_BYTES_UPLOAD = 270000L;
    Long DEFAULT_TEST_UL_IF_BYTES_DOWNLOAD = 330000L;
    Long DEFAULT_TEST_UL_IF_BYTES_UPLOAD = 2230000L;
    Long DEFAULT_TEST_PING_MEDIAN = 58420361L;
    String DEFAULT_TEST_COUNTRY_ASN = "DEFAULT_TEST_COUNTRY_ASN";
    String DEFAULT_TEST_GEO_IP = "DEFAULT_TEST_GEO_IP";
    String DEFAULT_TEST_CLIENT_PUBLIC_IP = "DEFAULT_TEST_CLIENT_PUBLIC_IP";
    Long DEFAULT_TEST_PUBLIC_IP_ASN = 23L;
    String DEFAULT_TEST_PUBLIC_IP_AS_NAME = "DEFAULT_TEST_PUBLIC_IP_AS_NAME";
    String DEFAULT_TEST_PUBLIC_IP_RDNS = "DEFAULT_TEST_PUBLIC_IP_RDNS";
    String DEFAULT_TEST_PROVIDER_NAME = "DEFAULT_TEST_PROVIDER_NAME";
    String DEFAULT_TEST_MOBILE_PROVIDER_NAME = "DEFAULT_TEST_MOBILE_PROVIDER_NAME";
    String DEFAULT_TEST_CLIENT_IP_LOCAL_TYPE = "DEFAULT_TEST_CLIENT_IP_LOCAL_TYPE";
    String DEFAULT_TEST_NAT_TYPE = "DEFAULT_TEST_NAT_TYPE";
    Integer DEFAULT_TEST_DURATION = 1000;
    Integer DEFAULT_TEST_SIM_COUNT = 1;
    Long DEFAULT_TIME_DOWNLOAD_OFFSET_NANOS = 2366333L;
    Long DEFAULT_TIME_UPLOAD_OFFSET_NANOS = 4422233L;
    String DEFAULT_TAG = "DEFAULT_TAG";
    Boolean DEFAULT_USER_SERVER_SELECTION = Boolean.TRUE;
    Boolean DEFAULT_DUAL_SIM = Boolean.TRUE;
    Integer DEFAULT_TELEPHONY_SIM_COUNT = 2;
    ClientStatus DEFAULT_LAST_CLIENT_STATUS = ClientStatus.SPEEDTEST_END;
    QosStatus DEFAULT_LAST_QOS_STATUS = QosStatus.QOS_END;
    Integer DEFAULT_TEST_SUBMISSION_RETRY_COUNT = 22;
    String DEFAULT_TEST_RESULT_DETAIL_SIGNAL_STRENGTH_VALUE = DEFAULT_SIGNAL_STRENGTH_FIRST + " dBm";
    String DEFAULT_TEST_RESULT_DETAIL_SIGNAL_RSRP_VALUE = DEFAULT_LTE_RSRP_FIRST + " dBm";
    String DEFAULT_TEST_RESULT_DETAIL_SIGNAL_RSRQ_VALUE = DEFAULT_LTE_RSRQ_FIRST + " dB";
    String DEFAULT_NETWORK_TYPE_VALUE = "3G (HSPA+)";
    String DEFAULT_NETWORK_TYPE_WLAN_VALUE = "WLAN";
    Integer DEFAULT_ROAMING_TYPE_ID = 2;
    String DEFAULT_ROAMING_TYPE_VALUE = "International";
    String DEFAULT_TEST_RESULT_DETAIL_SPEED_DOWNLOAD_VALUE = "2,100 Mbps";
    String DEFAULT_TEST_RESULT_DETAIL_SPEED_UPLOAD_VALUE = "1,000 Mbps";
    String DEFAULT_TEST_RESULT_DETAIL_PING_MEDIAN_VALUE = "58 ms";
    String DEFAULT_TEST_RESULT_DETAIL_WIFI_LINK_SPEED = "100 Mbps";
    String DEFAULT_TEST_RESULT_DETAIL_TOTAL_BYTES = "0.56 MB";
    String DEFAULT_TEST_RESULT_DETAIL_TOTAL_BYTES_IF = "0.11 MB";
    String DEFAULT_TEST_RESULT_DETAIL_TEST_DL_IF_BYTES_DOWNLOAD = "0.13 MB";
    String DEFAULT_TEST_RESULT_DETAIL_TEST_DL_IF_BYTES_UPLOAD = "0.27 MB";
    String DEFAULT_TEST_RESULT_DETAIL_TEST_UL_IF_BYTES_DOWNLOAD = "0.33 MB";
    String DEFAULT_TEST_RESULT_DETAIL_TEST_UL_IF_BYTES_UPLOAD = "2.2 MB";
    String DEFAULT_TEST_RESULT_DETAIL_TIME_DL = "0.002 s";
    String DEFAULT_TEST_RESULT_DETAIL_TIME_UL = "0.004 s";
    String DEFAULT_TEST_RESULT_DETAIL_DURATION_DL = "18 s";
    String DEFAULT_TEST_RESULT_DETAIL_DURATION_UL = "17 s";
    String DEFAULT_TEST_RESULT_DETAIL_DURATION = "1000 s";
    String DEFAULT_TEST_LOCATION_LINK_NAME = "DEFAULT_TEST_LOCATION_LINK_NAME";
    String DEFAULT_TEST_RESULT_DETAIL_LOCATION = "N 56°54.000'  E 87°18.000' (DEFAULT_PROVIDER, +/- 19 m)";
    String DEFAULT_TEST_RESULT_DETAIL_MOTION = "14 m";
    String DEFAULT_TEST_RESULT_DETAIL_GEO_ALTITUDE = "33 m";
    String DEFAULT_TEST_RESULT_DETAIL_GEO_SPEED = "94 km/h";
    String DEFAULT_TEST_RESULT_DETAIL_DTM_LEVEL = "12 m";
    String DEFAULT_TEST_RESULT_DETAIL_LAND_COVER = "100 (Artificial surfaces)";
    String DEFAULT_TEST_RESULT_DETAIL_SETTLEMENT_TYPE = "3 (settlement area)";
    String DEFAULT_TEST_RESULT_DETAIL_SPEED_DOWNLOAD_NDT = "0.23 Mbps";
    String DEFAULT_TEST_RESULT_DETAIL_SPEED_UPLOAD_NDT = "1.2 Mbps";
    String DEFAULT_TEST_RESULT_DETAIL_FREQUENCY_DL = "940 MHz";
    String DEFAULT_TEST_RESULT_DETAIL_RADIO_BAND = "8 (GSM 900)";
    String DEFAULT_TEST_RESULT_DETAIL_TIME_STRING = "Jan 19, 1970, 5:39:24 PM";
    String DEFAULT_TEST_RESULT_DETAIL_TIMEZONE = "UTC+1h";
    Integer DEFAULT_TEST_LOCATION_LINK_DISTANCE = 5;
    Integer DEFAULT_TEST_LOCATION_EDGE_ID = 15;
    Integer DEFAULT_TEST_LOCATION_FRC = 23;
    Integer DEFAULT_TEST_LOCATION_DTM_LEVEL = 12;
    Long DEFAULT_LINKNET_LINK_ID = 17L;
    String DEFAULT_LINKNET_NAME1 = "DEFAULT_LINKNET_NAME1";
    String DEFAULT_LINKNET_NAME2 = "DEFAULT_LINKNET_NAME2";
    String DEFAULT_ADMINISTRATIVE_BOUNDARIES_LOCALITY = "DEFAULT_ADMINISTRATIVE_BOUNDARIES_LOCALITY";
    String DEFAULT_ADMINISTRATIVE_BOUNDARIES_COMMUNITY = "DEFAULT_ADMINISTRATIVE_BOUNDARIES_COMMUNITY";
    String DEFAULT_ADMINISTRATIVE_BOUNDARIES_DISTRICT = "DEFAULT_ADMINISTRATIVE_BOUNDARIES_DISTRICT";
    String DEFAULT_ADMINISTRATIVE_BOUNDARIES_PROVINE = "DEFAULT_ADMINISTRATIVE_BOUNDARIES_PROVINE";
    Long DEFAULT_ADMINISTRATIVE_BOUNDARIES_KG_NR = 8L;
    Double DEFAULT_TEST_DISTANCE_MAX_ACCURACY_FIRST = 1.4d;
    Double DEFAULT_TEST_DISTANCE_MAX_ACCURACY_SECOND = 1.5d;
    Double DEFAULT_TEST_DISTANCE_TOTAL_DISTANCE_FIRST = 14d;
    Double DEFAULT_TEST_DISTANCE_TOTAL_DISTANCE_SECOND = 1.3d;
    Double DEFAULT_TEST_NDT_S2CSPD = 0.23d;
    Double DEFAULT_TEST_NDT_C2SSPD = 1.17d;
    String DEFAULT_TEST_NDT_MAIN = "DEFAULT_TEST_NDT_MAIN";
    String DEFAULT_TEST_NDT_STAT = "DEFAULT_TEST_NDT_STAT";
    String DEFAULT_TEST_NDT_DIAG = "DEFAULT_TEST_NDT_DIAG";
    String DEFAULT_USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36";
    String DEFAULT_REQUEST_URL = "DEFAULT_REQUEST_URL";
    StringBuffer DEFAULT_REQUEST_URL_BUFFER = new StringBuffer(DEFAULT_REQUEST_URL);
    String DEFAULT_USER_AGENT_PRODUCT = "Chrome";
    String DEFAULT_USER_AGENT_VERSION = "89.0.4389.82";
    String DEFAULT_USER_AGENT_CATEGORY = "pc";
    String DEFAULT_USER_AGENT_OS = "Windows 10";
    Integer DEFAULT_CLASSIFICATION_COUNT = 2;
    String DEFAULT_TEST_RESULT_RESPONSE_SHARE_SUBJECT = "RTR-NetTest result - Jan 19, 1970, 5:39:24 PM";
    String DEFAULT_TEST_RESULT_RESPONSE_SHARE_TEXT_DUAL_SIM_TRUE_SIGNAL_STRENGTH_NOT_NULL = "My Result:\n" +
            "Date/time: Jan 19, 1970, 5:39:24 PM\n" +
            "Download: 2,100 Mbps\n" +
            "Upload: 1,000 Mbps\n" +
            "Ping: 58 ms\n" +
            "Signal strength: -111 dBm\n" +
            "Network type: Dual SIM\n" +
            "Platform: \n" +
            "Model: \n" +
            "\n" +
            "\n";
    String DEFAULT_TEST_RESULT_RESPONSE_SHARE_TEXT_DUAL_SIM_FALSE_LTE_RSRP_NOT_NULL = "My Result:\n" +
            "Date/time: Jan 19, 1970, 5:39:24 PM\n" +
            "Download: 2,100 Mbps\n" +
            "Upload: 1,000 Mbps\n" +
            "Ping: 58 ms\n" +
            "Signal strength (RSRP): -5 dBm\n" +
            "Network type: UNKNOWN\n" +
            "Platform: \n" +
            "Model: \n" +
            "\n" +
            "\n";
    Integer DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION = 2;
    Integer DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_STRENGTH_CLASSIFICATION = 1;
    String DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_STRENGTH_TITLE = "Signal";
    String DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_LTE_RSRP_TITLE = "Signal (RSRP)";
    String DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_DOWNLOAD_TITLE = "Download";
    String DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_UPLOAD_TITLE = "Upload";
    String DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_PING_TITLE = "Ping";
    Integer DEFAULT_NEXT_TEST_SLOT = 4;
    String DEFAULT_NET_ITEM_RESPONSE_NETWORK_TYPE_TITLE = "Connection";
    String DEFAULT_NET_ITEM_RESPONSE_WIFI_SSID_TITLE = "WLAN SSID";
    String DEFAULT_NET_ITEM_RESPONSE_OPERATOR_NAME_TITLE = "Operator";
    String DEFAULT_NET_ITEM_RESPONSE_ROAMING_TITLE = "Roaming";
    QoeCategory DEFAULT_QOE_CATEGORY = QoeCategory.CLOUD;
    Integer DEFAULT_QOE_CLASSIFICATION = 4;
    Double DEFAULT_QUALITY = 1d;
    Integer DEFAULT_RESULT_LIMIT = 1;
    Integer DEFAULT_RESULT_OFFSET = 2;
    String DEFAULT_FORMATTED_SPEED_UPLOAD = "1,020.03";
    String DEFAULT_FORMATTED_SPEED_DOWNLOAD = "2,122";
    String DEFAULT_FORMATTED_PING = "58.42";
    String DEFAULT_FORMATTED_PING_SHORTEST = "58.42";
    String DEFAULT_HISTORY_RESPONSE_ITEM_LOOP_UUID = "L2458713e-9362-11eb-a8b3-0242ac130003";
    String DEFAULT_TEST_HISTORY_FINAL_QUERY = "SELECT DISTINCT t.uuid, time, timezone, speed_upload, speed_download, ping_median, lte_rsrp, signal_strength, dual_sim, sim_count, network_type, nt.group_name network_type_group_name, l.loop_uuid loop_uuid, COALESCE(adm.fullname, t.model) model FROM test t LEFT JOIN device_map adm ON adm.codename=t.model LEFT JOIN network_type nt ON t.network_type=nt.uid LEFT JOIN test_loopmode l ON (l.test_uuid = t.uuid) WHERE t.deleted = false AND t.implausible = false AND t.status = 'FINISHED'  AND client_id = 2  AND (COALESCE(adm.fullname, t.model) IN ('DEFAULT_DEVICE'))  AND nt.group_name IN ('2G (GSM)') ORDER BY time DESC  LIMIT 1 OFFSET 2";
    String DEFAULT_TEST_HISTORY_FINAL_QUERY_CLIENT_SYNCED = "SELECT DISTINCT t.uuid, time, timezone, speed_upload, speed_download, ping_median, lte_rsrp, signal_strength, dual_sim, sim_count, network_type, nt.group_name network_type_group_name, l.loop_uuid loop_uuid, COALESCE(adm.fullname, t.model) model FROM test t LEFT JOIN device_map adm ON adm.codename=t.model LEFT JOIN network_type nt ON t.network_type=nt.uid LEFT JOIN test_loopmode l ON (l.test_uuid = t.uuid) WHERE t.deleted = false AND t.implausible = false AND t.status = 'FINISHED'  AND (t.client_id IN (SELECT 2 UNION SELECT uid FROM client WHERE sync_group_id = 5 ))  AND (COALESCE(adm.fullname, t.model) IN ('DEFAULT_DEVICE'))  AND nt.group_name IN ('2G (GSM)') ORDER BY time DESC  LIMIT 1 OFFSET 2";
    Integer DEFAULT_CLIENT_SYNC_GROUP_ID = 5;
    MeasurementType DEFAULT_MEASUREMENT_TYPE_FLAG = MeasurementType.REGULAR;
    String DEFAULT_SYNC_CODE = "DEFAULT_SYNC_CODE";
    Long DEFAULT_CLIENT_UID_BY_SYNC_CODE = 5L;
    Long DEFAULT_CLIENT_UID_BY_CLIENT_UUID = 7L;
    Integer DEFAULT_SYNC_GROUP_UID = 13;
    Integer DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID = 4;
    Integer DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE = 8;
    String DEFAULT_SYNC_SUCCESSFUL_MSG_TEXT = "The synchronisation was successful.";
    String DEFAULT_SYNC_SUCCESSFUL_MSG_TITLE = "Successful";
    String DEFAULT_SYNC_UNKNOWN_CLIENT_MSG_TITLE = "Unknown client";
    String DEFAULT_SYNC_UNKNOWN_CLIENT_MSG_TEXT = "Your client is not registered in our system.";
    String DEFAULT_SYNC_WRONG_CODE_MSG_TITLE = "Wrong code";
    String DEFAULT_SYNC_WRONG_CODE_MSG_TEXT = "The code you entered does not exist.";
    String DEFAULT_SYNC_ALREADY_SYNCHRONIZED_MSG_TITLE = "Already synchronized";
    String DEFAULT_SYNC_ALREADY_SYNCHRONIZED_MSG_TEXT = "The devices are already synchronized with each other.";
    String DEFAULT_SYNC_SAME_DEVICE_MSG_TITLE = "Same device";
    String DEFAULT_SYNC_SAME_DEVICE_MSG_TEXT = "You can not synchronise with the same device.";

    interface Database {
        long CLIENT_TYPE_DESKTOP_UID = 1L;
        long CLIENT_TYPE_MOBILE_UID = 2L;
        UUID TEST1_UUID = UUID.fromString("48ddf306-3f55-4e28-a657-22dd7790921d");
        UUID CLIENT1_UUID = UUID.fromString("13d8667a-ddbc-40c9-95c9-93933087ec30");
        String PROVIDER_A1_TELECOM_SHORT_NAME = "A1 TA";
    }
}
