package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.SyncRequest;
import at.rtr.rmbt.response.SyncResponse;
import at.rtr.rmbt.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Client")
@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Synchronize device")
    @PostMapping(URIConstants.SYNC)
    public SyncResponse sync(@RequestBody SyncRequest request) {
        return clientService.sync(request);
    }
}
