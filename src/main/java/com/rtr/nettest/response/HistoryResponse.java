package com.rtr.nettest.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HistoryResponse {

    private final List<String> devices;

    private final List<String> networks;
}
