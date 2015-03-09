package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.ResultSherlockActivity;
import com.pillowapps.liqear.helpers.Constants;

public class SetlistsActivity extends ResultSherlockActivity {
    private EditText artistEditText;
    private EditText venueEditText;
    private EditText cityEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setlist_search);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.setlist));
        initUi();
        initListeners();
    }

    private void initUi() {
        artistEditText = (EditText) findViewById(R.id.artist_name_edit_text_setlist);
        cityEditText = (EditText) findViewById(R.id.city_edit_text_setlist);
        venueEditText = (EditText) findViewById(R.id.venue_edit_text_setlist);
        searchButton = (Button) findViewById(R.id.search_setlist_button);
    }

    private void initListeners() {
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetlistsActivity.this,
                        SearchActivity.class);
                intent.putExtra(SearchActivity.SEARCH_MODE,
                        SearchActivity.SearchMode.SETLIST);
                intent.putExtra("artist", artistEditText.getText().toString());
                intent.putExtra("venue", venueEditText.getText().toString());
                intent.putExtra("city", cityEditText.getText().toString());
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
        });
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
