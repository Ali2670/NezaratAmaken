package ibm.ws.model;

/**
 * @authorHamed
 */
public final class ExceptionPayload {
    private String exceptionCode;
    private String exceptionMessage;

    public ExceptionPayload() {
    }

    public ExceptionPayload(String exceptionCode, Object... param) {
        this.exceptionCode = exceptionCode;
//        this.exceptionMessage = IxProps.property(exceptionCode, param);
    }

    public ExceptionPayload(Exception e, String exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.exceptionMessage = e.getMessage();
    }

    public String getExceptionCode() {
        return this.exceptionCode;
    }

    public ExceptionPayload setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
        return this;
    }

    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    public ExceptionPayload setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    public String toString() {
        return "{exceptionCode='" + this.exceptionCode + '\'' + ", exceptionMessage='" + this.exceptionMessage + '\'' + '}';
    }
}
