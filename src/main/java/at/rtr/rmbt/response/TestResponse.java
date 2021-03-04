package at.rtr.rmbt.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Getter
@EqualsAndHashCode
public class TestResponse {

    private final UUID testUUID;

    private final ZonedDateTime time;
}
