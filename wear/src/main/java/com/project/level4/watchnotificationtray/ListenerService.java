package com.project.level4.watchnotificationtray;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rob on 11/01/2016.
 */
public class ListenerService extends WearableListenerService {

    private static final int TIMEOUT_MS = 50;
    private static final String WEARABLE_DATA_PATH = "/wearable_data";
    private DataMap dataMap;
    private NotificationObject notificationObject;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;
        for (DataEvent event : dataEvents) {
            Log.v("ListenerService", "DataMap received on watch: " + DataMapItem.fromDataItem(event.getDataItem()).getDataMap());
            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {}
                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();


                // Broadcast DataMap contents to wearable activity for display
                // The content has the golf hole number and distances to the front,
                // middle and back pin placements.

                Intent messageIntent = new Intent();
                messageIntent.setAction(Intent.ACTION_SEND);
                messageIntent.putExtra("datamap", dataMap.toBundle());
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

            }
        }
    }

//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//
//        if (messageEvent.getPath().equals("/message_path")) {
//            final String message = new String(messageEvent.getData());
//
//            // Broadcast message to wearable activity for display
//            Intent messageIntent = new Intent();
//            messageIntent.setAction(Intent.ACTION_SEND);
//            messageIntent.putExtra("message", message);
//            messageIntent.putExtra("package",notificationObject.getPackageName());
//            messageIntent.putExtra("title",notificationObject.getTitle());
//            messageIntent.putExtra("text",notificationObject.getText());
//            messageIntent.putExtra("icon",notificationObject.getIcon());
//            messageIntent.putExtra("position",notificationObject.getPosition());
//            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
//        } else {
//            super.onMessageReceived(messageEvent);
//        }
//    }
}