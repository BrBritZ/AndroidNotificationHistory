package com.project.level4.watchnotificationtray;

import android.app.Activity;

/**
 * Created by Rob on 09/02/2016.
 */
public class WearBaseActivity extends Activity {
    static boolean active = false;

    @Override
    public void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }
}