package com.ibm.hamsafar.fdate;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author unascribed
 * @version 1.0
 */

public class ShamsiDateException extends Exception {
    private int errorCode;

    public ShamsiDateException(int errCode, String errText) {
        super(errText);
        errorCode = errCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}