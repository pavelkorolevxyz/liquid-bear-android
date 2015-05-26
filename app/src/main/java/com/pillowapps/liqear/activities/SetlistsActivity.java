package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.ResultActivity;
import com.pillowapps.liqear.helpers.Constants;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SetlistsActivity extends ResultActivity {
    @InjectView(R.id.artist_name_edit_text_setlist)
    protected EditText artistEditText;
    @InjectView(R.id.city_edit_text_setlist)
    protected EditText venueEditText;
    @InjectView(R.id.venue_edit_text_setlist)
    protected EditText cityEditText;
    @InjectView(R.id.search_setlist_button)
    protected Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setlist_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.setlist));
    }

    @OnClick(R.id.search_setlist_button)
    protected void onSearchClicked() {
        Intent intent = new Intent(SetlistsActivity.this,
                SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_MODE,
                SearchActivity.SearchMode.SETLIST);
        intent.putExtra("artist", artistEditText.getText().toString());
        intent.putExtra("venue", venueEditText.getText().toString());
        intent.putExtra("city", cityEditText.getText().toString());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }
}
