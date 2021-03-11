package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class QosParamsResponse {

    @JsonProperty(value = "qos_test_uid")
    private final Long qosTestUid;

    @JsonProperty(value = "concurrency_group")
    private final Integer concurrencyGroup;

    @JsonProperty(value = "server_addr")
    private final String serverAddress;

    @JsonProperty(value = "server_port")
    private final Integer serverPort;

    @JsonProperty(value = "port")
    private final String port;

    @JsonProperty(value = "request")
    private final String request;

    @JsonProperty(value = "timeout")
    private final String timeout;

    @JsonProperty(value = "url")
    private final String url;

    @JsonProperty(value = "out_num_packets")
    private final String outNumPackets;

    @JsonProperty(value = "out_port")
    private final String outPort;

    @JsonProperty(value = "download_timeout")
    private final String downloadTimeout;

    @JsonProperty(value = "conn_timeout")
    private final String connTimeout;

    @JsonProperty(value = "record")
    private final String record;

    @JsonProperty(value = "host")
    private final String host;

    @JsonProperty(value = "call_duration")
    private final String callDuration;

    @JsonProperty(value = "in_port")
    private final String inPort;

    @JsonProperty(value = "resolver")
    private final String resolver;

    @JsonProperty(value = "range")
    private final String range;

    @JsonProperty(value = "in_num_packets")
    private final String inNumPackets;
}
