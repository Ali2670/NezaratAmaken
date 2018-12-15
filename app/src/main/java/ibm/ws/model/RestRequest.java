package ibm.ws.model;

import com.ibm.hamsafar.utils.Tools;

/**
 * @author Hamed
 */
public class RestRequest extends RestContainer implements Cloneable {
    private String senderName;
    private String macAddress;
    private String sessionId;
    //in seconds
    private Long timeout;
    private String service;
    private String action;


    public RestRequest() {
    }

    public RestRequest(String service, String action) {
        this.service = service;
        this.action = action;
        String session = Tools.getDefaults("SESSION_ID");
        String user = Tools.getDefaults("USER_NAME");
        this.sessionId = (session == null || session.trim().length() == 0) ? null : session;
        this.senderName = (user == null || user.trim().length() == 0) ? null : session;
        this.macAddress = Tools.MacAddress();
        this.setPayload(new EmptyRequest());
    }


    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }


    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestRequest that = (RestRequest) o;

        if (senderName != null ? !senderName.equals(that.senderName) : that.senderName != null)
            return false;
        if (macAddress != null ? !macAddress.equals(that.macAddress) : that.macAddress != null)
            return false;
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null)
            return false;
        if (timeout != null ? !timeout.equals(that.timeout) : that.timeout != null) return false;
        if (service != null ? !service.equals(that.service) : that.service != null) return false;
        return action != null ? action.equals(that.action) : that.action == null;
    }

    @Override
    public int hashCode() {
        int result = senderName != null ? senderName.hashCode() : 0;
        result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (timeout != null ? timeout.hashCode() : 0);
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RestRequest{" +
                "senderName='" + senderName + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", timeout=" + timeout +
                ", service='" + service + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

    public RestRequest clone() {
        RestRequest clone = new RestRequest();
        clone.senderName = this.senderName;
        clone.macAddress = this.macAddress;
        clone.sessionId = this.sessionId;
        clone.service = this.service;
        clone.action = this.action;
        clone.timeout = this.timeout;
        clone.payload = this.payload;
        clone.payloadClass = this.payloadClass;
        clone.type = this.type;
        return clone;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
