package com.project.level4.watchnotificationtray;

import android.app.Notification;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

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
       Bitmap icon = null;
       try {
           Drawable d = getPackageManager().getApplicationIcon(pack);
           icon = ((BitmapDrawable)d).getBitmap();
       }
       catch (PackageManager.NameNotFoundException e)
       {
           Log.w("OnNotificationPosted", "Cannot get icon from package");
       }

       Intent msgrcv = new Intent("com.project.level4.watchnotificationtray.NOTIFICATION");
       msgrcv.putExtra("package", pack);
       msgrcv.putExtra("title", title);
       msgrcv.putExtra("text", text);

       // icon to byte array to pass into intent
       if (icon != null){
           ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
           icon.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
           byte[] byteArray = byteStream.toByteArray();
           msgrcv.putExtra("iconByteArray", byteArray);
       }
       System.out.println("Broadcasting...");
       sendBroadcast(msgrcv);
   }
}
