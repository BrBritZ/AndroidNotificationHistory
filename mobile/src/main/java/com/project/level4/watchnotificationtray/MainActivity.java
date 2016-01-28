package com.project.level4.watchnotificationtray;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;
import java.io.ByteArrayOutputStream;


public class MainActivity extends PreferenceActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleClient;
    private NotificationReceiver notificationreceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        notificationreceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.project.level4.watchnotificationtray.NOTIFICATION");
        registerReceiver(notificationreceiver, filter);
    }

    // Send a data object when the data layer connection is successful.
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("MainActivity", "GoogleApiClient connected");
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationreceiver);
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


//    /*
//    * Notification Service
//    * */
//    public class NotificationService extends NotificationListenerService {
//        private String TAG = this.getClass().getSimpleName();
//        private com.project.level4.watchnotificationtray.NotificationReceiver notificationReceiver;
//
//        @Override
//        public void onCreate() {
//            super.onCreate();
//
//            notificationReceiver = new com.project.level4.watchnotificationtray.NotificationReceiver();
//            IntentFilter filter = new IntentFilter();
//            filter.addAction("com.project.level4.watchnotificationtray.NOTIFICATION");
//            registerReceiver(notificationReceiver, filter);
//        }
//
//        @Override
//        public void onDestroy() {
//            unregisterReceiver(notificationReceiver);
//            super.onDestroy();
//        }
//
//
//        @Override
//        public void onNotificationPosted(StatusBarNotification sbn) {
//            System.out.println("Received a notification");
//            String pack = sbn.getPackageName();
//            System.out.println("Package is: " + pack);
//            Notification notification = sbn.getNotification();
//
////        String ticker = sbn.getNotification().tickerText.toString();
//            Bundle extras = sbn.getNotification().extras;
//            String title = extras.getString("android.title");
//            System.out.println("Title is: " + title);
//            String text = extras.getCharSequence("android.text").toString();
//            System.out.println("Text is: " + text);
//            Bitmap icon = notification.largeIcon;
//            String iconID = String.valueOf(notification.extras.getInt(Notification.EXTRA_LARGE_ICON));
//
//            Log.i("Package", pack);
//            Log.i("Title", title);
//            Log.i("Text", text);
//            Log.i("IconID", iconID);
//
//            Intent msgrcv = new Intent("com.project.level4.watchnotificationtray.NOTIFICATION");
//            msgrcv.putExtra("package", pack);
//            msgrcv.putExtra("title", title);
//            msgrcv.putExtra("text", text);
//
//            // icon to byte array to pass into intent
//            if (icon != null) {
//                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//                icon.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
//                msgrcv.putExtra("iconByteArray", byteStream.toByteArray());
//            }
//
//            System.out.println("Broadcasting...");
//            sendBroadcast(msgrcv);
////        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgrcv);
//        }
//
//        @Override
//        public void onNotificationRemoved(StatusBarNotification sbn){
//            Log.i(TAG, "********** onNotificationRemoved");
//            Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
//            Intent i = new Intent("com.project.level4.watchnotificationtray.NOTIFICATION");
//            i.putExtra("notification<em>event", "onNotificationRemoved :" + sbn.getPackageName() + "n");
//            sendBroadcast(i);
//        }
//
//    }

    /*
    * NotificationReceiver
    * */
    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Received notification broadcast in MainActivity");


//        if (intent.getStringExtra("command").equals("clearall")) {
//           cancelAllNotifications();
//        }


            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            Bitmap icon = null;
            if (intent.getParcelableExtra("iconByteArray") != null) {
                icon = BitmapFactory.decodeByteArray(
                        intent.getByteArrayExtra("iconByteArray"),
                        0,
                        intent.getByteArrayExtra("byteArray").length);
            }


            // Create a DataMap object and send it to the data layer
            DataMap dataMap = new DataMap();
            dataMap.putString("package", pack);
            dataMap.putString("title", title);
            dataMap.putString("text", text);
            if (icon != null) {
                Asset asset = createAssetFromBitmap(icon);
                dataMap.putAsset("icon", asset);
            }

            System.out.println("created DataMap");

            String WEARABLE_DATA_PATH = "/wearable_data";

            //Requires a new thread to avoid blocking the UI
           new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap, googleClient).start();
        }

        private Asset createAssetFromBitmap(Bitmap bitmap) {
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        }
    }
}
