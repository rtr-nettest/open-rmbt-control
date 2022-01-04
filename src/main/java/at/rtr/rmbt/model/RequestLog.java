package at.rtr.rmbt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class RequestLog {
    @JsonProperty("headers")
    private Map<String, String> headers;

    @JsonProperty("request")
    private Object request;
}
