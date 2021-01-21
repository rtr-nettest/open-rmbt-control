package com.rtr.nettest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VersionResponse {

    @JsonProperty(value = "control_server_version")
    private final String controlServerVersion;
}
