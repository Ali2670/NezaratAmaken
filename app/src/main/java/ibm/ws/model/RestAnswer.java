package ibm.ws.model;

/**
 * @author Hamed
 */
public class RestAnswer extends RestContainer {

    public RestAnswer() {
    }

    @Override
    public String toString() {
        return "RestAnswer{" +
                "conversationId='" + conversationId + '\'' +
                super.toString() +
                '}';
    }
}
