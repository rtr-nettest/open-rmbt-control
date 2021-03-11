package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class TestResponse {

    @JsonProperty(value = "testUUID")
    private final UUID testUUID;

    @JsonProperty(value = "time")
    private final ZonedDateTime time;
}
