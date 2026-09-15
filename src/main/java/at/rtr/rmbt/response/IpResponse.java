package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class IpResponse {

    @JsonProperty(value = "v")
    private final String version;

    @JsonProperty(value = "ip")
    private final String ip;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "nat_type")
    private final String natType;

    @JsonProperty(value = "is_proxy")
    private final boolean proxy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "proxy_country")
    private final String proxyCountry;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "proxy_region")
    private final String proxyRegion;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "proxy_city")
    private final String proxyCity;

}
