package com.rtr.nettest.service;

import com.rtr.nettest.request.RtrSettingsRequest;
import com.rtr.nettest.response.SettingsResponse;

public interface RtrSettingsService {
    SettingsResponse getSettings(RtrSettingsRequest rtrSettingsRequest, String clientIpRaw);
}
