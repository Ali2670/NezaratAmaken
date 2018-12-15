package ibm.ws.model;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.Map;

//import static mfa.ettelaat.gozarname.epass.rest.util.Constants.LOGGER;

/**
 * @author Hamed
 */
public abstract class RestContainer {
    protected String conversationId;
    protected Object payload;
    protected String payloadClass;
    protected PayloadType type;

    @SuppressWarnings("unchecked")
    public <T> T getPayload() {
        if (payload == null)
            buildPayload();
        return (T) payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
        if (payload != null) {
            if (payloadClass == null)
                payloadClass = payload.getClass().getName();
            if (payload instanceof ExceptionPayload)
                type = PayloadType.EXCEPTION;
            else
                type = PayloadType.OK;
        }
    }

    public RestContainer buildPayload() {
        try {
            if (payloadClass == null || payload == null) {
                setPayload(new EmptyResponse());
                return this;
            }
            Class clazz = Class.forName(payloadClass);
            if (payload != null && payload instanceof Map)
                payload = JsonCodec.toObject((Map) payload, clazz);
            else if (payload != null) {
                setPayload(JsonCodec.mapper.convertValue(payload, clazz));
            }
        } catch (ClassNotFoundException ignored) {
        } catch (IllegalArgumentException e) {
            UnrecognizedPropertyException ex = (UnrecognizedPropertyException) e.getCause();
            String field = ex.getPropertyName();
            String known = ex.getKnownPropertyIds().toString();
            throw new RestClientException("ix.could.not.cast.sent.message", "field", field, "known", known);
        }
        return this;
    }

    public String getPayloadClass() {
        return payloadClass;
    }

    public void setPayloadClass(String payloadClass) {
        this.payloadClass = payloadClass;
    }

    public PayloadType getType() {
        return type;
    }

    public void setType(PayloadType type) {
        this.type = type;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

}
