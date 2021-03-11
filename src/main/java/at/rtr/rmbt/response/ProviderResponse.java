package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProviderResponse {

    @JsonProperty(value = "id")
    private final Long id;

    @JsonProperty(value = "name")
    private final String name;
}
