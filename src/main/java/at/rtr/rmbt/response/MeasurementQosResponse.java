package at.rtr.rmbt.response;

import at.rtr.rmbt.enums.TestType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class MeasurementQosResponse {

    @JsonProperty(value = "objectives")
    private final Map<TestType, List<QosParamsResponse>> objectives;

    @JsonProperty(value = "testDuration")
    private final Integer testDuration;

    @JsonProperty(value = "testNumThreads")
    private final Integer testNumThreads;

    @JsonProperty(value = "testNumPings")
    private final Integer testNumPings;

    @JsonProperty(value = "clientRemoteIp")
    private final String clientRemoteIp;

    @JsonProperty(value = "error")
    private final List<String> error;

}
