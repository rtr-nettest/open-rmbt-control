package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class ImplausibleRequest {

    @JsonProperty(value = "comment")
    private final String comment;

    @JsonProperty(value = "implausible")
    private final Boolean implausible;

    @JsonProperty(value = "uuid")
    private final String uuid;
}
