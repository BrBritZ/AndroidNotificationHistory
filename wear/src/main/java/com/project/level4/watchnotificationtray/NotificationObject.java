package com.project.level4.watchnotificationtray;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by Rob on 08/01/2016.
 */
public class NotificationObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String pack;
    private String title;
    private String text;
    private Drawable icon;


    public NotificationObject() {
        this.pack = null;
        this.title = null;
        this.text = null;
        this.icon = null;


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

    public Drawable getIcon(){
        return this.icon;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

}

