package com.pillowapps.liqear.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.pillowapps.liqear.R;

public class TrackedActivity extends SherlockActivity {

    protected void onCreate(Bundle savedInstanceState, String tag) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        if (getResources().getBoolean(R.bool.analytics_enabled)) {
            EasyTracker.getInstance(this).activityStart(this);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}
