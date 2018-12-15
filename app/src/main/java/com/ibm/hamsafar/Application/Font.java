package com.ibm.hamsafar.Application;

import android.app.Application;

import com.ibm.hamsafar.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Font extends Application {
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
