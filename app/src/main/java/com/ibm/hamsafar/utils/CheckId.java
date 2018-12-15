package com.ibm.hamsafar.utils;

import android.os.AsyncTask;
import android.widget.Toast;

import com.ibm.hamsafar.Application.AppStart;

import java.net.ConnectException;

import co.ronash.pushe.Pushe;
import ibm.ws.WsResult;
import ibm.ws.model.GenericBusinessException;

import static ibm.ws.model.ExceptionConstants.CONNECTION_TIMEOUT_EXCEPTION;
import static ibm.ws.model.ExceptionConstants.NULL_VALUE_RETURN;
import static ibm.ws.model.ExceptionConstants.RESULT_IS_OK;

//import com.ibm.hamsafar.object.PushIdReg;

public class CheckId extends AsyncTask<String, Void, WsResult> {


    Toast toast;

    @Override
    protected WsResult doInBackground(String... params) {

        WsResult result;
        String pid = null;

        pid = Pushe.getPusheId(AppStart.getContext());
        if (pid == null || pid.equals("")) {
            result = new WsResult(null, NULL_VALUE_RETURN);
            return result;
        }


        try {
            Boolean response = null;
//            Boolean response = RestCaller.PushSend("Push", new PushIdReg(pid));

            if (response == null) {
                result = new WsResult(null, NULL_VALUE_RETURN);
            } else {


                result = new WsResult(response, RESULT_IS_OK);


            }
            int a = 3;
            if (a == 1) {

                throw new GenericBusinessException("");
            } else if (a == 2) {
                throw new ConnectException("");
            }
        } catch (GenericBusinessException e) {
            result = new WsResult(null, e.getErrCode());

        } catch (ConnectException e) {
            result = new WsResult(null, CONNECTION_TIMEOUT_EXCEPTION);
        }
        return result;

    }

    @Override
    protected void onPostExecute(WsResult result) {
        try {
            if (result.getErr().equals(RESULT_IS_OK)) {
                Boolean resp = (Boolean) result.getObject();
                if (resp) {
                    Tools.setDefaults(Constants.PusheId, "OK");

                    System.out.println("Push Id Register ...");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


//

    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
