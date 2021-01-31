package at.rtr.rmbt.constant;

import at.rtr.rmbt.model.enums.ServerType;

import java.util.List;

public interface Config {
    List<String> SETTINGS_KEYS = List.of("tc_url_android",
            "tc_ndt_url_android",
            "tc_version_android",
            "tc_url_android_v4",
            "tc_url_ios",
            "tc_version_ios",
            "tc_version",
            "tc_url",
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
            "port_map_server"
    );
    List<String> SUPPORTED_CLIENT_NAMES = List.of("RMBT",
            "RMBTjs",
            "Open-RMBT",
            "Open-RMBT",
            "Open-RMBT",
            "RMBTws",
            "HW-PROBE");

    List<ServerType> SERVER_TEST_SERVER_TYPES = List.of(ServerType.RMBT);
    List<ServerType> SERVER_HTTP_TEST_SERVER_TYPES = List.of(ServerType.RMBT, ServerType.RMBThttp);
    List<ServerType> SERVER_WS_TEST_SERVER_TYPES = List.of(ServerType.RMBTws, ServerType.RMBThttp);
    List<ServerType> SERVER_QOS_TEST_SERVER_TYPES = List.of(ServerType.QoS);
}
