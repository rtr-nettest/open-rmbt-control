package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class TestResultRequest {

    @JsonProperty(value = "language")
    private final String language;

    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @JsonProperty(value = "capabilities")
    private final CapabilitiesRequest capabilitiesRequest;
}
