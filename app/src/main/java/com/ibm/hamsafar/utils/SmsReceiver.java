package com.ibm.hamsafar.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    public Pattern p = Pattern.compile("(|^)\\d{4}");

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {


            Bundle data = intent.getExtras();
            Object[] pdus = (Object[]) data.get("pdus");
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String sender = smsMessage.getDisplayOriginatingAddress();
                String phoneNumber = smsMessage.getDisplayOriginatingAddress();
                String senderNum = phoneNumber;
                String messageBody = smsMessage.getMessageBody();
                try {
                    if ((senderNum != null && (senderNum.equals("100077") || senderNum.endsWith("100077")) && messageBody != null)) {
                        Matcher m = p.matcher(messageBody);
                        if (m.find()) {
                            mListener.messageReceived(m.group(0));
                        } else {
                        }
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {

        }
    }
}