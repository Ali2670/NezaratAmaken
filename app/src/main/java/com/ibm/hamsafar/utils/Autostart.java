package com.ibm.hamsafar.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ibm.hamsafar.services.PcService;

/**
 * Created by Hamed on 10/05/2018.
 */

public class Autostart extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context, PcService.class);
        context.startService(intent);
        Log.i("Autostart", "started");
    }
}