package ibm.ws.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @authorHamed
 */
public class RestClientException extends RuntimeException {
    private static boolean printStackTrace = false;
    private String message;
    private String code;

    @JsonCreator
    public RestClientException(@JsonProperty("code") String code, @JsonProperty("message") String message) {
        this.message = message;
        this.code = code;
    }

    public RestClientException(String code, Object... params) {
        this.init(code, params);
    }

    public RestClientException(Throwable cause, String code, Object... params) {
        super(cause);
        this.init(code, params);
    }

    public static void fillPrintStackTraceProp() {
//        String stackTraceProp = IxProps.property("ix.microserver.print.stack.trace", new Object[0]);
        String stackTraceProp = "true";
        printStackTrace = !Objects.isNull(stackTraceProp) && Boolean.parseBoolean(stackTraceProp);
    }

    public static Object build(Exception e) {
        Throwable rootCause = rootCause(e);
        return rootCause instanceof NullPointerException ? rootCause : (printStackTrace ? rootCause : rootCause.getMessage());
    }

    private static Throwable rootCause(Throwable e) {
        return Objects.isNull(e.getCause()) ? e : rootCause(e.getCause());
    }

    private void init(String code, Object... params) {
        this.code = code;
//        this.message = IxProps.property(code, params);
        if (Objects.isNull(this.message)) {
            this.message = this.code;
        }

    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }

}
