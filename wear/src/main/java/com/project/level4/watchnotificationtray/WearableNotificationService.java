package com.project.level4.watchnotificationtray;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;


/**
 * Created by Rob on 11/01/2016.
 */
public class WearableNotificationService extends WearableListenerService {

    private static final String WEARABLE_DATA_PATH = "/wearable_data";
    private static final String ACTION = "NOTIFICATION";
    private static final String ACTIONCOUNTER = "COUNTER";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        System.out.println("datamap update..");
        DataMap dataMap;
        long counter = 0;
        for (DataEvent event : dataEvents) {
            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    counter++;

                    // Broadcast DataMap contents to wearable activity for display
                    // The content has the package name, title, text, and bitmap.

                    Intent notificationIntent = new Intent();
                    notificationIntent.setAction(ACTION);
                    notificationIntent.putExtra("datamap", dataMap.toBundle());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);

                    Intent counterIntent = new Intent();
                    counterIntent.setAction(ACTIONCOUNTER);
                    counterIntent.putExtra("counter", counter);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(counterIntent);
                }
            }
        }
    }
}

