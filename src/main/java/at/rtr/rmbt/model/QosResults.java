package at.rtr.rmbt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QosResults {

    @JsonProperty(value = "evaluate")
    private String evaluate;

    @JsonProperty(value = "on_failure")
    private String onFailure;

    @JsonProperty(value = "on_success")
    private String onSuccess;

    @JsonProperty(value = "operator")
    private String operator;

    @JsonProperty(value = "nontransproxy_result_response")
    private String nontransproxyResultResponse;

    @JsonProperty(value = "tcp_result_out")
    private String tcpResultOut;

    @JsonProperty(value = "udp_result_out_response_num_packets")
    private String udpResultOutResponseNumPackets;

    @JsonProperty(value = "dns_result_entries_found")
    private String dnsResultEntriesFound;

    @JsonProperty(value = "dns_result_info")
    private String dnsResultInfo;

    @JsonProperty(value = "traceroute_result_status")
    private String tracerouteResultStatus;

    @JsonProperty(value = "tcp_result_in")
    private String tcpResultIn;

    @JsonProperty(value = "website_result_status")
    private String websiteResultStatus;

    @JsonProperty(value = "http_result_hash")
    private String httpResultHash;

    @JsonProperty(value = "udp_result_in_response_num_packets")
    private String udpResultInResponseNumPackets;
}
