package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.TestType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QosMeasurementsResponse {
    @ApiModelProperty(notes = "contains result for each particular test")
    @JsonProperty("testresultdetail")
    private List<QosTestResultItem> testResultDetails;
    @ApiModelProperty(notes = "description of the test to explain user more details about it")
    @JsonProperty("testresultdetail_desc")
    private List<QosTestResultDescItem> testResultDetailDesc;
    @ApiModelProperty(notes = "description of the test to explain user more details about it")
    @JsonProperty("testresultdetail_testdesc")
    private List<QosTestResultTestDescItem> testResultDetailTestDesc;
    @ApiModelProperty
    @JsonProperty("eval_times")
    private EvalTimes evalTimes;
    @ApiModelProperty(example = "[ \"First error message\", \"Second error message\" ]")
    @JsonProperty("error")
    private ErrorResponse errorResponse;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class EvalTimes {
        @ApiModelProperty(notes = "time to evaluate results on the server side in millis without loading results", example = "10")
        @JsonProperty("eval")
        private Long eval;
        @ApiModelProperty(notes = "time to evaluate results on the server side in millis with loading results from DB", example = "27")
        @JsonProperty("full")
        private Long full;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QosTestResultTestDescItem {
        @ApiModelProperty(notes = "localized name of the qos test group", example = "Web page")
        @JsonProperty("name")
        private String name;
        @ApiModelProperty(notes = "enum value of one of the type of the QoS test category", example = "WEBSITE")
        @JsonProperty("test_type")
        private TestType testType;
        @ApiModelProperty(notes = "localized description of the test group", example = "The website test downloads a reference web page (mobile Kepler page by ETSI). It is verified, if the page can be transferred and how long the download of the page takes.")
        @JsonProperty("desc")
        private String desc;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QosTestResultDescItem {
        @ApiModelProperty(notes = "ids of the test performed", example = "[ 464646864, 46468468, 868895634, 86865846, 46848445 ]")
        @JsonProperty("uid")
        private List<Long> uid;
        @ApiModelProperty(notes = "enum value of one of the type of the QoS test category", example = "WEBSITE")
        @JsonProperty("test")
        private TestType testType;
        @ApiModelProperty(notes = "key to be evaluated", example = "website.200")
        @JsonProperty("key")
        private String key;
        @ApiModelProperty(notes = "ok", example = "result status of the qos test")
        @JsonProperty("status")
        private String status;
        @ApiModelProperty(notes = "localized description of the result", example = "The web page has been transferred successfully.")
        @JsonProperty("desc")
        private String desc;
    }

    @Getter
    @Setter
    @Builder
    public static class QosTestResultItem {
        @ApiModelProperty(notes = "Keys used in the map 'test_result_key_map'", example = "[\"traceroute.success\"]")
        @JsonProperty("test_result_keys")
        private Set<String> testResultKeys;
        @ApiModelProperty(notes = "Object containing all keys from 'test_result_keys'", example = "{ \"traceroute.success\": \"ok\" }")
        @JsonProperty("test_result_key_map")
        private Map<String, String> testResultKeyMap;
        @ApiModelProperty(notes = "id of the test", example = "199209678")
        @JsonProperty("uid")
        private Long uid;
        @ApiModelProperty(example = "166")
        @JsonProperty("nn_test_uid")
        private Long nnTestUid;
        @ApiModelProperty(notes = "qos test uid", example = "166")
        @JsonProperty("qos_test_uid")
        private Long qosTestUid;
        @ApiModelProperty(notes = "test id", example = "8772489")
        @JsonProperty("test_uid")
        private Long testUid;
        @ApiModelProperty(notes = "localized description of the test", example = "Traceroute target: traceroutev4.netztest.at")
        @JsonProperty("test_summary")
        private String testSummary;
        @ApiModelProperty(notes = "count of the successfully executed goals in the test", example = "1")
        @JsonProperty("success_count")
        private Integer successCount;
        @ApiModelProperty(notes = "count of the failed executed goals in the test, test is not successful when this number is bigger than 0, 0 = successfully executed test", example = "0")
        @JsonProperty("failure_count")
        private Integer failureCount;
        @ApiModelProperty(notes = "Enum value of one of the type of the QoS test category", example = "WEBSITE")
        @JsonProperty("test_type")
        private TestType testType;
        @ApiModelProperty(notes = "localized description of the test progress", example = "Traceroute test parameters: Host: traceroutev4.netztest.at Max hops: 30 Traceroute test results: Hops needed: 7 Traceroute result: OK Full route: 62.197.195.x time=86.97ms 217.75.72.x time=33.91ms 217.75.72.x time=36.59ms 62.168.99.x time=42.98ms 80.81.194.x time=57.63ms * time=2044.05ms 83.68.136.x time=38.80ms")
        @JsonProperty("test_desc")
        private String testDesc;
        @ApiModelProperty(notes = "detailed result about qos progress")
        @JsonProperty("result")
        private Map<String, Object> result;

        @Getter
        @Setter
        @Builder
        public static class QosTestResultDetailsItem {
            @ApiModelProperty(notes = "duration of the test", example = "2628208113")
            @JsonProperty("duration_ns")
            private Long durationNs;
            @ApiModelProperty(notes = "start time of the test in nanoseconds, relative to start of the entire measurement", example = "59639502458")
            @JsonProperty("start_time_ns")
            private Long startTimeNs;
            @ApiModelProperty(notes = "max hops count to be successful?", example = "30")
            @JsonProperty("traceroute_objective_max_hops")
            private Integer tracerouteObjectiveMaxHops;
            @JsonProperty("traceroute_result_details")
            private List<TraceRouteDetailsItem> tracerouteResultDetails;
            @ApiModelProperty(notes = "timeout of the test in nanoseconds", example = "35000000000")
            @JsonProperty("traceroute_objective_timeout")
            private Long tracerouteObjectiveTimeout;
            @ApiModelProperty(notes = "result status of the test", example = "OK")
            @JsonProperty("traceroute_result_status")
            private String tracerouteResultStatus;
            @ApiModelProperty(notes = "hops executed during the test", example = "7")
            @JsonProperty("traceroute_result_hops")
            private Integer tracerouteResultHops;
            @ApiModelProperty(notes = "target host address", example = "traceroutev4.netztest.at")
            @JsonProperty("traceroute_objective_host")
            private String tracerouteObjectiveHost;
            @ApiModelProperty(notes = "test url", example = "http://webtest.nettest.at/qostest/reference05.jpg")
            @JsonProperty("http_objective_url")
            private String httpObjectiveUrl;
            @ApiModelProperty(notes = "result loading time in nanoseconds", example = "172272154")
            @JsonProperty("http_result_duration")
            private Long httpResultDuration;
            @ApiModelProperty(notes = "header of the result", example = "Accept-Ranges: bytes Connection: keep-alive Content-Length: 37198 Content-Type: image/jpeg Date: Mon, 21 Oct 2019 13:52:52 GMT ETag: \"54975384-914e\" Last-Modified: Sun, 21 Dec 2014 23:11:00 GMT Server: nginx X-Android-Received-Millis: 1571665969265 X-Android-Response-Source: NETWORK 200 X-Android-Selected-Protocol: http/1.1 X-Android-Sent-Millis: 1571665969215")
            @JsonProperty("http_result_header")
            private String httpResultHeader;
            @ApiModelProperty(notes = "total bytes loaded", example = "37198")
            @JsonProperty("http_result_length")
            private Long httpResultLength;
            @ApiModelProperty(notes = "null if not defined and whole response is taken into account", example = "bytes=1000000-1004999")
            @JsonProperty("http_objective_range")
            private String httpObjectiveRange;
            @ApiModelProperty(notes = "hash of thre result", example = "ae9592475c364fa01909dab663417ab5")
            @JsonProperty("http_result_hash")
            private String httpResultHash;
            @ApiModelProperty(notes = "http status of response to the request", example = "200")
            @JsonProperty("http_result_status")
            private Integer httpResultStatus;
            @ApiModelProperty(notes = "timeout of the load website objective", example = "10000000000")
            @JsonProperty("website_objective_timeout")
            private Long websiteObjectiveTimeout;
            @ApiModelProperty(notes = "how much time it took to load page in nano seconds", example = "1725373807")
            @JsonProperty("website_result_duration")
            private Long websiteResultDuration;
            @ApiModelProperty(notes = "bytes uploaded during load", example = "25176")
            @JsonProperty("website_result_tx_byte")
            private Long websiteResultTxByte;
            @ApiModelProperty(notes = "bytes downloaded during load", example = "150333")
            @JsonProperty("website_result_rx_bytes")
            private Long website_result_rx_bytes;
            @ApiModelProperty(notes = "http status of the response", example = "200")
            @JsonProperty("website_result_status")
            private Integer website_result_status;
            @ApiModelProperty(notes = "target url to test", example = "http://webtest.nettest.at/kepler")
            @JsonProperty("website_objective_url")
            private String website_objective_url;
            @ApiModelProperty(notes = "result status in human readable form", example = "OK")
            @JsonProperty("website_result_info")
            private String website_result_info;
            @ApiModelProperty(notes = "result dns entries, null if not any", example = "")
            @JsonProperty("dns_result_entries")
            private List<DnsResultEntry> dnsResultEntries;
            @ApiModelProperty(notes = "test timeout", example = "5000000000")
            @JsonProperty("dns_objective_timeout")
            private Long dnsObjectiveTimeout;
            @ApiModelProperty(notes = "target host to resolve", example = "ftp.0185e9b255.com")
            @JsonProperty("dns_objective_host")
            private String dnsObjectiveHost;
            @ApiModelProperty(notes = "resolver address", example = "192.168.2.43")
            @JsonProperty("dns_objective_resolver")
            private String dnsObjectiveResolver;
            @ApiModelProperty(notes = "result status in human readable form", example = "OK")
            @JsonProperty("dns_result_info")
            private String dnsResultInfo;
            @ApiModelProperty(notes = "status of the result [\"NOERROR\", \"NXDOMAIN\"]", example = "NXDOMAIN")
            @JsonProperty("dns_result_status")
            private String dnsResultStatus;
            @ApiModelProperty(notes = "duration of the result obtaining", example = "332901846")
            @JsonProperty("dns_result_duration")
            private Long dnsResultDuration;
            @ApiModelProperty(notes = "type of the record to find", example = "A")
            @JsonProperty("dns_objective_dns_record")
            private String dnsObjectiveDnsRecord;
            @ApiModelProperty(notes = "count of the results found", example = "0")
            @JsonProperty("dns_result_entries_found")
            private String dnsResultEntriesFound;
            @ApiModelProperty(notes = "shortest sequence without any error", example = "100")
            @JsonProperty("voip_result_out_short_seq")
            private Long voipResultOutShortSeq;
            @ApiModelProperty(notes = "longest incomming sequence with error", example = "0")
            @JsonProperty("voip_result_in_sequence_error")
            private Long voipResultInSequenceError;
            @ApiModelProperty(notes = "objective delay to wait in nanoseconds", example = "10020000000")
            @JsonProperty("voip_objective_delay")
            private Long voipObjectiveDelay;
            @ApiModelProperty(notes = "objective sample rate", example = "8000")
            @JsonProperty("voip_objective_sample_rate")
            private Long voipObjectiveSampleRate;
            @ApiModelProperty(notes = "total number of packets emited", example = "100")
            @JsonProperty("voip_result_out_num_packets")
            private Integer voipResultOutNumPackets;
            @ApiModelProperty(notes = "longest errorless sequence", example = "100")
            @JsonProperty("voip_result_out_long_seq")
            private Integer voipResultOutLongSeq;
            @ApiModelProperty(notes = "bits per sample", example = "8")
            @JsonProperty("voip_objective_bits_per_sample")
            private Integer voipObjectiveBitsPerSample;
            @ApiModelProperty(notes = "max jitter out in nanos", example = "10212357")
            @JsonProperty("voip_result_out_max_jitter")
            private Long voipResultOutMaxJitter;
            @ApiModelProperty(notes = "mean jitter out in nanos", example = "3610909")
            @JsonProperty("voip_result_out_mean_jitter")
            private Long voipResultOutMeanJitter;
            @ApiModelProperty(notes = "total result status", example = "OK")
            @JsonProperty("voip_result_status")
            private String voipResultStatus;
            @ApiModelProperty(notes = "total number of incomming packets (outcoming and incoming should be equal to test to be successful)", example = "100")
            @JsonProperty("voip_result_in_num_packets")
            private Integer voipResultInNumPackets;
            @ApiModelProperty(notes = "longest outgoing sequence with error", example = "0")
            @JsonProperty("voip_result_out_sequence_error")
            private Integer voipResultOutSequenceError;
            @ApiModelProperty(notes = "max incomming jitter in nanoseconds", example = "4009958")
            @JsonProperty("voip_result_in_max_jitter")
            private Long voipResultInMaxJitter;
            @ApiModelProperty(notes = "incomming skew in nanoseconds", example = "-135516126")
            @JsonProperty("voip_result_in_skew")
            private Long voipResultInSkew;
            @ApiModelProperty(notes = "outgoing max delta in nanoseconds", example = "51375887")
            @JsonProperty("voip_result_out_max_delta")
            private Long voipResultOutMaxDelta;
            @ApiModelProperty(notes = "input port", example = "null")
            @JsonProperty("voip_objective_in_port")
            private Integer voipObjectiveInPort;
            @ApiModelProperty(notes = "total call duration in nanoseconds", example = "2000000000")
            @JsonProperty("voip_objective_call_duration")
            private Long voipObjectiveCallDuration;
            @ApiModelProperty(notes = "shortest valid incomming sequence", example = "100")
            @JsonProperty("voip_result_in_short_seq")
            private Long voipResultInShortSeq;
            @ApiModelProperty(notes = "incomming max delta in nanoseconds", example = "19643375")
            @JsonProperty("voip_result_in_max_delta")
            private Long voipResultInMaxDelta;
            @ApiModelProperty(notes = "payload objective", example = "8")
            @JsonProperty("voip_objective_payload")
            private Integer voipObjectivePayload;
            @ApiModelProperty(notes = "outgoing port", example = "5060")
            @JsonProperty("voip_objective_out_port")
            private Integer voipObjectiveOutPort;

            @ApiModelProperty(notes = "incomming mean jitter in nanoseconds", example = "1533211")
            @JsonProperty("voip_result_in_mean_jitter")
            private Long voipResultInMeanJitter;
            @ApiModelProperty(notes = "incomming longest errorless sequence", example = "100")
            @JsonProperty("voip_result_in_long_seq")
            private Integer voipResultInLongSeq;
            @ApiModelProperty(notes = "outgoing skew in nanoseconds", example = "-181283337")
            @JsonProperty("voip_result_out_skew")
            private Long voipResultOutSkew;

            @Getter
            @Builder
            @AllArgsConstructor
            public static class DnsResultEntry {
                @ApiModelProperty(notes = "dns result address - ipv4 or ipv6", example = "31.13.84.36")
                @JsonProperty("dns_result_address")
                private String dnsResultAddress;
                @ApiModelProperty(notes = "time to live of result in seconds?", example = "52")
                @JsonProperty("dns_result_ttl")
                private String dnsResultTtl;
            }

            @Getter
            @Builder
            @AllArgsConstructor
            public static class TraceRouteDetailsItem {
                @ApiModelProperty(notes = "ip address of the host", example = "62.197.195.x")
                @JsonProperty("host")
                private String host;
                @ApiModelProperty(notes = "duration of contacting address in nanoseconds", example = "86973384")
                @JsonProperty("time")
                private Long time;
            }
        }
    }
}
