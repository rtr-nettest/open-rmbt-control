package com.rtr.nettest.controller;


import com.rtr.nettest.request.SignalRequest;
import com.rtr.nettest.response.SignalResponse;
import com.rtr.nettest.service.SignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.rtr.nettest.constant.URIConstants.SIGNAL_REQUEST;

@RestController
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @PostMapping(SIGNAL_REQUEST)
    @ResponseStatus(HttpStatus.CREATED)
    public SignalResponse registerSignal(HttpServletRequest httpServletRequest, @RequestBody SignalRequest signalRequest) {
        return signalService.registerSignal(signalRequest, httpServletRequest);
    }
}
