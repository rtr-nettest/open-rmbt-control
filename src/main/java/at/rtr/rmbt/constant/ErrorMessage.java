package at.rtr.rmbt.constant;

public interface ErrorMessage {
    String SQL_ERROR_MESSAGE = "SQL Error requesting from database.";
    String ERROR_CLIENT_VERSION = "Your client version is not supported.";
    String ERROR_DB_GET_CLIENTTYPE = "Could not find client type in database";
    String CLIENT_NOT_FOUND = "No client found by id %s.";
    String ERROR_INVALID_SEQUENCE = "ERROR_INVALID_SEQUENCE";
    String TEST_NOT_FOUND = "No test found by id %s.";
    String ERROR_NETWORK_TYPE = "Illegal network_type";
    String ERROR_DOWNLOAD_INSANE = "Download invalid";
    String ERROR_UPLOAD_INSANE = "Upload invalid";
    String ERROR_PING_INSANE = "Ping invalid";
    String CLIENT_DOES_MATCH_TEST = "Client UUID does not match test";
    String REQUIRED_FIELDS_MISSING = "Required fields missing";
    String INVALID_UUID_TYPE = "Invalid uuid type";
    String QOS_TEST_RESULT_FOR_TEST_NOT_FOUND = "Qos test result not found for test with uuid %s";
}
