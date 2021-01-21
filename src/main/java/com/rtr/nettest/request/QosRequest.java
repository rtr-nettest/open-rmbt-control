package com.rtr.nettest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QosRequest {

    @JsonProperty(value = "supports_info")
    private final boolean supportsInfo;
}
