package com.ibm.hamsafar.asyncTask;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ibm.hamsafar.utils.Tools;

import java.net.ConnectException;

import hamsafar.ws.util.exception.ExceptionConstants;
import hamsafar.ws.util.exception.GenericBusinessException;
import ibm.ws.RestCaller;
import ibm.ws.WsResult;

import static hamsafar.ws.util.exception.ExceptionConstants.CONNECTION_TIMEOUT_EXCEPTION;
import static hamsafar.ws.util.exception.ExceptionConstants.NULL_VALUE_RETURN;
import static hamsafar.ws.util.exception.ExceptionConstants.RESULT_IS_OK;
import static hamsafar.ws.util.exception.ExceptionConstants.UNKNOWN_EXCEPTION;


public class ListHttp extends AsyncTask<Object, Void, WsResult> {


    Context context;
    TaskCallBack taskCallBack = null;
    String loadMore;
    String methodName;
    Boolean refresh;
    /*Boolean isResultList = false;*/

    Toast toast;

    public ListHttp(TaskCallBack taskCallBack , Context context, String loadMore, String methodName, Boolean refresh) {
        this.taskCallBack = taskCallBack;
        this.context = context;
        this.loadMore = loadMore;
        this.methodName = methodName;
        this.refresh = refresh;
    }

    /*public ListHttp(TaskCallBack taskCallBack , Context context, String loadMore, String methodName, Boolean refresh , Boolean isResultList) {
        this.isResultList = isResultList;
        this.taskCallBack = taskCallBack;
        this.context = context;
        this.loadMore = loadMore;
        this.methodName = methodName;
        this.refresh = refresh;
    }*/


    public ListHttp() {
    }

    @Override
    protected WsResult doInBackground(Object... params) {

        WsResult result;

        try {
            Object obj ;
            /*if(isResultList)*/
                obj = RestCaller.SingleObjectReturn(methodName, (Object[]) params);/*
            else
                obj = RestCaller.SingleObjectReturn(methodName, (Object[]) params);*/

            if (obj == null/* || obj.getAllPosts() == null || obj.getAllPosts().size() == 0*/) {
                result = new WsResult(null, NULL_VALUE_RETURN);
                loadMore = "endof";
            } else {
                loadMore = "false";
                result = new WsResult(obj, RESULT_IS_OK);
//                    if (obj.getAllPosts().size() < 10) {
//                        loadMore = "endof";
//                    }


            }
        } catch (GenericBusinessException e) {
            e.printStackTrace();
            result = new WsResult(null, e.getErrCode());

        } catch (ConnectException e) {
            e.printStackTrace();
            result = new WsResult(null, CONNECTION_TIMEOUT_EXCEPTION);
        }
        return result;

    }

    @Override
    protected void onPostExecute(WsResult result) {

        try {

            if (result.getErr().equals(RESULT_IS_OK)) {
                if (refresh) {
                    refresh = false;
                }
                Object obj = result.getObject();
                taskCallBack.onResultReceived(obj);
//                        list.addAll(obj.getAll());

            } else {
                if (loadMore == null || !loadMore.equals("endof")) {
                    Err(result.getErr());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Err(UNKNOWN_EXCEPTION);

        }

//            searchTrddBtn.setEnabled(true);



    }

    private void Err(int i) {
        String errStr = Tools.getStringByName("Exc." + i);
        if (toast != null) {
            toast.cancel();
        }
        Tools.ShowDialog(context, Tools.getStringByName("Error"), errStr);

        if (ExceptionConstants.REST_INVALID_SESSION_ID == i) {
//                startActivity(new Intent(CategoryList.this, LoginActivity.class));
//                finish();
        }
    }


    @Override
    protected void onPreExecute() {
//            recordNotFoundTv.setVisibility(View.INVISIBLE);
          /*  if (loadMore == null) {
                Toast.makeText(getActivity(), "err . drop down to refresh", Toast.LENGTH_SHORT).show();

            }
*/
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}