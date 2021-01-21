package com.rtr.nettest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CapabilitiesRequest {

    private final ClassificationRequest classification;

    private final QosRequest qos;

    @JsonProperty(value = "RMBThttp")
    private final RMBTHttpRequest rmbtHttpRequest;
}
