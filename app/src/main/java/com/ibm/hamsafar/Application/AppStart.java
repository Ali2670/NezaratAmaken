package com.ibm.hamsafar.Application;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;

import com.ibm.hamsafar.utils.Tools;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


/**
 * Created by Hamed on 28/08/2015.
 */
public class AppStart extends Application {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String str = "";

        Tools.URL_MEDIA[0] = "http://172.16.160.206:8001/";
        Tools.URL_MEDIA[1] = "http://172.16.160.206:8003/";

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

//        Tools.URL_APK[0] = "http://192.168.1.6:9009/Upload/APK/";
        Tools.URL_APK[0] = "http://172.16.160.206:8001/" + str + "Upload/APK/";
        Tools.URL_APK[1] = "http://172.16.160.206:8003/" + str + "Upload/APK/";
//        Tools.URL_APK[2] = "http://89.32.249.190:8005/Upload/APK/";

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)


                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPoolSize(5)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(9 * 1024 * 1024))
                .discCacheSize(80 * 1024 * 1024)

                .build();

        ImageLoader.getInstance().init(config);


//        MultiDex.install(this);
        mContext = this;

        Tools.tf = Typeface.createFromAsset(getContext().getAssets(), "morvarid.ttf");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        System.exit(1); // kill off the crashed app
    }
}
