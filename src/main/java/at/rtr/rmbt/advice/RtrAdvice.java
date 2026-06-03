package at.rtr.rmbt.advice;

import at.rtr.rmbt.exception.ClientNotFoundByNameException;
import at.rtr.rmbt.exception.ClientNotFoundException;
import at.rtr.rmbt.exception.EmptyClientVersionException;
import at.rtr.rmbt.exception.InvalidSequenceException;
import at.rtr.rmbt.exception.NotSupportedClientVersionException;
import at.rtr.rmbt.exception.SyncException;
import at.rtr.rmbt.exception.TestNotFoundException;
import at.rtr.rmbt.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

import static at.rtr.rmbt.constant.ErrorMessage.ERROR_CLIENT_VERSION;
import static at.rtr.rmbt.constant.ErrorMessage.ERROR_DB_GET_CLIENTTYPE;
import static at.rtr.rmbt.constant.ErrorMessage.ERROR_INVALID_SEQUENCE;
import static at.rtr.rmbt.constant.ErrorMessage.SQL_ERROR_MESSAGE;

/**
 * Rtr advice class.
 */
@Slf4j
@RestControllerAdvice
public class RtrAdvice {

    /**
     * Handle SQL exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ErrorResponse handleSQLException(SQLException ex) {
        return new ErrorResponse(SQL_ERROR_MESSAGE);
    }

    /**
     * Handle client not found by name exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ClientNotFoundByNameException.class)
    public ErrorResponse handleClientNotFoundByNameException(ClientNotFoundByNameException ex) {
        return new ErrorResponse(ERROR_DB_GET_CLIENTTYPE);
    }

    /**
     * Handle not supported client version exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotSupportedClientVersionException.class)
    public ErrorResponse handleNotSupportedClientVersionException(NotSupportedClientVersionException ex) {
        return new ErrorResponse(ERROR_CLIENT_VERSION);
    }

    /**
     * Sem version exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(EmptyClientVersionException.class)
    public ErrorResponse semVersionException(EmptyClientVersionException ex) {
        return ErrorResponse.empty();
    }

    /**
     * Handle client not found exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ClientNotFoundException.class)
    public ErrorResponse handleClientNotFoundException(ClientNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    /**
     * Handle invalid sequence exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidSequenceException.class)
    public ErrorResponse handleInvalidSequenceException(InvalidSequenceException ex) {
        return new ErrorResponse(ERROR_INVALID_SEQUENCE);
    }

    /**
     * Handle test not found exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TestNotFoundException.class)
    public ErrorResponse handleTestNotFoundException(TestNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    /**
     * Handle illegal argument exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    /**
     * Handle sync exception.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SyncException.class)
    public ErrorResponse handleSyncException(SyncException ex) {
        return new ErrorResponse(ex.getMessage());
    }
    // Catches JSON parsing errors (e.g., invalid format for time_ns)
    /**
     * Handle http message not readable.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        // This will log the exact parameter that caused parsing to fail
        Throwable rootCause = ex.getMostSpecificCause();
        log.error("Failed to parse JSON request. Problem with parameter in request body. Root cause: ", rootCause);

        // Return a clean error message to client
        return new ErrorResponse("Invalid request format: " + rootCause.getMessage());
    }

    // Catches @Valid validation errors (if you add @Valid to controller)
    /**
     * Handle validation exceptions.
     *
     * @param ex the Ex
     * @return the result
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Request validation failed for fields: {}", errors);
        // Return first error or all errors
        return new ErrorResponse("Validation failed: " + errors.values().iterator().next());
    }

}

