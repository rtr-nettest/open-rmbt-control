package at.rtr.rmbt.controller;

import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.service.TestServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static at.rtr.rmbt.constant.URIConstants.BY_ID;
import static at.rtr.rmbt.constant.URIConstants.TEST_SERVER;

@Tag(name = "Test server")
@RestController
@RequestMapping(TEST_SERVER)
@RequiredArgsConstructor
public class TestServerController {

    private final TestServerService testServerService;



}
