package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.SetlistsResultActivity;
import com.pillowapps.liqear.components.HintMaterialEditText;
import com.pillowapps.liqear.activities.base.ResultTrackedBaseActivity;
import com.pillowapps.liqear.helpers.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class SetlistsActivity extends ResultTrackedBaseActivity {
    @Bind(R.id.artist_name_edit_text_setlist)
    protected HintMaterialEditText artistEditText;
    @Bind(R.id.city_edit_text_setlist)
    protected HintMaterialEditText venueEditText;
    @Bind(R.id.venue_edit_text_setlist)
    protected HintMaterialEditText cityEditText;
    @Bind(R.id.search_setlist_button)
    protected FancyButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setlist_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.setlist));
        }
    }

    @OnClick(R.id.search_setlist_button)
    protected void onSearchClicked() {
        Intent intent = new Intent(SetlistsActivity.this, SetlistsResultActivity.class);
        intent.putExtra("artist", artistEditText.getText().toString());
        intent.putExtra("venue", venueEditText.getText().toString());
        intent.putExtra("city", cityEditText.getText().toString());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
