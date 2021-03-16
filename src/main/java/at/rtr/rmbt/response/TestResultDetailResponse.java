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
public class TestResultDetailResponse {

    @JsonProperty(value = "testresultdetail")
    private final List<TestResultDetailContainerResponse> testResultDetailContainerResponse;
}
