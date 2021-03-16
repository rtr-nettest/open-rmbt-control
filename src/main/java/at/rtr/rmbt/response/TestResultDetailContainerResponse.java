package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class TestResultDetailContainerResponse {

    @JsonProperty(value = "title")
    private final String title;

    @JsonProperty(value = "value")
    private final String value;

    @JsonProperty(value = "timezone")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String timezone;

    @JsonProperty(value = "time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Long time;

    @JsonProperty(value = "open_uuid")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String openUUID;

    @JsonProperty(value = "open_test_uuid")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String openTestUUID;
}
