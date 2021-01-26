package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.facade.TestSettingsFacade;
import at.rtr.rmbt.request.TestSettingsRequest;
import at.rtr.rmbt.response.TestSettingsResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api("Test Settings")
@RestController
@RequestMapping(URIConstants.REGISTRATION_URL)
public class RegistrationController {

    private TestSettingsFacade testSettingsFacade;

    @Autowired
    public RegistrationController(TestSettingsFacade testSettingsFacade) {
        this.testSettingsFacade = testSettingsFacade;
    }

    @ApiOperation(value = "Update test settings", notes = "Request to update configuration for basic test")
    @PostMapping
    public TestSettingsResponse updateTestSettings(@RequestBody TestSettingsRequest testSettingsRequest, HttpServletRequest request) {
        return testSettingsFacade.updateTestSettings(testSettingsRequest, request);
    }
}
