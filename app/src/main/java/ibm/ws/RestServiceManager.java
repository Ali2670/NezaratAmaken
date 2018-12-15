package ibm.ws;


import com.ibm.hamsafar.Application.AppStart;
import com.ibm.hamsafar.utils.Constants;
import com.ibm.hamsafar.utils.Tools;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

import ibm.ws.model.EmptyRequest;
import ibm.ws.model.EmptyResponse;
import ibm.ws.model.ExceptionPayload;
import ibm.ws.model.JsonCodec;
import ibm.ws.model.ListResponse;
import ibm.ws.model.PayloadType;
import ibm.ws.model.RestAnswer;
import ibm.ws.model.RestException;
import ibm.ws.model.RestRequest;


/**
 * @author Hamed
 */

public class RestServiceManager {
    //    ClientHttpRequestFactory factor;
    String[] baseUrl = new String[3];

    public RestServiceManager() {
//        factor = clientHttpRequestFactory();
        //Arvand Url
//        baseUrl = "http://37.32.45.77:80/ws/handler";
        baseUrl[0] = Tools.URL_MEDIA[0] + "ws/handler";
        baseUrl[1] = Tools.URL_MEDIA[1] + "ws/handler";
//        baseUrl[2] = Tools.URL_MEDIA[2] + "ws/handler";
    }

    private static ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(120 * 1000);
        factory.setConnectTimeout(21 * 1000);
        return factory;
    }

    private ResponseEntity<RestAnswer> callRestService(RestRequest requestParamaters) throws ConnectException, RestException {
        if (!Tools.isNetworkAvailable(AppStart.getContext())) {
            throw new RestException(RestException.CHECK_INTERNET_CONNECTION);

        }
        RestTemplate restTemplate = new RestTemplate();
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(1000 * 40);
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(1000 * 20);

        ResponseEntity<RestAnswer> response = null;
//        RestRequest restRequest = new RestRequest();
        if (requestParamaters.getPayload() == null)
            requestParamaters.setPayload(new EmptyRequest());
        requestParamaters.setSessionId(Tools.getDefaults(Constants.SessionId, AppStart.getContext()));
        requestParamaters.setSenderName(Tools.getDefaults(Constants.UserName, AppStart.getContext()));
        try {
            if (Tools.MacAddress() != null) {
                requestParamaters.setMacAddress(Tools.MacAddress());
            } else {
                throw new RestException(RestException.DEVICE_NOT_HAVE_MAC_ADDRESS);
            }
        } catch (Exception e) {
            throw new RestException(RestException.DEVICE_NOT_HAVE_MAC_ADDRESS);
        }
        try {
          /*  if(!requestParamaters.getAction().equals("LastVersion")){
                AdtBastaWSSec basta = new AdtBastaWSSec();
                String str = JsonCodec.toString(requestParamaters.getPayload());
                String v1 = basta.envelopMessage(requestParamaters.getMacAddress(), requestParamaters.getService(), str, DateUtil.getCurrentTime());

                requestParamaters.setPayload(v1);
            }*/
            response = restTemplate.postForEntity(
                                                    baseUrl[Tools.SERVER_ID],
                                                    requestParamaters,
                                                    RestAnswer.class);
        } catch (RestClientException cex) {
            if (Tools.SERVER_ID < 1) {
                try {
                    Tools.SERVER_ID++;
                    return callRestService(requestParamaters);
                } catch (Exception e) {
                    throw new ConnectException(baseUrl[Tools.SERVER_ID]);
                }
            } else {
                if (cex.getRootCause() instanceof ConnectException)
                    System.out.println("Connection Error");
                if (cex.getRootCause() instanceof SocketTimeoutException)
                    System.out.println("Read Error");
                Integer old = Tools.SERVER_ID;
                Tools.SERVER_ID = 0;
                throw new ConnectException(baseUrl[old]);
            }
        }
        return response;
    }


    public void CallMethod(RestRequest requestParamaters) throws RestException, ConnectException {
        ResponseEntity<RestAnswer> restAnswerResponseEntity = callRestService(requestParamaters);
        if (restAnswerResponseEntity.getBody().getType().equals(PayloadType.EXCEPTION)) {
            ExceptionPayload exceptionPayload = JsonCodec.toObject((Map) restAnswerResponseEntity.getBody().getPayload(), ExceptionPayload.class);
            throw new RestException(exceptionPayload.getExceptionCode(), exceptionPayload.getExceptionMessage());
        }

    }

    public Object getListResponse(Class clazzType, RestRequest requestParamaters) throws RestException, ConnectException, Exception {
        ResponseEntity<RestAnswer> restAnswerResponseEntity = callRestService(requestParamaters);
        if (restAnswerResponseEntity.getBody().getType().equals(PayloadType.EXCEPTION)) {
            ExceptionPayload exceptionPayload = JsonCodec.toObject((Map) restAnswerResponseEntity.getBody().getPayload(), ExceptionPayload.class);
            throw new RestException(exceptionPayload.getExceptionCode(), exceptionPayload.getExceptionMessage());
        } else if (restAnswerResponseEntity.getBody().getType().equals(PayloadType.NO_RESULT)) {
            return null;
        }
        if (restAnswerResponseEntity.getBody().getPayload() == null ||
                restAnswerResponseEntity.getBody().getPayload() instanceof EmptyResponse) {
            return null;
        }

        ListResponse ll = JsonCodec.toObject((Map) restAnswerResponseEntity.getBody().getPayload(), ListResponse.class);

        return ll.getByType(clazzType);
    }


    public Object getObjectResponse(Class clazzType, RestRequest requestParamaters) throws RestException, ConnectException {
        ResponseEntity<RestAnswer> restAnswerResponseEntity = callRestService(requestParamaters);

        if (restAnswerResponseEntity.getBody().getType().equals(PayloadType.EXCEPTION)) {
            ExceptionPayload exceptionPayload = JsonCodec.toObject((Map) restAnswerResponseEntity.getBody().getPayload(), ExceptionPayload.class);
            throw new RestException(exceptionPayload.getExceptionCode(), exceptionPayload.getExceptionMessage());
        } else if (restAnswerResponseEntity.getBody().getType().equals(PayloadType.NO_RESULT)) {
            return null;
        }

        try {
            Object paylaod = restAnswerResponseEntity.getBody().getPayload();
            if (paylaod instanceof Map) {
                return JsonCodec.toObject((Map) restAnswerResponseEntity.getBody().getPayload(), clazzType);
            } else if (paylaod instanceof Integer) {
                return Long.valueOf(restAnswerResponseEntity.getBody().getPayload().toString());
            }
            return restAnswerResponseEntity.getBody().getPayload();
        } catch (Exception e) {
            throw new RestException(e.getMessage());
        }
    }

}
