package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;

/**
 * Error response class.
 */
@Getter
@NoArgsConstructor
public class ErrorResponse {

    @JsonProperty(value = "error")
    private final List<String> error = new ArrayList<>();

    /**
     * Add error string.
     *
     * @param errorMessage the Error message
     */
    public void addErrorString(String errorMessage) {
        this.error.add(errorMessage);
    }

    /**
     * Creates a new ErrorResponse instance.
     *
     * @param errorMessage the Error message
     */
    public ErrorResponse(String errorMessage) {
        addErrorString(errorMessage);
    }

    /**
     * Empty.
     *
     * @return the result
     */
    public static ErrorResponse empty(){
        return new ErrorResponse();
    }
}
