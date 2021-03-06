package at.rtr.rmbt.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class TestServerResponseForSettings {

    private final String name;

    private final String uuid;
}
