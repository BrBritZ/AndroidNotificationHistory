package com.project.level4.watchnotificationtray;

/**
 * Created by Rob on 20/12/2015.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class WearMainActivity extends Activity {
    private TextView mHeader;
    private static final int TIMEOUT_MS = 1000;
    private static final String ACTION = "NOTIFICATION";
    private static final String ACTIONCOUNTER = "COUNTER";

    private MessageReceiver messageReceiver;
    private LinkedList<NotificationObject> notificationLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationLL = new LinkedList<NotificationObject>();

        readNotificationsFromInternalStorage();

        if (!notificationLL.isEmpty()){
            updateUI();
        } else {
            NotificationObject defaultObject = new NotificationObject();
            defaultObject.setTitle(getResources().getString(R.string.notification_default_value));
            notificationLL.add(defaultObject);
            mHeader = (TextView) findViewById(R.id.wearable_listview_header);
            WearableListView wearableListView =
                    (WearableListView) findViewById(R.id.wearable_listview_container);
            System.out.println("setting up default view");
            wearableListView.setAdapter(new WearableAdapter(this, notificationLL));

            wearableListView.setClickListener(mClickListener);
            wearableListView.addOnScrollListener(mOnScrollListener);
        }

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(ACTION);
        messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        writeNotificationsToInternalStorage();

        // unregister receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void writeNotificationsToInternalStorage() {
        FileOutputStream fileOut = null;
        String fileName = getResources().getString(R.string.filename);
        try {

            fileOut = getApplicationContext().openFileOutput(
                    fileName, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(notificationLL);
            out.close();
            fileOut.close();

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void readNotificationsFromInternalStorage() {
        FileInputStream fileIn = null;
        String fileName = getResources().getString(R.string.filename);
        LinkedList<NotificationObject> temp = new LinkedList<NotificationObject>();
        try {
            fileIn = getApplicationContext().openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fileIn);
            temp = (LinkedList<NotificationObject>)is.readObject();

            if (temp != null){
                notificationLL = temp;
            }
            is.close();
        }
        catch (FileNotFoundException e) {
            Log.e("ReadingFile","File not found");
        }
        catch (StreamCorruptedException e) {
            e.printStackTrace();

        }
        catch (IOException e) {
            e.printStackTrace();

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();

        }

    }


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION.equals(intent.getAction())) {
                System.out.println("Received a message in WEAR");

                // remove default message for empty notifcation history
                removeEmptyNotifcationLLValues();

                Bundle data = intent.getBundleExtra("datamap");
                DataMap dataMap = DataMap.fromBundle(data);
                NotificationObject notificationObject = null;

                System.out.println("Got DataMap");
                System.out.println("package: " + dataMap.getString("package"));
                System.out.println("title: " + dataMap.getString("title"));
                System.out.println("text: " + dataMap.getString("text"));

                if (dataMap != null) {
                    // create notification object from DataMap
                    notificationObject = new NotificationObject();

                    if (dataMap.getString("package") != null) {
                        notificationObject.setPack(dataMap.getString("package"));
                    }
                    if (dataMap.getString("title") != null) {
                        notificationObject.setTitle(dataMap.getString("title"));
                    }
                    if (dataMap.getString("text") != null) {
                        notificationObject.setText(dataMap.getString("text"));
                    }

                    if (dataMap.getAsset("icon") != null) {
                        System.out.println("icon not null, starting async task");
                        getBitmapAsyncTask(context, dataMap, notificationObject);
                    } else {
                        System.out.println("Added notification to linked list WITHOUT ICON");
                        notificationLL.add(notificationObject);
                        // This is our list header
                        updateUI();
                    }

                }
                System.out.println("Created NotificationObject");
            }
        }
    }

    private void updateUI(){
        System.out.println("Updating User Interface!");
        mHeader = (TextView) findViewById(R.id.wearable_listview_header);
        WearableListView wearableListView =
                (WearableListView) findViewById(R.id.wearable_listview_container);
        System.out.println("got notification from linked list");
        wearableListView.setAdapter(new WearableAdapter(getApplicationContext(), notificationLL));
        wearableListView.setClickListener(mClickListener);
        wearableListView.addOnScrollListener(mOnScrollListener);
        System.out.println("Set adapter for view");

    }

    public void removeEmptyNotifcationLLValues(){
        if (!notificationLL.isEmpty() && notificationLL != null) {
            NotificationObject notification = notificationLL.get(0);
            if (notification.getTitle().equals(getResources().getString(R.string.notification_default_value))) {
                notificationLL.remove(0);
            }
        }
    }

    public void getBitmapAsyncTask(final Context context, final DataMap map, final NotificationObject notification) {
        new AsyncTask<NotificationObject, Void, NotificationObject>() {
            @Override
            protected NotificationObject doInBackground(NotificationObject... notification) {
                System.out.println("1");
                GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                        .addApi(Wearable.API)
                        .build();
                ConnectionResult result =
                        googleApiClient.blockingConnect(
                                TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (!result.isSuccess()) {
                    notification[0].setIcon(null); // could handle incorrectly
                    return notification[0];
                }
                System.out.println("2");
                // convert asset into a file descriptor and block until it's ready
                InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                        googleApiClient, map.getAsset("icon")).await().getInputStream();
                googleApiClient.disconnect();

                if (assetInputStream == null) {
                    Log.w("AsyncTask", "Requested an unknown Asset.");
                    return null;
                }
                System.out.println("3");
                // decode the stream into a bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(assetInputStream);
                Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 48, 48, true);
                notification[0].setIcon(bMapScaled);
                return notification[0];
            }

            @Override
            protected void onPostExecute(NotificationObject notification) {
                super.onPostExecute(notification);
                notificationLL.add(notification);
                System.out.println("updating UI from ASYNCTASK");
                updateUI();
            }
        }.execute(notification);
    }

    // Handle our Wearable List's click events
    private WearableListView.ClickListener mClickListener =
            new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    Toast.makeText(WearMainActivity.this,
                            String.format("You selected item #%s",
                                    viewHolder.getLayoutPosition() + 1),
                            Toast.LENGTH_SHORT).show();
                    Intent counterIntent = new Intent();
                    counterIntent.setAction(ACTIONCOUNTER);
                    counterIntent.putExtra("counter", 0);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(counterIntent);
                }

                @Override
                public void onTopEmptyRegionClick() {
                    Toast.makeText(WearMainActivity.this,
                            "Top empty area tapped", Toast.LENGTH_SHORT).show();
                }
            };

    // The following code ensures that the title scrolls as the user scrolls up
    // or down the list
    private WearableListView.OnScrollListener mOnScrollListener =
            new WearableListView.OnScrollListener() {
                @Override
                public void onAbsoluteScrollChange(int i) {
                    // Only scroll the title up from its original base position
                    // and not down.
                    if (i > 0) {
                        mHeader.setY(-i);
                    }
                }

                @Override
                public void onScroll(int i) {
                    // Placeholder
                }

                @Override
                public void onScrollStateChanged(int i) {
                    // Placeholder
                }

                @Override
                public void onCentralPositionChanged(int i) {
                    // Placeholder
                }
            };

}