package com.project.level4.watchnotificationtray;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
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
        GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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


    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Received notification broadcast in MainActivity");

            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            System.out.println(pack + '\n' + title + '\n' + text);
            Bitmap icon = null;
            if (intent.getByteArrayExtra("iconByteArray") != null) {
                byte[] byteArray = intent.getByteArrayExtra("iconByteArray");
                icon  = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }

            String limitKey = getApplicationContext().getResources().getString(R.string.notification_limit);
            SharedPreferences sharedPreferences = getSharedPreferences("pref_key_notification_settings",MODE_PRIVATE);
            String limit = sharedPreferences.getString(limitKey, "10");


            // Create a DataMap object and send it to the data layer
            DataMap dataMap = new DataMap();
            dataMap.putString("package", pack);
            dataMap.putString("title", title);
            dataMap.putString("text", text);
            dataMap.putString("limit", limit);
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
