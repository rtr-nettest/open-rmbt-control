package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class TestResultResponse {

    @JsonProperty(value = "open_uuid")
    private final String openUUID;

    @JsonProperty(value = "open_test_uuid")
    private final String openTestUUID;

    @JsonProperty(value = "share_subject")
    private final String shareSubject;

    @JsonProperty(value = "share_text")
    private final String shareText;

    @JsonProperty(value = "timezone")
    private final String timezone;

    @JsonProperty(value = "measurement")
    private final List<TestResultMeasurementResponse> measurement;

    @JsonProperty(value = "measurement_result")
    private final MeasurementResultResponse measurementResult;

    @JsonProperty(value = "geo_long")
    private final Double geoLong;

    @JsonProperty(value = "geo_lat")
    private final Double geoLat;

    @JsonProperty(value = "location")
    private final String location;

    @JsonProperty(value = "time")
    private final Long time;

    @JsonProperty(value = "time_string")
    private final String timeString;

    @JsonProperty(value = "net")
    private final List<NetItemResponse> netItemResponses;

    @JsonProperty(value = "network_info")
    private final NetworkInfoResponse networkInfoResponse;

    @JsonProperty(value = "network_type")
    private final Integer networkType;

    @JsonProperty(value = "qoe_classification")
    private final List<QoeClassificationResponse> qoeClassificationResponses;

    @JsonProperty(value = "status")
    private final String status;
}
