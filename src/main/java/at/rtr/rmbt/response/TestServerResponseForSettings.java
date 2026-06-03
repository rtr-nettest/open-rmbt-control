package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Test server response for settings class.
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class TestServerResponseForSettings {

    @JsonProperty(value = "name")
    private final String name;

    @JsonProperty(value = "uuid")
    private final String uuid;
}
