package at.rtr.rmbt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Test server not found exception class.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TestServerNotFoundException extends RuntimeException {
}
