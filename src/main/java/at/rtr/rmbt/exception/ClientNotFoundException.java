package at.rtr.rmbt.exception;

/**
 * Client not found exception class.
 */
public class ClientNotFoundException extends RuntimeException {

    /**
     * Creates a new ClientNotFoundException instance.
     *
     * @param message the Message
     */
    public ClientNotFoundException(String message) {
        super(message);
    }
}
