package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.TestType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class QosSendTestResultItem {
    @ApiModelProperty(notes = "Qos test uid")
    @JsonProperty("qos_test_uid")
    private Long qosTestUid;

    @ApiModelProperty(example = "WEBSITE", notes = "Enum value of one of the type of the QoS test category")
    @JsonProperty("test_type")
    private TestType testType;

    @ApiModelProperty(example = "2628208113", notes = "Duration of the test")
    @JsonProperty("duration_ns")
    private long durationNs;

    @ApiModelProperty(example = "59639502458", notes = "Start time of the test in nanoseconds, relative to start of the entire measurement")
    @JsonProperty("start_time_ns")
    private long startTimeNs;

    @ApiModelProperty(example = "100", notes = "VOIP shortest sequence without any error")
    @JsonProperty("voip_result_out_short_seq")
    private long voipResultOutShortSeq;

    @ApiModelProperty(example = "0", notes = "VOIP longest incomming sequence with error")
    @JsonProperty("voip_result_in_sequence_error")
    private long voipResultInSequenceError;

    @ApiModelProperty(example = "10020000000", notes = "VOIP objective delay to wait in nanoseconds")
    @JsonProperty("voip_objective_delay")
    private long voipObjectiveDelay;

    @ApiModelProperty(example = "8000", notes = "VOIP objective sample rate")
    @JsonProperty("voip_objective_sample_rate")
    private long voipObjectiveSampleRate;

    @ApiModelProperty(example = "100", notes = "VOIP total number of packets emitted")
    @JsonProperty("voip_result_out_num_packets")
    private int voipResultOutNumPackets;

    @ApiModelProperty(example = "100", notes = "VOIP longest errorless sequence")
    @JsonProperty("voip_result_out_long_seq")
    private int voipResultOutLongSeq;

    @ApiModelProperty(example = "8", notes = "VOIP bits per sample")
    @JsonProperty("voip_objective_bits_per_sample")
    private int voipObjectiveBitsPerSample;

    @ApiModelProperty(example = "10212357", notes = "VOIP max jitter out in nanos")
    @JsonProperty("voip_result_out_max_jitter")
    private long voipResultOutMaxJitter;

    @ApiModelProperty(example = "3610909", notes = "VOIP mean jitter out in nanos")
    @JsonProperty("voip_result_out_mean_jitter")
    private long voipResultOutMeanJitter;

    @ApiModelProperty(example = "OK", notes = "VOIP total result status")
    @JsonProperty("voip_result_status")
    private String voipResultStatus;

    @ApiModelProperty(example = "100", notes = "VOIP total number of incomming packets (outcoming and incoming should be equal to test to be successful)")
    @JsonProperty("voip_result_in_num_packets")
    private int voipResultInNumPackets;

    @ApiModelProperty(example = "0", notes = "VOIP longest outgoing sequence with error")
    @JsonProperty("voip_result_out_sequence_error")
    private int voipResultOutSequenceError;

    @ApiModelProperty(example = "4009958", notes = "VOIP max incomming jitter in nanoseconds")
    @JsonProperty("voip_result_in_max_jitter")
    private long voipResultInMaxJitter;

    @ApiModelProperty(example = "-135516126", notes = "VOIP incomming skew in nanoseconds")
    @JsonProperty("voip_result_in_skew")
    private long voipResultInSkew;

    @ApiModelProperty(example = "51375887", notes = "VOIP outgoing max delta in nanoseconds")
    @JsonProperty("voip_result_out_max_delta")
    private long voipResultOutMaxDelta;

    @ApiModelProperty(example = "null", notes = "VOIP input port")
    @JsonProperty("voip_objective_in_port")
    private int voipObjectiveInPort;

    @ApiModelProperty(example = "2000000000", notes = "VOIP total call duration in nanoseconds")
    @JsonProperty("voip_objective_call_duration")
    private long voipObjectiveCallDuration;

    @ApiModelProperty(example = "100", notes = "VOIP shortest valid incomming sequence")
    @JsonProperty("voip_result_in_short_seq")
    private long voipResultInShortSeq;

    @ApiModelProperty(example = "19643375", notes = "VOIP incomming max delta in nanoseconds")
    @JsonProperty("voip_result_in_max_delta")
    private long voipResultInMaxDelta;

    @ApiModelProperty(example = "8", notes = "VOIP payload objective")
    @JsonProperty("voip_objective_payload")
    private int voipObjectivePayload;

    @ApiModelProperty(example = "5060", notes = "VOIP outgoing port")
    @JsonProperty("voip_objective_out_port")
    private int voipObjectiveOutPort;

    @ApiModelProperty(example = "1533211", notes = "VOIP incomming mean jitter in nanoseconds")
    @JsonProperty("voip_result_in_mean_jitter")
    private long voipResultInMeanJitter;

    @ApiModelProperty(example = "100", notes = "VOIP incomming longest errorless sequence")
    @JsonProperty("voip_result_in_long_seq")
    private int voipResultInLongSeq;

    @ApiModelProperty(example = "-181283337", notes = "VOIP outgoing skew in nanoseconds")
    @JsonProperty("voip_result_out_skew")
    private int voipResultOutSkew;

    @ApiModelProperty(example = "30", notes = "max valid hops to count this test results as success")
    @JsonProperty("traceroute_objective_max_hops")
    private int tracerouteObjectiveMaxHops;

    @ApiModelProperty(notes = "Result items with addresses and times which were tested")
    @JsonProperty("traceroute_result_details")
    private List<TracerouteItem> tracerouteResultDetails;

    @ApiModelProperty(example = "35000000000", notes = "Timeout of the test until which is test executed in nanoseconds")
    @JsonProperty("traceroute_objective_timeout")
    private Long tracerouteObjectiveTimeout;

    @ApiModelProperty(notes = "OK if test ended successfully, FAILED if not", example = "OK")
    @JsonProperty("traceroute_result_status")
    private String tracerouteResultStatus;

    @ApiModelProperty(notes = "hops necessary to achieve destination", example = "13")
    @JsonProperty("traceroute_result_hops")
    private Integer tracerouteResultHops;

    @ApiModelProperty(notes = "host to reach in traceroute test", example = "traceroutev4.netztest.at")
    @JsonProperty("traceroute_objective_host")
    private String tracerouteObjectiveHost;

    @ApiModelProperty(notes = "timeout of the test to succeed in nanoseconds", example = "5000000000")
    @JsonProperty("tcp_objective_timeout")
    private Long tcpObjectiveTimeout;

    @ApiModelProperty(notes = "OK if test ended successfully or FAILED if not", example = "FAILED")
    @JsonProperty("tcp_result_in")
    private String tcpResultIn;

    @ApiModelProperty(notes = "in port for tcp connection", example = "8080")
    @JsonProperty("tcp_objective_in_port")
    private Integer tcpObjectiveInPort;

    @ApiModelProperty(notes = "OK if test ended successfully or FAILED if not", example = "FAILED")
    @JsonProperty("tcp_result_out")
    private String tcpResultOut;

    @ApiModelProperty(notes = "response from server to the test", example = "PING")
    @JsonProperty("tcp_result_out_response")
    private String tcpResultOutResponse;

    @ApiModelProperty(notes = "outgoing port for tcp connection", example = "8080")
    @JsonProperty("tcp_objective_out_port")
    private Integer tcpObjectiveOutPort;

    @ApiModelProperty(notes = "delay in nanoseconds to send packets back", example = "3000000000")
    @JsonProperty("udp_objective_delay")
    private Long udpObjectiveDelay;

    @ApiModelProperty(notes = "timeout for test to succeed in nanoseconds", example = "5000000000")
    @JsonProperty("udp_objective_timeout")
    private Long udpObjectiveTimeout;

    @ApiModelProperty(notes = "number of incomming packets via UDP protocol", example = "0")
    @JsonProperty("udp_result_in_num_packets")
    private Integer udpResultInNumPackets;

    @ApiModelProperty(notes = "packet loss rate in percentage", example = "100")
    @JsonProperty("udp_result_in_packet_loss_rate")
    private String udpResultInPacketLossRate;

    @ApiModelProperty(notes = "total number of expected incomming packets", example = "5")
    @JsonProperty("udp_objective_in_num_packets")
    private Integer udpObjectiveInNumPackets;

    @ApiModelProperty(notes = "number of packet in response back to server", example = "0")
    @JsonProperty("udp_result_in_response_num_packets")
    private Integer udpResultInResponseNumPackets;

    @ApiModelProperty(notes = "port to be tested", example = "5004")
    @JsonProperty("udp_objective_in_port")
    private Integer udpObjectiveInPort;

    @ApiModelProperty(notes = "number of outgoing packets via UDP protocol", example = "0")
    @JsonProperty("udp_result_out_num_packets")
    private Integer udpResultOutNumPackets;

    @ApiModelProperty(notes = "packet loss rate in percentage", example = "0")
    @JsonProperty("udp_result_out_packet_loss_rate")
    private String udpResultOutPacketLossRate;

    @ApiModelProperty(notes = "total number of expected outgoing packets", example = "5")
    @JsonProperty("udp_objective_out_num_packets")
    private Integer udpObjectiveOutNumPackets;

    @ApiModelProperty(notes = "number of packet in response back", example = "0")
    @JsonProperty("udp_result_out_response_num_packets")
    private Integer udpResultOutResponseNumPackets;

    @ApiModelProperty(notes = "port to be tested", example = "27005")
    @JsonProperty("udp_objective_out_port")
    private Integer udpObjectiveOutPort;

    @ApiModelProperty(notes = "OK if test succeeds, else FAILED or TIMEOUT", example = "OK")
    @JsonProperty("nontransproxy_result")
    private String nonTransparentProxyResult;

    @ApiModelProperty(notes = "request message", example = "GET")
    @JsonProperty("nontransproxy_objective_request")
    private String nonTransparentProxyObjectiveRequest;

    @ApiModelProperty(notes = "timeout in nanoseconds to test succeed", example = "5000000000")
    @JsonProperty("nontransproxy_objective_timeout")
    private Long nonTransparentProxyObjectiveTimeout;

    @ApiModelProperty(notes = "port to be tested", example = "48806")
    @JsonProperty("nontransproxy_objective_port")
    private Integer nonTransparentProxyObjectivePort;

    @ApiModelProperty(notes = "response message sent by server", example = "GET")
    @JsonProperty("nontransproxy_result_response")
    private String nonTransparentProxyResultResponse;

    @ApiModelProperty(notes = "url to test", example = "http://webtest.nettest.at/qostest/reference05.jpg")
    @JsonProperty("http_objective_url")
    private String httpObjectiveUrl;

    @ApiModelProperty(notes = "duration of the test in nanoseconds", example = "187509154")
    @JsonProperty("http_result_duration")
    private Long httpResultDuration;

    @ApiModelProperty(notes = "header of the result", example = "Accept-Ranges: bytes Connection: keep-alive Content-Length: 37198 Content-Type: image/jpeg Date: Wed, 12 Feb 2020 12:18:55 GMT ETag: \"54975384-914e\" Last-Modified: Sun, 21 Dec 2014 23:11:00 GMT Server: nginx X-Android-Received-Millis: 1581509934654 X-Android-Response-Source: NETWORK 200 X-Android-Selected-Protocol: http/1.1 X-Android-Sent-Millis: 1581509934597")
    @JsonProperty("http_result_header")
    private String httpResultHeader;

    @ApiModelProperty(notes = "result length in bytes", example = "37198")
    @JsonProperty("http_result_length")
    private Long httpResultLength;

    @ApiModelProperty(notes = "range of the bytes to load e.g. bytes=1000000-1004999 or null if whole response", example = "null")
    @JsonProperty("http_objective_range")
    private String httpObjectiveRange;

    @ApiModelProperty(notes = "hash of the results", example = "ae9592475c364fa01909dab663417ab5")
    @JsonProperty("http_result_hash")
    private String httpResultHash;

    @ApiModelProperty(notes = "http status of response e.g. 200, 206, ...", example = "200")
    @JsonProperty("http_result_status")
    private Integer httpResultStatus;

    @ApiModelProperty(notes = "timeout in nanoseconds", example = "10000000000")
    @JsonProperty("website_objective_timeout")
    private String websiteObjectiveTimeout;

    @ApiModelProperty(notes = "real duration of the load in nanoseconds", example = "6445962303")
    @JsonProperty("website_result_duration")
    private Long websiteResultDuration;

    @ApiModelProperty(notes = "bytes uploaded", example = "25248")
    @JsonProperty("website_result_tx_bytes")
    private Long websiteResultTxBytes;

    @ApiModelProperty(notes = "http status of the loaded webpage", example = "200")
    @JsonProperty("website_result_status")
    private Integer websiteResultStatus;

    @ApiModelProperty(notes = "bytes downloaded", example = "150281")
    @JsonProperty("website_result_rx_bytes")
    private Long websiteResultRxBytes;

    @ApiModelProperty(notes = "url to be tested", example = "http://webtest.nettest.at/kepler")
    @JsonProperty("website_objective_url")
    private String websiteObjectiveUrl;

    @ApiModelProperty(notes = "result status of the test, OK if succeed, FAILED otherwise", example = "OK")
    @JsonProperty("website_result_info")
    private String websiteResultInfo;

    @ApiModelProperty(notes = "array with all addresses for requested name")
    @JsonProperty("dns_result_entries")
    private List<DnsResultItem> dnsResultEntries;

    @ApiModelProperty(notes = "timeout for test to succeed", example = "5000000000")
    @JsonProperty("dns_objective_timeout")
    private Long dnsObjectiveTimeout;

    @ApiModelProperty(notes = "type of the record to accept", example = "A")
    @JsonProperty("dns_objective_dns_record")
    private String dnsObjectiveDnsRecord;

    @ApiModelProperty(notes = "address to be translated by DNS server", example = "ftp.f351dc1bef.com")
    @JsonProperty("dns_objective_host")
    private String dnsObjectiveHost;

    @ApiModelProperty(notes = "DNS resolver address to use during the test", example = "192.168.1.1")
    @JsonProperty("dns_objective_resolver")
    private String dnsObjectiveResolver;

    @ApiModelProperty(notes = "result status OK if test succeeds, TIMEOUT if timeout, FAILED otherwise", example = "OK")
    @JsonProperty("dns_result_info")
    private String dnsResultInfo;

    @ApiModelProperty(notes = "result status from DNS server NXDOMAIN - non existing domain, NOERROR - found entries", example = "NXDOMAIN")
    @JsonProperty("dns_result_status")
    private String dnsResultStatus;

    @ApiModelProperty(notes = "duration in nanoseconds to obtain translated address", example = "254117730,")
    @JsonProperty("dns_result_duration")
    private Long dnsResultDuration;

    @ApiModelProperty(notes = "how many addresses (entries) found", example = "0")
    @JsonProperty("dns_result_entries_found")
    private String dnsResultEntriesFound;

    @Getter
    @Setter
    public static class TracerouteItem {
        @ApiModelProperty(notes = "Anonymized ip address of the destination", example = "192.168.1.x")
        @JsonProperty("host")
        private String host;

        @ApiModelProperty(notes = "Time needed to executed traceroute to the destination address in nanoseconds", example = "445846961")
        @JsonProperty("time")
        private Long time;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DnsResultItem {
        @ApiModelProperty(notes = "dns result address from requested name", example = "31.13.84.36")
        @JsonProperty("dns_result_address")
        private String dnsResultAddress;

        @ApiModelProperty(notes = "Time to live of the entry in seconds", example = "261")
        @JsonProperty("dns_result_ttl")
        private Long dnsResultTtl;
    }
}
