package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.model.RequestLog;
import at.rtr.rmbt.request.SyncRequest;
import at.rtr.rmbt.response.SyncResponse;
import at.rtr.rmbt.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api("Client")
@RestController
@RequiredArgsConstructor
public class ClientController {
    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    @ApiOperation(value = "Synchronize device")
    @PostMapping(URIConstants.SYNC)
    public SyncResponse sync(@RequestBody SyncRequest request, @RequestHeader Map<String, String> headers) throws JsonProcessingException {
        logger.info(objectMapper.writeValueAsString(new RequestLog(headers, request)));
        return clientService.sync(request);
    }
}
