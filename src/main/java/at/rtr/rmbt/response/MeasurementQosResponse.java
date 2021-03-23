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

    @JsonProperty(value = "test_duration")
    private final Integer testDuration;

    @JsonProperty(value = "test_numthreads")
    private final Integer testNumThreads;

    @JsonProperty(value = "test_numpings")
    private final Integer testNumPings;

    @JsonProperty(value = "client_remote_ip")
    private final String clientRemoteIp;

    @JsonProperty(value = "error")
    private final List<String> error;

}
