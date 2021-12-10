package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.request.settings.admin.update.AdminUpdateSettingsRequest;
import at.rtr.rmbt.response.SettingsResponse;
import at.rtr.rmbt.response.settings.admin.update.AdminSettingsResponse;
import at.rtr.rmbt.service.RtrSettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RTRSettingsController {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(RTRSettingsController.class);
    private final RtrSettingsService rtrSettingsService;

    @PostMapping(URIConstants.SETTINGS_URL)
    @ApiOperation(value = "Get settings of control server", notes = "Registers device, returns settings of the control server, map server and other settings and device UUID (creates new UUID when it is not sent in request body)")
    public SettingsResponse getSettings(@RequestBody RtrSettingsRequest request,  @RequestHeader Map<String, String> headers) throws JsonProcessingException {
        logger.info(objectMapper.writeValueAsString(request));
        logger.info(objectMapper.writeValueAsString(headers));
        SettingsResponse settings = rtrSettingsService.getSettings(request);
        logger.info(objectMapper.writeValueAsString(settings));
        return settings;
    }

    @PostMapping(URIConstants.ADMIN_SETTING)
    @ApiOperation(value = "Set settings of control server")
    public void createSettings(@RequestBody AdminSettingsRequest adminSettingsRequest) {
        rtrSettingsService.createSettings(adminSettingsRequest);
    }

    @GetMapping(URIConstants.ADMIN_SETTING)
    @ApiOperation(value = "Get settings of control server")
    public AdminSettingsResponse getSettings() {
        return rtrSettingsService.getAllSettings();
    }

    @PutMapping(URIConstants.ADMIN_SETTING)
    @ApiOperation(value = "Update settings of control server")
    public void adminUpdateSettings(@RequestBody AdminUpdateSettingsRequest adminUpdateSettingsRequest) {
        rtrSettingsService.updateSettings(adminUpdateSettingsRequest);
    }
}