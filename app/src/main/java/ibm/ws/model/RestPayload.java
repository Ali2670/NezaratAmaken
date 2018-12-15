package ibm.ws.model;

import java.util.Arrays;

/**
 * @author Hamed
 */
public class RestPayload {
    Object[] params;

    public Object[] getParams() {
        return params;
    }

    public RestPayload setParams(Object[] params) {
        this.params = params;
        return this;
    }

    @Override
    public String toString() {
        return "RestPayload{" +
                "params=" + Arrays.toString(params) +
                '}';
    }
}
