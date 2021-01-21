package com.rtr.nettest.service;

import com.rtr.nettest.request.SettingsRequest;
import com.rtr.nettest.response.SettingsResponse;

public interface SettingsService {

    SettingsResponse getSettings(SettingsRequest settingsRequest, String clientIpRaw);
}
