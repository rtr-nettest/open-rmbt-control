package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class TermAndConditionsResponse {

    @JsonProperty(value = "version")
    private final Long version;

    @JsonProperty(value = "url")
    private final String url;

    @JsonProperty(value = "ndt_url")
    private final String ndtUrl;
}
