package com.pillowapps.liqear.activities.base;

import android.os.Bundle;
import android.view.View;

import com.pillowapps.liqear.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class SearchListBaseActivity extends ListBaseActivity {

    @Bind(R.id.edit_part_quick_search_layout)
    protected View searchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        searchLayout.setVisibility(View.VISIBLE);
    }

    protected abstract void initWatcher();
}
