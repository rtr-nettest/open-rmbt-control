package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.facade.TestSettingsFacade;
import at.rtr.rmbt.request.TestSettingsRequest;
import at.rtr.rmbt.response.TestSettingsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Tag(name = "Test Settings")
@RestController
@RequestMapping(URIConstants.REGISTRATION_URL)
@RequiredArgsConstructor
public class RegistrationController {

    private final TestSettingsFacade testSettingsFacade;

    @Operation(summary = "Update test settings", description = "Request to update configuration for basic test")
    @PostMapping
    public TestSettingsResponse updateTestSettings(@RequestBody TestSettingsRequest testSettingsRequest, HttpServletRequest request, @RequestHeader Map<String, String> headers) {
        TestSettingsResponse testSettingsResponse = testSettingsFacade.updateTestSettings(testSettingsRequest, request, headers);
        return testSettingsResponse;
    }
}
