package com.pillowapps.liqear.activities.modes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.components.HintMaterialEditText;
import com.pillowapps.liqear.components.ResultActivity;
import com.pillowapps.liqear.helpers.DividerItemDecoration;

public abstract class SearchBaseActivity extends ResultActivity {

    protected HintMaterialEditText editText;
    protected UltimateRecyclerView recyclerView;
    protected TextView emptyTextView;
    protected ProgressBar progressBar;
    protected ActionBar actionBar;

    private LinearLayoutManager layoutManager;
    public View searchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        editText = (HintMaterialEditText) findViewById(R.id.search_edit_text_quick_search_layout);
        searchLayout = findViewById(R.id.edit_part_quick_search_layout);
        recyclerView = (UltimateRecyclerView) findViewById(R.id.list);
        emptyTextView = (TextView) findViewById(R.id.empty);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    protected abstract void initWatcher();

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                finish();
                Intent intent = new Intent(SearchBaseActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            default:
                return false;
        }
    }
}
