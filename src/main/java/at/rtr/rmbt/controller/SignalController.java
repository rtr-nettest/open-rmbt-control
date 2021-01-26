package at.rtr.rmbt.controller;


import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.response.SignalResponse;
import at.rtr.rmbt.service.SignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @PostMapping(URIConstants.SIGNAL_REQUEST)
    @ResponseStatus(HttpStatus.CREATED)
    public SignalResponse registerSignal(HttpServletRequest httpServletRequest, @RequestBody SignalRequest signalRequest) {
        return signalService.registerSignal(signalRequest, httpServletRequest);
    }
}
