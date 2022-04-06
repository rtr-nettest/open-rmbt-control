package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.ApplicationVersionResponse;
import at.rtr.rmbt.service.ApplicationVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("Version")
@RestController
@RequiredArgsConstructor
public class ApplicationVersionController {

    private final ApplicationVersionService applicationVersionService;

    @ApiOperation(value = "Get version of application")
    @GetMapping(URIConstants.VERSION)
    public ApplicationVersionResponse getApplicationVersion() {
        return applicationVersionService.getApplicationVersion();
    }
}
