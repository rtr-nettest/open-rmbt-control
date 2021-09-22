package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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


    @JsonInclude
    @JsonProperty(value = "country_geoip")
    private final String countryGeoIp;

    @JsonInclude
    @JsonProperty(value = "ip")
    private final String ip;

    @JsonInclude
    @JsonProperty(value = "port")
    private final Integer port;

    @JsonInclude
    @JsonProperty(value = "product")
    private final String product;

    @JsonInclude
    @JsonProperty(value = "version")
    private final String version;

    @JsonInclude
    @JsonProperty(value = "category")
    private final String category;

    @JsonInclude
    @JsonProperty(value = "os")
    private final String os;

    @JsonInclude
    @JsonProperty(value = "agent")
    private final String agent;

    @JsonInclude
    @JsonProperty(value = "url")
    private final String url;

    @JsonInclude
    @JsonProperty(value = "languages")
    private final List<String> languages;

    @JsonInclude
    @JsonProperty(value = "headers")
    private final Map<String, String> headers;
}
