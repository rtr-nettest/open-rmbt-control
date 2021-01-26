package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.service.RtrSettingsService;
import at.rtr.rmbt.response.SettingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RTRSettingsController {

    private final RtrSettingsService rtrSettingsService;

    @PostMapping(URIConstants.SETTINGS_URL)
    public SettingsResponse getSettings(@RequestBody RtrSettingsRequest request) {
        return rtrSettingsService.getSettings(request);
    }

    @PostMapping(URIConstants.ADMIN_SETTING)
    public void createSettings(@RequestBody AdminSettingsRequest adminSettingsRequest) {
        rtrSettingsService.createSettings(adminSettingsRequest);
    }
}