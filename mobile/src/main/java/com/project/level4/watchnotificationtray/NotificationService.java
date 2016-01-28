package com.project.level4.watchnotificationtray;

import android.app.Notification;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.io.ByteArrayOutputStream;

/**
 * Created by Rob on 28/01/2016.
 */
public class NotificationService extends NotificationListenerService {
    private String TAG = this.getClass().getSimpleName();
    private NotificationReceiver notificationReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.project.level4.watchnotificationtray.NOTIFICATION");
        registerReceiver(notificationReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }

   @Override
    public void onNotificationPosted(StatusBarNotification sbn){
       System.out.println("Received a notification");

       String pack = sbn.getPackageName();
       Notification notification = sbn.getNotification();
       //       String ticker = sbn.getNotification().tickerText.toString();
       Bundle extras = sbn.getNotification().extras;
       String title = extras.getString("android.title");
       String text = extras.getCharSequence("android.text").toString();
       Bitmap icon = notification.largeIcon;
       String iconID = String.valueOf(notification.extras.getInt(Notification.EXTRA_LARGE_ICON));

       Intent msgrcv = new Intent("com.project.level4.watchnotificationtray.NOTIFICATION");
       msgrcv.putExtra("package", pack);
       msgrcv.putExtra("title", title);
       msgrcv.putExtra("text", text);

       // icon to byte array to pass into intent
       if (icon != null){
           ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
           icon.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
           msgrcv.putExtra("iconByteArray", icon);
       }
       System.out.println("Broadcasting...");
       sendBroadcast(msgrcv);
   }
}
