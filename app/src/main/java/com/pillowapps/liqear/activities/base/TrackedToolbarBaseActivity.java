package com.pillowapps.liqear.activities.base;

import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.pillowapps.liqear.BuildConfig;

public abstract class TrackedToolbarBaseActivity extends ToolbarBaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!BuildConfig.DEBUG) {
            EasyTracker.getInstance(this).activityStop(this);
        }
    }
}
