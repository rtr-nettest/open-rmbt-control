package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.facade.TestSettingsFacade;
import at.rtr.rmbt.model.RequestLog;
import at.rtr.rmbt.request.TestSettingsRequest;
import at.rtr.rmbt.response.TestSettingsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api("Test Settings")
@RestController
@RequestMapping(URIConstants.REGISTRATION_URL)
public class RegistrationController {


    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private TestSettingsFacade testSettingsFacade;

    @Autowired
    public RegistrationController(TestSettingsFacade testSettingsFacade) {
        this.testSettingsFacade = testSettingsFacade;
    }

    @ApiOperation(value = "Update test settings", notes = "Request to update configuration for basic test")
    @PostMapping
    public TestSettingsResponse updateTestSettings(@RequestBody TestSettingsRequest testSettingsRequest, HttpServletRequest request, @RequestHeader Map<String, String> headers) throws JsonProcessingException {
        logger.info(objectMapper.writeValueAsString(new RequestLog(headers, testSettingsRequest)));
        TestSettingsResponse testSettingsResponse = testSettingsFacade.updateTestSettings(testSettingsRequest, request, headers);
        logger.info(objectMapper.writeValueAsString(testSettingsResponse));
        return testSettingsResponse;
    }
}
