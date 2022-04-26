package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.response.ProviderResponse;
import at.rtr.rmbt.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Provider")
@RestController
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @Operation(summary = "Get list of all providers")
    @GetMapping(URIConstants.PROVIDERS)
    public List<ProviderResponse> getAllProviders() {
        return providerService.getAllProviders();
    }
}
