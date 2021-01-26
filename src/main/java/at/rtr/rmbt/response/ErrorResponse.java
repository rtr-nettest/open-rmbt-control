package at.rtr.rmbt.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ErrorResponse {

    private final List<String> error = new ArrayList<>();

    public void addErrorString(String errorMessage) {
        this.error.add(errorMessage);
    }

    public ErrorResponse(String errorMessage) {
        addErrorString(errorMessage);
    }
}
