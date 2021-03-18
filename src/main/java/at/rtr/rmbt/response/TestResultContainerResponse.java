package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class TestResultContainerResponse {

    @JsonProperty(value = "testresult")
    private final List<TestResultResponse> testResultResponses;
}
