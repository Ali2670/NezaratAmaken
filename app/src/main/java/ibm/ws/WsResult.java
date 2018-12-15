package ibm.ws;

/**
 * Created by Hamed on 01/12/2017.
 */

public class WsResult {
    Object object;
    Boolean resp;
    Integer err;
    String methodType;

    public WsResult() {
    }

    public WsResult(Object object, Integer err, String methodType) {
        this.object = object;
        this.err = err;
        this.methodType = methodType;
    }

    public WsResult(Object object, Integer err) {
        this.object = object;
        this.err = err;
    }

    public Boolean getResp() {
        if (resp == null)
            resp = false;
        return resp;
    }

    public void setResp(Boolean resp) {
        this.resp = resp;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Integer getErr() {
        return err;
    }

    public void setErr(Integer err) {
        this.err = err;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
}
