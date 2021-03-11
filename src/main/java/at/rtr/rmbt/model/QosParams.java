package at.rtr.rmbt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QosParams {

    private String port;

    private String request;

    private String timeout;

    private String url;

    @JsonProperty(value = "out_num_packets")
    private String outNumPackets;

    @JsonProperty(value = "out_port")
    private String outPort;

    @JsonProperty(value = "download_timeout")
    private String downloadTimeout;

    @JsonProperty(value = "conn_timeout")
    private String connTimeout;

    @JsonProperty(value = "record")
    private String record;

    @JsonProperty(value = "host")
    private String host;

    @JsonProperty(value = "call_duration")
    private String callDuration;

    @JsonProperty(value = "in_port")
    private String inPort;

    @JsonProperty(value = "resolver")
    private String resolver;

    @JsonProperty(value = "range")
    private String range;

    @JsonProperty(value = "in_num_packets")
    private String inNumPackets;
}
