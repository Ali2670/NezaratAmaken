package com.ibm.hamsafar.asyncTask;

public interface TaskCallBack<T> {
    void onResultReceived(T result);
//    void onFailure(Exception exception);
}
