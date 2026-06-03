package at.rtr.rmbt.service;

import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.request.settings.admin.update.AdminUpdateSettingsRequest;
import at.rtr.rmbt.response.SettingsResponse;
import at.rtr.rmbt.response.settings.admin.update.AdminSettingsResponse;

/**
 * Rtr settings service interface.
 */
public interface RtrSettingsService {
    SettingsResponse getSettings(RtrSettingsRequest rtrSettingsRequest);

    /**
     * Create settings.
     *
     * @param adminSettingsRequest the Admin settings request
     */
    void createSettings(AdminSettingsRequest adminSettingsRequest);

    AdminSettingsResponse getAllSettings();

    /**
     * Update settings.
     *
     * @param adminUpdateSettingsRequest the Admin update settings request
     */
    void updateSettings(AdminUpdateSettingsRequest adminUpdateSettingsRequest);
}
