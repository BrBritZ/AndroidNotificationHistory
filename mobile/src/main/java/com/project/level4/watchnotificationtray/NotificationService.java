package com.project.level4.watchnotificationtray;

/**
 * Created by Rob on 08/01/2016.
 */


import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;


public class NotificationService extends NotificationListenerService {

    Context context;
    long position;

    @Override

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        position = 0;

    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        Notification notification = sbn.getNotification();

        String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();
        Bitmap icon = notification.largeIcon;
        String iconID = String.valueOf(notification.extras.getInt(Notification.EXTRA_LARGE_ICON));

        Log.i("Package", pack);
        Log.i("Ticker",ticker);
        Log.i("Title",title);
        Log.i("Text",text);
        Log.i("IconID", iconID);

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        msgrcv.putExtra("icon", icon);
        msgrcv.putExtra("position", position);

        position++;

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }
}