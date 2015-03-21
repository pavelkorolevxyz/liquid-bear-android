package com.pillowapps.liqear.activities.viewers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.adapters.ViewerAdapter;
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.models.lastfm.LastfmTagModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class LastfmTagViewerActivity extends PagerResultActivity {
    public static final String TAG = "tag";
    public static final int TRACKS_INDEX = 0;
    public static final int PAGES_NUMBER = 5;
    private Tag tag;
    private LastfmTagModel tagModel = new LastfmTagModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        Bundle extras = getIntent().getExtras();
        tag = new Tag(extras.getString((TAG)));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(tag.getName());
        initUi();
        ViewerPage viewer = getViewer(TRACKS_INDEX);
        if (viewer.isNotLoaded()) {
            viewer.showProgressBar(true);
            viewer.onLoadMore();
        }
    }

    private void initUi() {
        initViewPager();
    }

    protected void initViewPager() {
        List<ViewerPage> pages = new ArrayList<>(PAGES_NUMBER);
        pages.add(createTagTopTracksPage());
        setViewers(pages);
        final ViewerAdapter adapter = new ViewerAdapter(pages);
        injectViewPager(adapter);
    }

    private ViewerPage createTagTopTracksPage() {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.tracks);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTagTopTracks(tag, getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(LastfmTagViewerActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void getTagTopTracks(Tag tag, int limit, int page, final LastfmTracksViewerPage viewer) {
        tagModel.getTagTopTracks(tag.getName(),
                limit, page, new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        viewer.fill(lastfmTracks);
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                    }
                });
    }
}