package com.rtr.nettest.advice;

import com.rtr.nettest.exception.ClientNotFoundByNameException;
import com.rtr.nettest.exception.NotSupportedClientVersionException;
import com.rtr.nettest.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

import static com.rtr.nettest.constant.ErrorMessage.*;

@Slf4j
@RestControllerAdvice
public class RtrAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ErrorResponse handleSQLException(SQLException ex) {
        return new ErrorResponse(SQL_ERROR_MESSAGE);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ClientNotFoundByNameException.class)
    public ErrorResponse handleClientNotFoundByNameException(ClientNotFoundByNameException ex) {
        return new ErrorResponse(ERROR_DB_GET_CLIENTTYPE);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotSupportedClientVersionException.class)
    public ErrorResponse handleNotSupportedClientVersionException(NotSupportedClientVersionException ex) {
        return new ErrorResponse(ERROR_CLIENT_VERSION);
    }
}
