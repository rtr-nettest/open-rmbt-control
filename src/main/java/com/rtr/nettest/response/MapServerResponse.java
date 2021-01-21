package com.rtr.nettest.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class MapServerResponse {

    private final Long port;

    private final String host;

    private final boolean ssl;
}
