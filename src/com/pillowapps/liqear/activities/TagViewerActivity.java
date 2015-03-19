package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.ViewerAdapter;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.models.lastfm.LastfmTagModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class TagViewerActivity extends PagerResultActivity {
    public static final String TAG = "tag";
    public static final int TAG_INFO_INDEX = 1;
    public static final int TRACKS_INDEX = 0;
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private Tag tag;
    private LastfmTagModel tagModel = new LastfmTagModel();

    @SuppressWarnings("unchecked")
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

    private void initViewPager() {
        final LayoutInflater inflater = LayoutInflater.from(this);

        List<ViewerPage> pages = new ArrayList<>(5);
        pages.add(createTagTopTracksPage(inflater));
        setViewers(pages);
        final ViewerAdapter adapter = new ViewerAdapter(pages);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setTextColor(getResources().getColor(R.color.secondary_text));
        indicator.setSelectedColor(getResources().getColor(R.color.primary_text));
        indicator.setFooterColor(getResources().getColor(R.color.accent));
    }

    private ViewerPage createTagTopTracksPage(LayoutInflater inflater) {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
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
                Intent intent = new Intent(TagViewerActivity.this, MainActivity.class);
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