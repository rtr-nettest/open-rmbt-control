package at.rtr.rmbt.request;

import at.rtr.rmbt.enums.TestType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class QosSendTestResultItem {
    @Schema(description = "Qos test uid")
    @JsonProperty("qos_test_uid")
    private long qosTestUid;

    @Schema(example = "WEBSITE", description = "Enum value of one of the type of the QoS test category")
    @JsonProperty("test_type")
    private TestType testType;

    @Schema(example = "2628208113", description = "Duration of the test")
    @JsonProperty("duration_ns")
    private Long durationNs;

    @Schema(example = "59639502458", description = "Start time of the test in nanoseconds, relative to start of the entire measurement")
    @JsonProperty("start_time_ns")
    private Long startTimeNs;

    @Schema(example = "100", description = "VOIP shortest sequence without any error")
    @JsonProperty("voip_result_out_short_seq")
    private Long voipResultOutShortSeq;

    @Schema(example = "0", description = "VOIP longest incomming sequence with error")
    @JsonProperty("voip_result_in_sequence_error")
    private Long voipResultInSequenceError;

    @Schema(example = "10020000000", description = "VOIP objective delay to wait in nanoseconds")
    @JsonProperty("voip_objective_delay")
    private Long voipObjectiveDelay;

    @Schema(example = "8000", description = "VOIP objective sample rate")
    @JsonProperty("voip_objective_sample_rate")
    private Long voipObjectiveSampleRate;

    @Schema(example = "100", description = "VOIP total number of packets emitted")
    @JsonProperty("voip_result_out_num_packets")
    private Integer voipResultOutNumPackets;

    @Schema(example = "100", description = "VOIP longest errorless sequence")
    @JsonProperty("voip_result_out_long_seq")
    private Integer voipResultOutLongSeq;

    @Schema(example = "8", description = "VOIP bits per sample")
    @JsonProperty("voip_objective_bits_per_sample")
    private Integer voipObjectiveBitsPerSample;

    @Schema(example = "10212357", description = "VOIP max jitter out in nanos")
    @JsonProperty("voip_result_out_max_jitter")
    private Long voipResultOutMaxJitter;

    @Schema(example = "3610909", description = "VOIP mean jitter out in nanos")
    @JsonProperty("voip_result_out_mean_jitter")
    private Long voipResultOutMeanJitter;

    @Schema(example = "OK", description = "VOIP total result status")
    @JsonProperty("voip_result_status")
    private String voipResultStatus;

    @Schema(example = "100", description = "VOIP total number of incomming packets (outcoming and incoming should be equal to test to be successful)")
    @JsonProperty("voip_result_in_num_packets")
    private Integer voipResultInNumPackets;

    @Schema(example = "0", description = "VOIP longest outgoing sequence with error")
    @JsonProperty("voip_result_out_sequence_error")
    private Integer voipResultOutSequenceError;

    @Schema(example = "4009958", description = "VOIP max incomming jitter in nanoseconds")
    @JsonProperty("voip_result_in_max_jitter")
    private Long voipResultInMaxJitter;

    @Schema(example = "-135516126", description = "VOIP incomming skew in nanoseconds")
    @JsonProperty("voip_result_in_skew")
    private Long voipResultInSkew;

    @Schema(example = "51375887", description = "VOIP outgoing max delta in nanoseconds")
    @JsonProperty("voip_result_out_max_delta")
    private Long voipResultOutMaxDelta;

    @Schema(example = "null", description = "VOIP input port")
    @JsonProperty("voip_objective_in_port")
    private Integer voipObjectiveInPort;

    @Schema(example = "2000000000", description = "VOIP total call duration in nanoseconds")
    @JsonProperty("voip_objective_call_duration")
    private Long voipObjectiveCallDuration;

    @Schema(example = "100", description = "VOIP shortest valid incomming sequence")
    @JsonProperty("voip_result_in_short_seq")
    private Long voipResultInShortSeq;

    @Schema(example = "19643375", description = "VOIP incomming max delta in nanoseconds")
    @JsonProperty("voip_result_in_max_delta")
    private Long voipResultInMaxDelta;

    @Schema(example = "8", description = "VOIP payload objective")
    @JsonProperty("voip_objective_payload")
    private Integer voipObjectivePayload;

    @Schema(example = "5060", description = "VOIP outgoing port")
    @JsonProperty("voip_objective_out_port")
    private Integer voipObjectiveOutPort;

    @Schema(example = "1533211", description = "VOIP incomming mean jitter in nanoseconds")
    @JsonProperty("voip_result_in_mean_jitter")
    private Long voipResultInMeanJitter;

    @Schema(example = "100", description = "VOIP incomming longest errorless sequence")
    @JsonProperty("voip_result_in_long_seq")
    private Integer voipResultInLongSeq;

    @Schema(example = "-181283337", description = "VOIP outgoing skew in nanoseconds")
    @JsonProperty("voip_result_out_skew")
    private Integer voipResultOutSkew;

    @Schema(example = "30", description = "max valid hops to count this test results as success")
    @JsonProperty("traceroute_objective_max_hops")
    private Integer tracerouteObjectiveMaxHops;

    @Schema(description = "Result items with addresses and times which were tested")
    @JsonProperty("traceroute_result_details")
    private List<TracerouteItem> tracerouteResultDetails;

    @Schema(example = "35000000000", description = "Timeout of the test until which is test executed in nanoseconds")
    @JsonProperty("traceroute_objective_timeout")
    private Long tracerouteObjectiveTimeout;

    @Schema(description = "OK if test ended successfully, FAILED if not", example = "OK")
    @JsonProperty("traceroute_result_status")
    private String tracerouteResultStatus;

    @Schema(description = "hops necessary to achieve destination", example = "13")
    @JsonProperty("traceroute_result_hops")
    private Integer tracerouteResultHops;

    @Schema(description = "host to reach in traceroute test", example = "traceroutev4.netztest.at")
    @JsonProperty("traceroute_objective_host")
    private String tracerouteObjectiveHost;

    @Schema(description = "timeout of the test to succeed in nanoseconds", example = "5000000000")
    @JsonProperty("tcp_objective_timeout")
    private Long tcpObjectiveTimeout;

    @Schema(description = "OK if test ended successfully or FAILED if not", example = "FAILED")
    @JsonProperty("tcp_result_in")
    private String tcpResultIn;

    @Schema(description = "in port for tcp connection", example = "8080")
    @JsonProperty("tcp_objective_in_port")
    private Integer tcpObjectiveInPort;

    @Schema(description = "OK if test ended successfully or FAILED if not", example = "FAILED")
    @JsonProperty("tcp_result_out")
    private String tcpResultOut;

    @Schema(description = "response from server to the test", example = "PING")
    @JsonProperty("tcp_result_out_response")
    private String tcpResultOutResponse;

    @Schema(description = "outgoing port for tcp connection", example = "8080")
    @JsonProperty("tcp_objective_out_port")
    private Integer tcpObjectiveOutPort;

    @Schema(description = "delay in nanoseconds to send packets back", example = "3000000000")
    @JsonProperty("udp_objective_delay")
    private Long udpObjectiveDelay;

    @Schema(description = "timeout for test to succeed in nanoseconds", example = "5000000000")
    @JsonProperty("udp_objective_timeout")
    private Long udpObjectiveTimeout;

    @Schema(description = "number of incomming packets via UDP protocol", example = "0")
    @JsonProperty("udp_result_in_num_packets")
    private Integer udpResultInNumPackets;

    @Schema(description = "packet loss rate in percentage", example = "100")
    @JsonProperty("udp_result_in_packet_loss_rate")
    private Integer udpResultInPacketLossRate;

    @Schema(description = "total number of expected incomming packets", example = "5")
    @JsonProperty("udp_objective_in_num_packets")
    private Integer udpObjectiveInNumPackets;

    @Schema(description = "number of packet in response back to server", example = "0")
    @JsonProperty("udp_result_in_response_num_packets")
    private Integer udpResultInResponseNumPackets;

    @Schema(description = "port to be tested", example = "5004")
    @JsonProperty("udp_objective_in_port")
    private Integer udpObjectiveInPort;

    @Schema(description = "number of outgoing packets via UDP protocol", example = "0")
    @JsonProperty("udp_result_out_num_packets")
    private Integer udpResultOutNumPackets;

    @Schema(description = "packet loss rate in percentage", example = "0")
    @JsonProperty("udp_result_out_packet_loss_rate")
    private Integer udpResultOutPacketLossRate;

    @Schema(description = "total number of expected outgoing packets", example = "5")
    @JsonProperty("udp_objective_out_num_packets")
    private Integer udpObjectiveOutNumPackets;

    @Schema(description = "number of packet in response back", example = "0")
    @JsonProperty("udp_result_out_response_num_packets")
    private Integer udpResultOutResponseNumPackets;

    @Schema(description = "port to be tested", example = "27005")
    @JsonProperty("udp_objective_out_port")
    private Integer udpObjectiveOutPort;

    @Schema(description = "OK if test succeeds, else FAILED or TIMEOUT", example = "OK")
    @JsonProperty("nontransproxy_result")
    private String nonTransparentProxyResult;

    @Schema(description = "request message", example = "GET")
    @JsonProperty("nontransproxy_objective_request")
    private String nonTransparentProxyObjectiveRequest;

    @Schema(description = "timeout in nanoseconds to test succeed", example = "5000000000")
    @JsonProperty("nontransproxy_objective_timeout")
    private Long nonTransparentProxyObjectiveTimeout;

    @Schema(description = "port to be tested", example = "48806")
    @JsonProperty("nontransproxy_objective_port")
    private Integer nonTransparentProxyObjectivePort;

    @Schema(description = "response message sent by server", example = "GET")
    @JsonProperty("nontransproxy_result_response")
    private String nonTransparentProxyResultResponse;

    @Schema(description = "url to test", example = "http://webtest.nettest.at/qostest/reference05.jpg")
    @JsonProperty("http_objective_url")
    private String httpObjectiveUrl;

    @Schema(description = "duration of the test in nanoseconds", example = "187509154")
    @JsonProperty("http_result_duration")
    private Long httpResultDuration;

    @Schema(description = "header of the result", example = "Accept-Ranges: bytes Connection: keep-alive Content-Length: 37198 Content-Type: image/jpeg Date: Wed, 12 Feb 2020 12:18:55 GMT ETag: \"54975384-914e\" Last-Modified: Sun, 21 Dec 2014 23:11:00 GMT Server: nginx X-Android-Received-Millis: 1581509934654 X-Android-Response-Source: NETWORK 200 X-Android-Selected-Protocol: http/1.1 X-Android-Sent-Millis: 1581509934597")
    @JsonProperty("http_result_header")
    private String httpResultHeader;

    @Schema(description = "result length in bytes", example = "37198")
    @JsonProperty("http_result_length")
    private Long httpResultLength;

    @Schema(description = "range of the bytes to load e.g. bytes=1000000-1004999 or null if whole response", example = "null")
    @JsonProperty("http_objective_range")
    private String httpObjectiveRange;

    @Schema(description = "hash of the results", example = "ae9592475c364fa01909dab663417ab5")
    @JsonProperty("http_result_hash")
    private String httpResultHash;

    @Schema(description = "http status of response e.g. 200, 206, ...", example = "200")
    @JsonProperty("http_result_status")
    private Integer httpResultStatus;

    @Schema(description = "timeout in nanoseconds", example = "10000000000")
    @JsonProperty("website_objective_timeout")
    private Long websiteObjectiveTimeout;

    @Schema(description = "real duration of the load in nanoseconds", example = "6445962303")
    @JsonProperty("website_result_duration")
    private Long websiteResultDuration;

    @Schema(description = "bytes uploaded", example = "25248")
    @JsonProperty("website_result_tx_bytes")
    private Long websiteResultTxBytes;

    @Schema(description = "http status of the loaded webpage", example = "200")
    @JsonProperty("website_result_status")
    private Integer websiteResultStatus;

    @Schema(description = "bytes downloaded", example = "150281")
    @JsonProperty("website_result_rx_bytes")
    private Long websiteResultRxBytes;

    @Schema(description = "url to be tested", example = "http://webtest.nettest.at/kepler")
    @JsonProperty("website_objective_url")
    private String websiteObjectiveUrl;

    @Schema(description = "result status of the test, OK if succeed, FAILED otherwise", example = "OK")
    @JsonProperty("website_result_info")
    private String websiteResultInfo;

    @Schema(description = "array with all addresses for requested name")
    @JsonProperty("dns_result_entries")
    private List<DnsResultItem> dnsResultEntries;

    @Schema(description = "timeout for test to succeed", example = "5000000000")
    @JsonProperty("dns_objective_timeout")
    private Long dnsObjectiveTimeout;

    @Schema(description = "type of the record to accept", example = "A")
    @JsonProperty("dns_objective_dns_record")
    private String dnsObjectiveDnsRecord;

    @Schema(description = "address to be translated by DNS server", example = "ftp.f351dc1bef.com")
    @JsonProperty("dns_objective_host")
    private String dnsObjectiveHost;

    @Schema(description = "DNS resolver address to use during the test", example = "192.168.1.1")
    @JsonProperty("dns_objective_resolver")
    private String dnsObjectiveResolver;

    @Schema(description = "result status OK if test succeeds, TIMEOUT if timeout, FAILED otherwise", example = "OK")
    @JsonProperty("dns_result_info")
    private String dnsResultInfo;

    @Schema(description = "result status from DNS server NXDOMAIN - non existing domain, NOERROR - found entries", example = "NXDOMAIN")
    @JsonProperty("dns_result_status")
    private String dnsResultStatus;

    @Schema(description = "duration in nanoseconds to obtain translated address", example = "254117730,")
    @JsonProperty("dns_result_duration")
    private Long dnsResultDuration;

    @Schema(description = "how many addresses (entries) found", example = "0")
    @JsonProperty("dns_result_entries_found")
    private Integer dnsResultEntriesFound;

    @Getter
    @Setter
    public static class TracerouteItem {
        @Schema(description = "Anonymized ip address of the destination", example = "192.168.1.x")
        @JsonProperty("host")
        private String host;

        @Schema(description = "Time needed to executed traceroute to the destination address in nanoseconds", example = "445846961")
        @JsonProperty("time")
        private Long time;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DnsResultItem {
        @Schema(description = "dns result address from requested name", example = "31.13.84.36")
        @JsonProperty("dns_result_address")
        private String dnsResultAddress;

        @Schema(description = "Time to live of the entry in seconds", example = "261")
        @JsonProperty("dns_result_ttl")
        private Long dnsResultTtl;

        @Schema(description = "dns result priority", example = "200")
        @JsonProperty("dns_result_priority")
        private Long dnsResultPriority;
    }
}
