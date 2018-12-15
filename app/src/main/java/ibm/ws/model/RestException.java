package ibm.ws.model;

/**
 * @authorHamed
 */
public class RestException extends Exception {
    public static final String REST_CONNECTION_ERROR = "2000";
    public static final String REST_TIMEOUT_ERROR = "2001";
    public static final String REST_ENCRPTION_ERROR = "3001";
    public static final String REST_SERVER_ENCRPTION_ERROR = "3002";
    public static final String REST_SERVER_TOKEN_INVALID = "3004";
    public static final String DEVICE_NOT_HAVE_MAC_ADDRESS = "900002";
    public static final String CHECK_INTERNET_CONNECTION = "900009";


    private String exceptionCode;
    private String exceptionMessage;

    public RestException() {
    }

    public RestException(String exceptionCode, Object... param) {
        this.exceptionCode = exceptionCode;
//        this.exceptionMessage = IxProps.property(exceptionCode, param);
    }

    public RestException(Exception e, String exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.exceptionMessage = e.getMessage();
    }

    public String getExceptionCode() {
        return this.exceptionCode;
    }

    public RestException setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
        return this;
    }

    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    public RestException setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    public String toString() {
        return "{exceptionCode='" + this.exceptionCode + '\'' + ", exceptionMessage='" + this.exceptionMessage + '\'' + '}';
    }

}
