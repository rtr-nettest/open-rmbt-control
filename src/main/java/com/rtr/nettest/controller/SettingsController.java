package com.rtr.nettest.controller;

import com.rtr.nettest.request.SettingsRequest;
import com.rtr.nettest.response.SettingsResponse;
import com.rtr.nettest.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.rtr.nettest.constant.HeaderConstants.IP;
import static com.rtr.nettest.constant.URIConstants.SETTINGS_URL;

@RestController
@RequestMapping(SETTINGS_URL)
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @PostMapping
    public SettingsResponse getSettings(@RequestHeader(IP) String clientIpRaw,
                                        @RequestBody SettingsRequest request) {
        return settingsService.getSettings(request, clientIpRaw);
    }
}