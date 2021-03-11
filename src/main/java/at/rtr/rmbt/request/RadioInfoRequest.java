package at.rtr.rmbt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RadioInfoRequest {

    @JsonProperty(value = "cells")
    private final List<RadioCellRequest> cells;

    @JsonProperty(value = "signals")
    private final List<RadioSignalRequest> signals;
}
