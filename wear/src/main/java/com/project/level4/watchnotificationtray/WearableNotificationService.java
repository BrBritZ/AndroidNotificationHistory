package com.project.level4.watchnotificationtray;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.LinkedList;


/**
 * Created by Rob on 11/01/2016.
 */
public class WearableNotificationService extends WearableListenerService {

    private static final String WEARABLE_DATA_PATH = "/wearable_data";
    private static final String ACTION = "NOTIFICATION";
    private static final String ACTIONCOUNTER = "COUNTER";
    private long counter = 0;
    private static LinkedList<DataMap> unsentMaps = new LinkedList<DataMap>();

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        System.out.println("datamap update..");
        DataMap dataMap;
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
                    System.out.println("IS ACTIVE? " + WearMainActivity.active);
                    if (!WearMainActivity.active){
                        unsentMaps.add(dataMap);
                        System.out.println("UNREAD SIZE: " + unsentMaps.size());
                    } if (WearMainActivity.active) {
                        for (int i = 0; i < unsentMaps.size(); i++) {
                            broadcastDataMap(unsentMaps.get(i));
                            System.out.println("Broadcasting map: " + i);
                        }
                        unsentMaps = new LinkedList<DataMap>();
                    }

                    broadcastDataMap(dataMap);
                    broadcastCounter();
                }
            }
        }
    }

    public void broadcastDataMap(DataMap dataMap){
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(ACTION);
        notificationIntent.putExtra("datamap", dataMap.toBundle());
        LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
    }

    public void broadcastCounter(){
        Intent counterIntent = new Intent();
        counterIntent.setAction(ACTIONCOUNTER);
        counterIntent.putExtra("counter", counter);
        LocalBroadcastManager.getInstance(this).sendBroadcast(counterIntent);
    }
}

