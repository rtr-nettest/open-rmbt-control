package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.TestType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QosMeasurementsResponse extends ErrorResponse {
    @Schema(description = "contains result for each particular test")
    @JsonProperty("testresultdetail")
    private List<QosTestResultItem> testResultDetails;
    @Schema(description = "description of the test to explain user more details about it")
    @JsonProperty("testresultdetail_desc")
    private List<QosTestResultDescItem> testResultDetailDesc;
    @Schema(description = "description of the test to explain user more details about it")
    @JsonProperty("testresultdetail_testdesc")
    private List<QosTestResultTestDescItem> testResultDetailTestDesc;
    @Schema(description = "Evaluation times")
    @JsonProperty("eval_times")
    private EvalTimes evalTimes;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class EvalTimes {
        @Schema(description = "time to evaluate results on the server side in millis without loading results", example = "10")
        @JsonProperty("eval")
        private Long eval;
        @Schema(description = "time to evaluate results on the server side in millis with loading results from DB", example = "27")
        @JsonProperty("full")
        private Long full;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QosTestResultTestDescItem {
        @Schema(description = "localized name of the qos test group", example = "Web page")
        @JsonProperty("name")
        private String name;
        @Schema(description = "enum value of one of the type of the QoS test category", example = "WEBSITE")
        @JsonProperty("test_type")
        private String testType;
        @Schema(description = "localized description of the test group", example = "The website test downloads a reference web page (mobile Kepler page by ETSI). It is verified, if the page can be transferred and how long the download of the page takes.")
        @JsonProperty("desc")
        private String desc;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QosTestResultDescItem {
        @Schema(description = "ids of the test performed", example = "[ 464646864, 46468468, 868895634, 86865846, 46848445 ]")
        @JsonProperty("uid")
        private List<Long> uid;
        @Schema(description = "Enum value of one of the type of the QoS test category (UPPERCASE)", example = "WEBSITE")
        @JsonProperty("test")
        private String testType;
        @Schema(description = "key to be evaluated", example = "website.200")
        @JsonProperty("key")
        private String key;
        @Schema(description = "ok", example = "result status of the qos test")
        @JsonProperty("status")
        private String status;
        @Schema(description = "localized description of the result", example = "The web page has been transferred successfully.")
        @JsonProperty("desc")
        private String desc;
    }

    @Getter
    @Setter
    @Builder
    public static class QosTestResultItem {
        @Schema(description = "Keys used in the map 'test_result_key_map'", example = "[\"traceroute.success\"]")
        @JsonProperty("test_result_keys")
        private Set<String> testResultKeys;
        @Schema(description = "Object containing all keys from 'test_result_keys'", example = "{ \"traceroute.success\": \"ok\" }")
        @JsonProperty("test_result_key_map")
        private Map<String, String> testResultKeyMap;
        @Schema(description = "id of the test", example = "199209678")
        @JsonProperty("uid")
        private Long uid;
        @Schema(example = "166")
        @JsonProperty("nn_test_uid")
        private Long nnTestUid;
        @Schema(description = "qos test uid", example = "166")
        @JsonProperty("qos_test_uid")
        private Long qosTestUid;
        @Schema(description = "test id", example = "8772489")
        @JsonProperty("test_uid")
        private Long testUid;
        @Schema(description = "localized description of the test", example = "Traceroute target: traceroutev4.netztest.at")
        @JsonProperty("test_summary")
        private String testSummary;
        @Schema(description = "count of the successfully executed goals in the test", example = "1")
        @JsonProperty("success_count")
        private Integer successCount;
        @Schema(description = "count of the failed executed goals in the test, test is not successful when this number is bigger than 0, 0 = successfully executed test", example = "0")
        @JsonProperty("failure_count")
        private Integer failureCount;
        @Schema(description = "Enum value of one of the type of the QoS test category", example = "WEBSITE")
        @JsonProperty("test_type")
        private TestType testType;
        @Schema(description = "localized description of the test progress", example = "Traceroute test parameters: Host: traceroutev4.netztest.at Max hops: 30 Traceroute test results: Hops needed: 7 Traceroute result: OK Full route: 62.197.195.x time=86.97ms 217.75.72.x time=33.91ms 217.75.72.x time=36.59ms 62.168.99.x time=42.98ms 80.81.194.x time=57.63ms * time=2044.05ms 83.68.136.x time=38.80ms")
        @JsonProperty("test_desc")
        private String testDesc;
        @Schema(description = "detailed result about qos progress")
        @JsonProperty("result")
        private Map<String, Object> result;

        @Getter
        @Setter
        @Builder
        public static class QosTestResultDetailsItem {
            @Schema(description = "duration of the test", example = "2628208113")
            @JsonProperty("duration_ns")
            private Long durationNs;
            @Schema(description = "start time of the test in nanoseconds, relative to start of the entire measurement", example = "59639502458")
            @JsonProperty("start_time_ns")
            private Long startTimeNs;
            @Schema(description = "max hops count to be successful?", example = "30")
            @JsonProperty("traceroute_objective_max_hops")
            private Integer tracerouteObjectiveMaxHops;
            @JsonProperty("traceroute_result_details")
            private List<TraceRouteDetailsItem> tracerouteResultDetails;
            @Schema(description = "timeout of the test in nanoseconds", example = "35000000000")
            @JsonProperty("traceroute_objective_timeout")
            private Long tracerouteObjectiveTimeout;
            @Schema(description = "result status of the test", example = "OK")
            @JsonProperty("traceroute_result_status")
            private String tracerouteResultStatus;
            @Schema(description = "hops executed during the test", example = "7")
            @JsonProperty("traceroute_result_hops")
            private Integer tracerouteResultHops;
            @Schema(description = "target host address", example = "traceroutev4.netztest.at")
            @JsonProperty("traceroute_objective_host")
            private String tracerouteObjectiveHost;
            @Schema(description = "test url", example = "http://webtest.nettest.at/qostest/reference05.jpg")
            @JsonProperty("http_objective_url")
            private String httpObjectiveUrl;
            @Schema(description = "result loading time in nanoseconds", example = "172272154")
            @JsonProperty("http_result_duration")
            private Long httpResultDuration;
            @Schema(description = "header of the result", example = "Accept-Ranges: bytes Connection: keep-alive Content-Length: 37198 Content-Type: image/jpeg Date: Mon, 21 Oct 2019 13:52:52 GMT ETag: \"54975384-914e\" Last-Modified: Sun, 21 Dec 2014 23:11:00 GMT Server: nginx X-Android-Received-Millis: 1571665969265 X-Android-Response-Source: NETWORK 200 X-Android-Selected-Protocol: http/1.1 X-Android-Sent-Millis: 1571665969215")
            @JsonProperty("http_result_header")
            private String httpResultHeader;
            @Schema(description = "total bytes loaded", example = "37198")
            @JsonProperty("http_result_length")
            private Long httpResultLength;
            @Schema(description = "null if not defined and whole response is taken into account", example = "bytes=1000000-1004999")
            @JsonProperty("http_objective_range")
            private String httpObjectiveRange;
            @Schema(description = "hash of thre result", example = "ae9592475c364fa01909dab663417ab5")
            @JsonProperty("http_result_hash")
            private String httpResultHash;
            @Schema(description = "http status of response to the request", example = "200")
            @JsonProperty("http_result_status")
            private Integer httpResultStatus;
            @Schema(description = "timeout of the load website objective", example = "10000000000")
            @JsonProperty("website_objective_timeout")
            private Long websiteObjectiveTimeout;
            @Schema(description = "how much time it took to load page in nano seconds", example = "1725373807")
            @JsonProperty("website_result_duration")
            private Long websiteResultDuration;
            @Schema(description = "bytes uploaded during load", example = "25176")
            @JsonProperty("website_result_tx_byte")
            private Long websiteResultTxByte;
            @Schema(description = "bytes downloaded during load", example = "150333")
            @JsonProperty("website_result_rx_bytes")
            private Long website_result_rx_bytes;
            @Schema(description = "http status of the response", example = "200")
            @JsonProperty("website_result_status")
            private Integer website_result_status;
            @Schema(description = "target url to test", example = "http://webtest.nettest.at/kepler")
            @JsonProperty("website_objective_url")
            private String website_objective_url;
            @Schema(description = "result status in human readable form", example = "OK")
            @JsonProperty("website_result_info")
            private String website_result_info;
            @Schema(description = "result dns entries, null if not any", example = "")
            @JsonProperty("dns_result_entries")
            private List<DnsResultEntry> dnsResultEntries;
            @Schema(description = "test timeout", example = "5000000000")
            @JsonProperty("dns_objective_timeout")
            private Long dnsObjectiveTimeout;
            @Schema(description = "target host to resolve", example = "ftp.0185e9b255.com")
            @JsonProperty("dns_objective_host")
            private String dnsObjectiveHost;
            @Schema(description = "resolver address", example = "192.168.2.43")
            @JsonProperty("dns_objective_resolver")
            private String dnsObjectiveResolver;
            @Schema(description = "result status in human readable form", example = "OK")
            @JsonProperty("dns_result_info")
            private String dnsResultInfo;
            @Schema(description = "status of the result [\"NOERROR\", \"NXDOMAIN\"]", example = "NXDOMAIN")
            @JsonProperty("dns_result_status")
            private String dnsResultStatus;
            @Schema(description = "duration of the result obtaining", example = "332901846")
            @JsonProperty("dns_result_duration")
            private Long dnsResultDuration;
            @Schema(description = "type of the record to find", example = "A")
            @JsonProperty("dns_objective_dns_record")
            private String dnsObjectiveDnsRecord;
            @Schema(description = "count of the results found", example = "0")
            @JsonProperty("dns_result_entries_found")
            private String dnsResultEntriesFound;
            @Schema(description = "shortest sequence without any error", example = "100")
            @JsonProperty("voip_result_out_short_seq")
            private Long voipResultOutShortSeq;
            @Schema(description = "longest incomming sequence with error", example = "0")
            @JsonProperty("voip_result_in_sequence_error")
            private Long voipResultInSequenceError;
            @Schema(description = "objective delay to wait in nanoseconds", example = "10020000000")
            @JsonProperty("voip_objective_delay")
            private Long voipObjectiveDelay;
            @Schema(description = "objective sample rate", example = "8000")
            @JsonProperty("voip_objective_sample_rate")
            private Long voipObjectiveSampleRate;
            @Schema(description = "total number of packets emited", example = "100")
            @JsonProperty("voip_result_out_num_packets")
            private Integer voipResultOutNumPackets;
            @Schema(description = "longest errorless sequence", example = "100")
            @JsonProperty("voip_result_out_long_seq")
            private Integer voipResultOutLongSeq;
            @Schema(description = "bits per sample", example = "8")
            @JsonProperty("voip_objective_bits_per_sample")
            private Integer voipObjectiveBitsPerSample;
            @Schema(description = "max jitter out in nanos", example = "10212357")
            @JsonProperty("voip_result_out_max_jitter")
            private Long voipResultOutMaxJitter;
            @Schema(description = "mean jitter out in nanos", example = "3610909")
            @JsonProperty("voip_result_out_mean_jitter")
            private Long voipResultOutMeanJitter;
            @Schema(description = "total result status", example = "OK")
            @JsonProperty("voip_result_status")
            private String voipResultStatus;
            @Schema(description = "total number of incomming packets (outcoming and incoming should be equal to test to be successful)", example = "100")
            @JsonProperty("voip_result_in_num_packets")
            private Integer voipResultInNumPackets;
            @Schema(description = "longest outgoing sequence with error", example = "0")
            @JsonProperty("voip_result_out_sequence_error")
            private Integer voipResultOutSequenceError;
            @Schema(description = "max incomming jitter in nanoseconds", example = "4009958")
            @JsonProperty("voip_result_in_max_jitter")
            private Long voipResultInMaxJitter;
            @Schema(description = "incomming skew in nanoseconds", example = "-135516126")
            @JsonProperty("voip_result_in_skew")
            private Long voipResultInSkew;
            @Schema(description = "outgoing max delta in nanoseconds", example = "51375887")
            @JsonProperty("voip_result_out_max_delta")
            private Long voipResultOutMaxDelta;
            @Schema(description = "input port", example = "null")
            @JsonProperty("voip_objective_in_port")
            private Integer voipObjectiveInPort;
            @Schema(description = "total call duration in nanoseconds", example = "2000000000")
            @JsonProperty("voip_objective_call_duration")
            private Long voipObjectiveCallDuration;
            @Schema(description = "shortest valid incomming sequence", example = "100")
            @JsonProperty("voip_result_in_short_seq")
            private Long voipResultInShortSeq;
            @Schema(description = "incomming max delta in nanoseconds", example = "19643375")
            @JsonProperty("voip_result_in_max_delta")
            private Long voipResultInMaxDelta;
            @Schema(description = "payload objective", example = "8")
            @JsonProperty("voip_objective_payload")
            private Integer voipObjectivePayload;
            @Schema(description = "outgoing port", example = "5060")
            @JsonProperty("voip_objective_out_port")
            private Integer voipObjectiveOutPort;

            @Schema(description = "incomming mean jitter in nanoseconds", example = "1533211")
            @JsonProperty("voip_result_in_mean_jitter")
            private Long voipResultInMeanJitter;
            @Schema(description = "incomming longest errorless sequence", example = "100")
            @JsonProperty("voip_result_in_long_seq")
            private Integer voipResultInLongSeq;
            @Schema(description = "outgoing skew in nanoseconds", example = "-181283337")
            @JsonProperty("voip_result_out_skew")
            private Long voipResultOutSkew;

            @Getter
            @Builder
            @AllArgsConstructor
            public static class DnsResultEntry {
                @Schema(description = "dns result address - ipv4 or ipv6", example = "31.13.84.36")
                @JsonProperty("dns_result_address")
                private String dnsResultAddress;
                @Schema(description = "time to live of result in seconds?", example = "52")
                @JsonProperty("dns_result_ttl")
                private String dnsResultTtl;
            }

            @Getter
            @Builder
            @AllArgsConstructor
            public static class TraceRouteDetailsItem {
                @Schema(description = "ip address of the host", example = "62.197.195.x")
                @JsonProperty("host")
                private String host;
                @Schema(description = "duration of contacting address in nanoseconds", example = "86973384")
                @JsonProperty("time")
                private Long time;
            }
        }
    }
}
