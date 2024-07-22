package at.rtr.rmbt.constant;

import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.enums.TestStatus;

import java.util.List;

public interface Config {
    String SIGNAL_RESULT_URL_KEY = "signal_result_url";
    String TEST_RESULT_QOS_URL_KEY = "test_result_qos_url";
    String TEST_RESULT_URL_KEY = "test_result_url";
    String TEST_DURATION_KEY = "rmbt_duration";
    String TEST_NUM_PINGS_KEY = "rmbt_num_pings";
    String TEST_NUM_THREADS_KEY = "rmbt_num_threads";
    String MAP_SERVER_HOST_KEY = "host_map_server";
    String MAP_SERVER_SSL_KEY = "ssl_map_server";
    String MAP_SERVER_PORT_KEY = "port_map_server";
    String TERM_AND_CONDITION_VERSION_KEY = "tc_version";
    String TERM_AND_CONDITION_VERSION_ANDROID_KEY = "tc_version_android";
    String TERM_AND_CONDITION_VERSION_IOS_KEY = "tc_version_ios";
    String TERM_AND_CONDITION_URL_KEY = "tc_url";
    String TERM_AND_CONDITION_URL_IOS_KEY = "tc_url_ios";
    String TERM_AND_CONDITION_URL_ANDROID_KEY = "tc_url_android";
    String TERM_AND_CONDITION_NDT_URL_KEY = "tc_ndt_url_android";
    String URL_OPEN_DATA_PREFIX_KEY = "url_open_data_prefix";
    String URL_SHARE_KEY = "url_share";
    String URL_STATISTIC_KEY = "url_statistics";
    String URL_CONTROL_IPV4_ONLY_KEY = "control_ipv4_only";
    String URL_CONTROL_IPV6_ONLY_KEY = "control_ipv6_only";
    String URL_IPV4_CHECK_KEY = "url_ipv4_check";
    String URL_IPV6_CHECK_KEY = "url_ipv6_check";
    String URL_MAP_SERVER_KEY = "url_map_server";
    String URL_STATISTIC_SERVER_KEY = "url_statistic_server";
    String SYSTEM_UUID_KEY = "system_UUID";
    String GEO_PROVIDER_MANUAL = "manual";
    String GEO_PROVIDER_GEOCODER = "geocoder";
    String GEO_PROVIDER_GPS = "gps";


    List<String> ADMIN_SETTINGS_KEYS = List.of(SIGNAL_RESULT_URL_KEY,
        TEST_RESULT_QOS_URL_KEY,
        TEST_RESULT_URL_KEY,
        TEST_DURATION_KEY,
        TEST_NUM_PINGS_KEY,
        TEST_NUM_THREADS_KEY,
        MAP_SERVER_HOST_KEY,
        MAP_SERVER_SSL_KEY,
        MAP_SERVER_PORT_KEY,
        TERM_AND_CONDITION_VERSION_KEY,
        TERM_AND_CONDITION_VERSION_IOS_KEY,
        TERM_AND_CONDITION_VERSION_ANDROID_KEY,
        TERM_AND_CONDITION_URL_ANDROID_KEY,
        TERM_AND_CONDITION_URL_KEY,
        TERM_AND_CONDITION_URL_IOS_KEY,
        TERM_AND_CONDITION_URL_ANDROID_KEY,
        TERM_AND_CONDITION_NDT_URL_KEY,
        URL_OPEN_DATA_PREFIX_KEY,
        URL_SHARE_KEY,
        URL_STATISTIC_KEY,
        URL_CONTROL_IPV4_ONLY_KEY,
        URL_CONTROL_IPV6_ONLY_KEY,
        URL_IPV4_CHECK_KEY,
        URL_IPV6_CHECK_KEY,
        URL_MAP_SERVER_KEY
    );
    List<String> SETTINGS_KEYS = List.of("tc_url_android",
        "tc_ndt_url_android",
        "tc_version_android",
        "tc_url_android_v4",
        "tc_url_ios",
        "tc_version_ios",
        "tc_version",
        "tc_url",
        "tc_version_desktop",
        "tc_url_desktop",
        "url_open_data_prefix",
        "url_share",
        "url_statistics",
        "control_ipv4_only",
        "control_ipv6_only",
        "url_ipv4_check",
        "url_ipv6_check",
        "url_map_server",
        "host_map_server",
        "ssl_map_server",
        "port_map_server",
        "url_statistic_server"
    );
    List<ServerType> SERVER_TEST_SERVER_TYPES = List.of(ServerType.RMBT);
    List<ServerType> SERVER_HTTP_TEST_SERVER_TYPES = List.of(ServerType.RMBT, ServerType.RMBThttp);
    List<ServerType> SERVER_WS_TEST_SERVER_TYPES = List.of(ServerType.RMBTws, ServerType.RMBThttp);
    List<ServerType> SERVER_EL_TEST_SERVER_TYPES = List.of(ServerType.RMBThttp);
    List<ServerType> SERVER_QOS_TEST_SERVER_TYPES = List.of(ServerType.QoS);
    List<String> SIGNAL_RESULT_STATUSES = List.of(TestStatus.SIGNAL_STARTED.toString(), TestStatus.SIGNAL.toString());
    List<String> TEST_RESULT_DETAIL_STATUSES = List.of(TestStatus.FINISHED.toString());
    List<String> TEST_RESULT_STATUSES = List.of(TestStatus.FINISHED.toString());
    List<String> TEST_RESULT_STATUSES_INCLUDE_ERROR = List.of(TestStatus.FINISHED.toString(), TestStatus.ERROR.toString());
    Integer SIGNIFICANT_PLACES = 2;
}
