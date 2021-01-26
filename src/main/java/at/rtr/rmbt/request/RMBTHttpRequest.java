package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RMBTHttpRequest {

    @JsonProperty(value = "RMBThttp")
    private final boolean rmbtHttp;
}
