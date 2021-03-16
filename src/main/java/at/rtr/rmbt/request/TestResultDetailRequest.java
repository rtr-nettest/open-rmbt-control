package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class TestResultDetailRequest {

    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;

    @JsonProperty(value = "language")
    private final String language;
}
