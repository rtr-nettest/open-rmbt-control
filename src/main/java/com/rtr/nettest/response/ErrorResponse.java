package com.rtr.nettest.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {

    private final List<String> error = new ArrayList<>();

    public void addErrorString(String errorMessage) {
        this.error.add(errorMessage);
    }

    public ErrorResponse(String errorMessage) {
        addErrorString(errorMessage);
    }
}
