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
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import java.util.concurrent.TimeUnit;

public class WearMainActivity extends Activity {
    private TextView mHeader;
    private static final int TIMEOUT_MS = 50;

    private NotificationObject notificationObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Could stub layout when not connected to mobile here


        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle data = intent.getBundleExtra("datamap");
            // create notification object from DataMap


            NotificationObject notificationObject = new NotificationObject(
                   data.getString("pack"),
                    data.getString("title"),
                    data.getString("text"),
                    getBitmapFromAsset((Asset)data.get("icon")),
                    data.getLong("position")
                    );
            Log.i("HERE ", notificationObject.getTitle());
            Log.i("HERE ", notificationObject.getText());

            // Display message in UI
            // This is our list header
            mHeader = (TextView) findViewById(R.id.wearable_listview_header);

            WearableListView wearableListView =
                    (WearableListView) findViewById(R.id.wearable_listview_container);
            wearableListView.setAdapter(
                    new WearableAdapter(context,
                    notificationObject.getTitle(),
                    notificationObject.getText()));
            wearableListView.setClickListener(mClickListener);
            wearableListView.addOnScrollListener(mOnScrollListener);
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