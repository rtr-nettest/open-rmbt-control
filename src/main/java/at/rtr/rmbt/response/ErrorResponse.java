package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ErrorResponse {

    @JsonProperty(value = "error")
    private final List<String> error = new ArrayList<>();

    public void addErrorString(String errorMessage) {
        this.error.add(errorMessage);
    }

    public ErrorResponse(String errorMessage) {
        addErrorString(errorMessage);
    }

    public static ErrorResponse empty(){
        return new ErrorResponse();
    }
}
