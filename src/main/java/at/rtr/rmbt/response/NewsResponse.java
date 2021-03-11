package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewsResponse {

    @JsonProperty(value = "uid")
    private final Long uid;

    @JsonProperty(value = "title")
    private final String title;

    @JsonProperty(value = "text")
    private final String text;
}
