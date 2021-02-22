package at.rtr.rmbt.constant;

public interface ErrorMessage {
    String SQL_ERROR_MESSAGE = "SQL Error requesting from database.";
    String ERROR_CLIENT_VERSION = "Your client version is not supported.";
    String ERROR_DB_GET_CLIENTTYPE = "Could not find client type in database";
    String CLIENT_NOT_FOUND = "No client found by id %s.";
    String ERROR_INVALID_SEQUENCE = "ERROR_INVALID_SEQUENCE";
}
