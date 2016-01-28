package com.project.level4.watchnotificationtray;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Rob on 08/01/2016.
 */
public class NotificationObject {
    String pack;
    String title;
    String text;
    Bitmap icon;
    long position;

    public NotificationObject(String pack, String title, String text, Bitmap icon, long position) {
        this.pack = pack;
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.position = position;
    }

    public String getPackageName(){
        return this.pack;
    }

    public String getTitle(){
        return this.title;
    }

    public String getText(){
        return this.text;
    }

    public Bitmap getIcon(){
        return this.icon;
    }

    public long getPosition(){
        return this.position;
    }

    public DataMap putToDataMap() {
        DataMap map = new DataMap();

        map.putString("package", pack);
        map.putString("title", title);
        map.putString("text", text);
        map.putLong("position", position);
        map.putAsset("icon", createAssetFromBitmap(icon));

        return map;
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
