package ibm.ws.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @authorHamed
 */
public class ListResponse {

    private List payloadList;

    public List getPayloadList() {
        return payloadList;
    }

    public void setPayloadList(List payloadList) {
        this.payloadList = payloadList;
    }

    public <T> List<T> getByType(Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (payloadList != null)
            for (Object o : payloadList) {
                result.add(JsonCodec.toObject((Map) o, clazz));
            }
        return result;
    }


}