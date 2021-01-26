package at.rtr.rmbt.service;

import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.response.SettingsResponse;

public interface RtrSettingsService {
    SettingsResponse getSettings(RtrSettingsRequest rtrSettingsRequest);

    void createSettings(AdminSettingsRequest adminSettingsRequest);
}
