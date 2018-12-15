package ibm.ws;


import com.ibm.hamsafar.utils.Tools;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import ibm.ws.model.ExceptionConstants;
import ibm.ws.model.GenericBusinessException;
import ibm.ws.model.RestException;
import ibm.ws.model.RestPayload;
import ibm.ws.model.RestRequest;


/**
 * @author Hamed
 */
public class RestCaller {
    public static RestServiceManager restServiceManager;

    public static RestServiceManager Call() {
        if (restServiceManager == null)
            restServiceManager = new RestServiceManager();
        return restServiceManager;
    }


    public static Object SingleObjectReturn(String methodName, Object... params) throws GenericBusinessException, ConnectException {
        try {
            String serviceName = "Ws";
            System.out.println("calling " + methodName);
            RestRequest request = new RestRequest(serviceName,
                    methodName);
            if (params != null && params.length > 0) {
                request.setPayload(new RestPayload().setParams(params));
                request.setPayloadClass(RestPayload.class.getCanonicalName());
            }
            Object restResult = Call().getObjectResponse(
                    Object.class, request);

            return (Object) restResult;

        } catch (ConnectException e) {
            System.out.println("Connection Exception");
            throw new ConnectException("");
        } catch (RestException e) {

            throw CreateExc(e);
        } catch (Exception e) {
            throw new GenericBusinessException(e);
        }
    }


    public static List<Object> ListObjectReturn(String methodName, Object... params) throws GenericBusinessException, ConnectException {
        try {
            String serviceName = "Ws";
            System.out.println("calling " + methodName);
            RestRequest request = new RestRequest(serviceName,
                    methodName);
            if (params != null && params.length > 0) {
                request.setPayload(new RestPayload().setParams(params));
                request.setPayloadClass(RestPayload.class.getCanonicalName());
            }
            Object restResult = Call().getListResponse(
                    Object.class, request);
            if (restResult instanceof List) {
                return (List<Object>) restResult;
            }
            return new ArrayList<>();
        } catch (ConnectException e) {
            System.out.println("Connection Exception");
            throw new ConnectException("");
        } catch (RestException e) {

            throw CreateExc(e);
        } catch (Exception e) {
            throw new GenericBusinessException(e);
        }
    }


    public static GenericBusinessException CreateExc(RestException e) {
        Integer errCode;
        try {
            errCode = Integer.valueOf(e.getExceptionCode());
            if (errCode.equals(100009) || errCode.equals(100008)) {
                Tools.setDefaults("SESSION_ID", null);
                Tools.getDefaults("USER_NAME", null);
            }
        } catch (Exception ee) {
            errCode = ExceptionConstants.UNKNOWN_EXCEPTION;
        }
        return new GenericBusinessException(errCode, e);
    }


}
