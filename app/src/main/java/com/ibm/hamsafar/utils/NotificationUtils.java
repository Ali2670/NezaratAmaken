package com.ibm.hamsafar.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.ibm.hamsafar.R;

import java.util.List;
import java.util.Random;

/**
 * Created by Hamed on 21/09/2015.
 */
public class NotificationUtils {

    private String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils() {
    }

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public void showNotificationMessage(String title, String message, Intent intent, String color) {

        // Check for empty push message
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(message))
            return;

//        if (isAppIsInBackground(mContext)) {
        // notification icon
        int icon = R.drawable.logo;

        int mNotificationId = 100;
        PendingIntent resultPendingIntent = null;
//        if (isAppIsInBackground(mContext)) {
        resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
//        }else{
//
//        }

//            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                    mContext);
//            Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
//                    .setAutoCancel(true)
//                    .setContentTitle(title)
//                    .setStyle(inboxStyle)
//                    .setContentIntent(resultPendingIntent)
//                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
//                    .setContentText(message)
//                    .build();
//
//            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(mNotificationId, notification);
        Notification notification = new Notification(R.drawable.ic_launcher, "اعلان", System.currentTimeMillis());

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.custom_noti);

        contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.notification_title, title);
        contentView.setTextViewText(R.id.notification_text, message);
        if (color != null) {
         /*   contentView.setInt(R.id.customNotifRL, "setBackgroundColor",
                    Color.parseColor(color));*/
//            contentView.setInt(R.id.myRectangleViewNotif, "setCardBackgroundColor",Color.parseColor(color));
        }

        notification.contentView = contentView;
        if (resultPendingIntent != null) {
            notification.contentIntent = resultPendingIntent;
        }
//            notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        mNotificationManager.notify(m, notification);
//        } else {
//            Log.e(TAG, "Push received: CANCLE ");
//            intent.putExtra("title", "test");
//            intent.putExtra("message", "massage");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            mContext.startActivity(intent);
//        }
    }
}
