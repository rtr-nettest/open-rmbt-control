package at.rtr.rmbt.advice;

import at.rtr.rmbt.exception.*;
import org.junit.Test;

import java.sql.SQLException;

import static at.rtr.rmbt.constant.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RtrAdviceTest {
    private final RtrAdvice rtrAdvice = new RtrAdvice();

    @Test
    public void handleSQLException_whenCommonData_expectErrorResponse() {
        assertEquals(SQL_ERROR_MESSAGE, rtrAdvice.handleSQLException(new SQLException()).getError().get(0));
    }

    @Test
    public void handleClientNotFoundByNameException_whenCommonData_expectErrorResponse() {
        assertEquals(
            ERROR_DB_GET_CLIENTTYPE,
            rtrAdvice.handleClientNotFoundByNameException(new ClientNotFoundByNameException()).getError().get(0)
        );
    }

    @Test
    public void handleNotSupportedClientVersionException_whenCommonData_expectErrorResponse() {
        assertEquals(
            ERROR_CLIENT_VERSION,
            rtrAdvice.handleNotSupportedClientVersionException(new NotSupportedClientVersionException()).getError().get(0)
        );
    }

    @Test
    public void handleClientNotFoundException_whenCommonData_expectErrorResponse() {
        assertEquals("test", rtrAdvice.handleClientNotFoundException(new ClientNotFoundException("test")).getError().get(0));
    }

    @Test
    public void handleInvalidSequenceException_whenCommonData_expectErrorResponse() {
        assertEquals(ERROR_INVALID_SEQUENCE, rtrAdvice.handleInvalidSequenceException(new InvalidSequenceException()).getError().get(0));
    }

    @Test
    public void handleTestNotFoundException_whenCommonData_expectErrorResponse() {
        assertEquals("test", rtrAdvice.handleTestNotFoundException(new TestNotFoundException("test")).getError().get(0));
    }

    @Test
    public void handleIllegalArgumentException_whenCommonData_expectErrorResponse() {
        assertEquals("test", rtrAdvice.handleIllegalArgumentException(new IllegalArgumentException("test")).getError().get(0));
    }
}
