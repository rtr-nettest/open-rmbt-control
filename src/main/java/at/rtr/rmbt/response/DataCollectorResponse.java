package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@ToString
public class DataCollectorResponse {

    @JsonProperty(value = "ip")
    private final String ip;

    @JsonProperty(value = "port")
    private final Integer port;

    @JsonProperty(value = "product")
    private final String product;

    @JsonProperty(value = "version")
    private final String version;

    @JsonProperty(value = "category")
    private final String category;

    @JsonProperty(value = "os")
    private final String os;

    @JsonProperty(value = "agent")
    private final String agent;

    @JsonProperty(value = "url")
    private final String url;

    @JsonProperty(value = "languages")
    private final List<String> languages;

    @JsonProperty(value = "headers")
    private final Map<String, String> headers;
}
