package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class QosTestTypeDescResponse {

    @JsonProperty(value = "name")
    private final String name;

    @JsonProperty(value = "test_type")
    private final String testType;
}
