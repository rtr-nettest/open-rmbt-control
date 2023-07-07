package at.rtr.rmbt.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RtrErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseStatus(HttpStatus.OK)
    public String handleError(HttpServletRequest request) {
        return "error";
    }
}
