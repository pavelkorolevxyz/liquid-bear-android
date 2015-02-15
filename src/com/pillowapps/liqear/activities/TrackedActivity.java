package com.pillowapps.liqear.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.pillowapps.liqear.R;

public class TrackedActivity extends ActionBarActivity {

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
