package com.project.level4.watchnotificationtray;

import android.graphics.Bitmap;
import android.graphics.drawable.Icon;

import com.google.android.gms.wearable.DataMap;

/**
 * Created by Rob on 08/01/2016.
 */
public class NotificationObject {
    String pack;
    String title;
    String text;
    Bitmap icon;
    long position;

    public NotificationObject(){
        this.pack = null;
        this.title = null;
        this.text = null;
        this.icon = null;
        this.position = 0;
    }

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

}
