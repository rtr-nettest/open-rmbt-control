package com.rtr.nettest.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class NewsRequest {

    @NotNull
    private final String title;

    @NotNull
    private final String text;

    @NotNull
    private final String language;

    private final boolean active;

    private final boolean force;

    private final String platform;

    private final Long minSoftwareVersion;

    private final Long maxSoftwareVersion;

    @NotNull
    private final UUID uuid;
}
