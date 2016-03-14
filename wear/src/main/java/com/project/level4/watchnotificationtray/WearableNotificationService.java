package com.project.level4.watchnotificationtray;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
    private static final String ACTIONPULL = "PULLREQUEST";
    private int counter = 0;
    private static LinkedList<DataMap> unsentMaps = new LinkedList<DataMap>();

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(ACTIONPULL);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mPullReceiver, messageFilter);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i("WearableNotifService","datamap update..");
        DataMap dataMap;
        for (DataEvent event : dataEvents) {
            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    // Broadcast DataMap contents to wearable activity for display
                    // The content has the package name, title, text, and bitmap.
                    unsentMaps.add(dataMap);
                    if (WearMainActivity.active && !WearNotificationActivity.active) {
                        for (int i = 0; i < unsentMaps.size(); i++) {
                            broadcastDataMap(unsentMaps.get(i));
                            Log.i("WearableNotifService","Broadcasting map: " + i);
                        }
                        resetMapList();
                    }
                    if (counter < 99){
                        counter++;
                    } else {
                        counter--;
                        unsentMaps.removeFirst();
                    }
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

    public void resetMapList(){
        unsentMaps = new LinkedList<DataMap>();
    }

    // when WearMainActivity starts, broadcast unsent DataMaps
    final BroadcastReceiver mPullReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTIONPULL.equals(intent.getAction())) {
                for (int i=0; i<unsentMaps.size(); i++){
                    broadcastDataMap(unsentMaps.get(i));
                }
                resetMapList();
            }
        }
    };
}

