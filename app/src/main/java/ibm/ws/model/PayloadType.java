package ibm.ws.model;

/**
 * @authorHamed
 */
public enum PayloadType {
    EXCEPTION,
    OK,
    FAILED,
    NO_RESULT,
    ENDPOINT_EXCEPTION;

    private PayloadType() {
    }
}
