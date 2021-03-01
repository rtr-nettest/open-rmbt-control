package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.ProviderResponse;
import at.rtr.rmbt.service.ProviderService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @ApiOperation("Get list of all providers")
    @GetMapping(URIConstants.PROVIDERS)
    public List<ProviderResponse> getAllProviders() {
        return providerService.getAllProviders();
    }
}
