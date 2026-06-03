package at.rtr.rmbt.exception;

/**
 * Test not found exception class.
 */
public class TestNotFoundException extends RuntimeException {

    /**
     * Creates a new TestNotFoundException instance.
     *
     * @param message the Message
     */
    public TestNotFoundException(String message) {
        super(message);
    }
}
