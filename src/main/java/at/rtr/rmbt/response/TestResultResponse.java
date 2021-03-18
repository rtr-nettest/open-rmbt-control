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

    @JsonProperty(value = "measurement")
    private final List<TestResultMeasurementResponse> measurement;

    @JsonProperty(value = "open_test_uuid")
    private final String openTestUUID;

    @JsonProperty(value = "share_subject")
    private final String shareSubject;

    @JsonProperty(value = "share_text")
    private final String shareText;

    @JsonProperty(value = "time_string")
    private final String timeString;
}
