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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class WearMainActivity extends Activity {
    private TextView mHeader;
    private static final int TIMEOUT_MS = 100;

    private LinkedList<NotificationObject> notificationLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationLL = new LinkedList<NotificationObject>();

        if (savedInstanceState != null && (savedInstanceState.getSerializable("NotificationLinkedList") != null)) {
            notificationLL = (LinkedList<NotificationObject>) savedInstanceState.getSerializable("NotificationLinkedList");
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
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
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
        Bundle saveBundle = new Bundle();
        saveBundle.putSerializable("NotificationLinkedList", notificationLL);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("NotificationLinkedList", notificationLL);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Received a message in WEAR");

            // remove default message for empty notifcation history
            removeEmptyNotifcationLLValues();

            Bundle data = intent.getBundleExtra("datamap");
            DataMap dataMap = DataMap.fromBundle(data);
            NotificationObject notificationObject = null;

            System.out.println("Got DataMap");

            System.out.println("package: "+ dataMap.getString("package"));
            System.out.println("title: "+dataMap.getString("title"));
            System.out.println("text: "+dataMap.getString("text"));
            if (dataMap != null){
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
                    Drawable drawableIcon = new BitmapDrawable(getResources(), getBitmapFromAsset((Asset) dataMap.get("icon")));
                    notificationObject.setIcon(drawableIcon);
                }
                Log.i("WearMainActivity", "NotificationObject created");
            }

            System.out.println("Created NotificationObject");

            if (notificationObject != null){
                notificationLL.add(notificationObject);
            }

            System.out.println("Added notification to linked list");

            // This is our list header
            updateUI();
        }
    }

    private void updateUI(){
        System.out.println("Updating User Interface!");
        mHeader = (TextView) findViewById(R.id.wearable_listview_header);
        WearableListView wearableListView =
                (WearableListView) findViewById(R.id.wearable_listview_container);
        System.out.println("got first notification from linked list");
        wearableListView.setAdapter(new WearableAdapter(this, notificationLL));

        wearableListView.setClickListener(mClickListener);
        wearableListView.addOnScrollListener(mOnScrollListener);
        System.out.println("Set adapter for view");
    }

    public void removeEmptyNotifcationLLValues(){
        if (!notificationLL.isEmpty() || notificationLL != null) {
            NotificationObject notification = notificationLL.get(0);
            if (notification.getTitle().equals(getResources().getString(R.string.notification_default_value))) {
                notificationLL.remove(0);
            }
        }
    }

    private Bitmap getBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Wearable.API).build();
        ConnectionResult result = mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()){
            return null;
        }

        // convert asset into a file descriptor and block until it is ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w("NotificationObject", "Requested an unknown assset");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
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