package at.rtr.rmbt.service;

import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.request.settings.admin.update.AdminUpdateSettingsRequest;
import at.rtr.rmbt.response.SettingsResponse;
import at.rtr.rmbt.response.settings.admin.update.AdminSettingsResponse;

public interface RtrSettingsService {
    SettingsResponse getSettings(RtrSettingsRequest rtrSettingsRequest);

    void createSettings(AdminSettingsRequest adminSettingsRequest);

    AdminSettingsResponse getAllSettings();

    void updateSettings(AdminUpdateSettingsRequest adminUpdateSettingsRequest);
}
