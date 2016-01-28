package com.project.level4.watchnotificationtray;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Rob on 28/01/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        System.out.println("Received notification broadcast in MainActivity");
//
//
////        if (intent.getStringExtra("command").equals("clearall")) {
////           cancelAllNotifications();
////        }
//
//        /*ERROR HERE SOMEWHERE, never reaches "starting thread" */
//        System.out.println("Trying to unpack intent");
//        String pack = intent.getStringExtra("package");
//        System.out.println("Got package name: " + pack);
//        String title = intent.getStringExtra("title");
//        System.out.println("Got title name: " + title);
//        String text = intent.getStringExtra("text");
//        System.out.println("Got text: " + text);
//        Bitmap icon = null;
//        if (intent.getParcelableExtra("iconByteArray") != null) {
//            icon = BitmapFactory.decodeByteArray(
//                    intent.getByteArrayExtra("iconByteArray"),
//                    0,
//                    intent.getByteArrayExtra("byteArray").length);
//            System.out.println("Got bitmap for icon");
//        }
//
//
//        // Create a DataMap object and send it to the data layer
//        DataMap dataMap = new DataMap();
//        dataMap.putString("package", pack);
//        dataMap.putString("title", title);
//        dataMap.putString("text", text);
//        if (icon != null) {
//            Asset asset = createAssetFromBitmap(icon);
//            dataMap.putAsset("icon", asset);
//        }
//
//        System.out.println("created DataMap");
//
//        String WEARABLE_DATA_PATH = "/wearable_data";
//
//        //Requires a new thread to avoid blocking the UI
//        System.out.println("starting thread you jobby muncher");
////        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap, googleClient);
//    }
//
//    private Asset createAssetFromBitmap(Bitmap bitmap) {
//        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
//        return Asset.createFromBytes(byteStream.toByteArray());
//    }
    }
}