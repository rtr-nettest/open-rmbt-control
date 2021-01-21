package com.rtr.nettest.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SettingsResponse {

    private final List<SettingResponse> settings;
}
