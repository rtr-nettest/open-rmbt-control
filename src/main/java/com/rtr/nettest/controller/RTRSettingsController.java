package com.rtr.nettest.controller;

import com.rtr.nettest.request.AdminSettingsRequest;
import com.rtr.nettest.request.RtrSettingsRequest;
import com.rtr.nettest.response.SettingsResponse;
import com.rtr.nettest.service.RtrSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.rtr.nettest.constant.URIConstants.ADMIN_SETTING;
import static com.rtr.nettest.constant.URIConstants.SETTINGS_URL;

@RestController
@RequiredArgsConstructor
public class RTRSettingsController {

    private final RtrSettingsService rtrSettingsService;

    @PostMapping(SETTINGS_URL)
    public SettingsResponse getSettings(@RequestBody RtrSettingsRequest request) {
        return rtrSettingsService.getSettings(request);
    }

    @PostMapping(ADMIN_SETTING)
    public void createSettings(@RequestBody AdminSettingsRequest adminSettingsRequest) {
        rtrSettingsService.createSettings(adminSettingsRequest);
    }
}