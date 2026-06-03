package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.request.settings.admin.update.AdminUpdateSettingsRequest;
import at.rtr.rmbt.response.SettingsResponse;
import at.rtr.rmbt.response.settings.admin.update.AdminSettingsResponse;
import at.rtr.rmbt.service.RtrSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * RTR settings controller class.
 */
@Tag(name = "Settings")
@RestController
@RequiredArgsConstructor
public class RTRSettingsController {

    private final RtrSettingsService rtrSettingsService;

    /**
     * Get settings.
     *
     * @param request the Request
     * @return the Settings
     */
    @PostMapping(URIConstants.SETTINGS_URL)
    @Operation(summary = "Get settings of control server", description = "Registers device, returns settings of the control server, map server and other settings and device UUID (creates new UUID when it is not sent in request body)")
    public SettingsResponse getSettings(@RequestBody RtrSettingsRequest request) {
        SettingsResponse settings = rtrSettingsService.getSettings(request);
        return settings;
    }

    /**
     * Create settings.
     *
     * @param adminSettingsRequest the Admin settings request
     */
    @PostMapping(URIConstants.ADMIN_SETTING)
    @Operation(summary = "Set settings of control server")
    public void createSettings(@RequestBody AdminSettingsRequest adminSettingsRequest) {
        rtrSettingsService.createSettings(adminSettingsRequest);
    }

    /**
     * Returns the Settings.
     *
     * @return the Settings
     */
    @GetMapping(URIConstants.ADMIN_SETTING)
    @Operation(summary = "Get settings of control server")
    public AdminSettingsResponse getSettings() {
        return rtrSettingsService.getAllSettings();
    }

    /**
     * Admin update settings.
     *
     * @param adminUpdateSettingsRequest the Admin update settings request
     */
    @PutMapping(URIConstants.ADMIN_SETTING)
    @Operation(summary = "Update settings of control server")
    public void adminUpdateSettings(@RequestBody AdminUpdateSettingsRequest adminUpdateSettingsRequest) {
        rtrSettingsService.updateSettings(adminUpdateSettingsRequest);
    }
}