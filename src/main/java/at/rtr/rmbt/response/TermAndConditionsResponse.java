package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class TermAndConditionsResponse {

    private final Long version;

    private final String url;

    @JsonProperty(value = "ndt_url")
    private final String ndtUrl;
}
