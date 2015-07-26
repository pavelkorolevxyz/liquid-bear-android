package com.pillowapps.liqear.activities.modes;

import android.os.Bundle;
import android.view.View;

public abstract class ListBaseActivity extends SearchBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchLayout.setVisibility(View.GONE);
    }

    @Override
    protected void initWatcher() {
        // No operations.
    }
}
