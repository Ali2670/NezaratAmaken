package com.basta.websec;

public class AdtBastaWSSec {
    static {
        System.loadLibrary("adtbasta");
    }

    public native String getVer();

    public native String envelopMessage(String myUser, String myPass, String myData, String myTime);

    public native String extractMessage(String myUser, String myPass, String envMsg);

}
