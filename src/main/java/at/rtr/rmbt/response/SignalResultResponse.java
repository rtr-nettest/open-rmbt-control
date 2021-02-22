package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class SignalResultResponse {

    @JsonProperty(value = "test_uuid")
    private final UUID testUUID;
}
