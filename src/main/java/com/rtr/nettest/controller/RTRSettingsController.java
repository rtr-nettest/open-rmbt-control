package com.rtr.nettest.controller;

import com.rtr.nettest.request.RtrSettingsRequest;
import com.rtr.nettest.response.SettingsResponse;
import com.rtr.nettest.service.RtrSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.rtr.nettest.constant.HeaderConstants.IP;
import static com.rtr.nettest.constant.URIConstants.SETTINGS_URL;

@RestController
@RequestMapping(SETTINGS_URL)
@RequiredArgsConstructor
public class RTRSettingsController {

    private final RtrSettingsService rtrSettingsService;

    @PostMapping
    public SettingsResponse getSettings(@RequestHeader(IP) String clientIpRaw,
                                        @RequestBody RtrSettingsRequest request) {
        return rtrSettingsService.getSettings(request, clientIpRaw);
    }
}