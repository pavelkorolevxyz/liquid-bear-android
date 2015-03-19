package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.ViewerAdapter;
import com.pillowapps.liqear.components.viewers.LastfmArtistViewerPage;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.models.lastfm.LastfmChartModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class ChartsActivity extends PagerResultActivity {
    public static final int MOST_LOVED = 4;
    public static final int TOP_ARTISTS = 3;
    public static final int TOP_TRACKS = 2;
    public static final int HYPED_TRACKS = 1;
    public static final int HYPED_ARTISTS = 0;
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private LastfmChartModel chartsModel = new LastfmChartModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.charts);
        initUi();
    }

    private void initUi() {
        initViewPager();
        int defaultIndex = TOP_TRACKS;
        changeViewPagerItem(defaultIndex);
    }

    private void initViewPager() {
        List<ViewerPage> pages = new ArrayList<>(5);
        LayoutInflater inflater = LayoutInflater.from(this);
        pages.add(createHypedArtistsPage(inflater));
        pages.add(createHypedTracksPage(inflater));
        pages.add(createTopTracksPage(inflater));
        pages.add(createTopArtistsPage(inflater));
        pages.add(createLovedTracksPage(inflater));
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
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int index) {
                invalidateOptionsMenu();
                ViewerPage viewer = getViewer(index);
                if (viewer.isNotLoaded()) {
                    viewer.showProgressBar(true);
                    viewer.onLoadMore();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private ViewerPage createLovedTracksPage(LayoutInflater inflater) {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.loved_tracks);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getMostLoved(viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createTopArtistsPage(LayoutInflater inflater) {
        final LastfmArtistViewerPage viewer = new LastfmArtistViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.top_artists);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTopArtists(viewer);
            }
        });
        viewer.setItemClickListener(artistClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createTopTracksPage(LayoutInflater inflater) {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.top_tracks);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTopTracks(viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private LastfmTracksViewerPage createHypedTracksPage(LayoutInflater inflater) {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.hyped_artists);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getHypedTracks(viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createHypedArtistsPage(LayoutInflater inflater) {
        final LastfmArtistViewerPage viewer = new LastfmArtistViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.hyped_artists);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getHypedArtists(viewer);
            }
        });
        viewer.setItemClickListener(artistClickListener);
        addViewer(viewer);
        return viewer;
    }

    private void changeViewPagerItem(int currentItem) {
        if (pager.getCurrentItem() == currentItem) {
            indicator.onPageSelected(currentItem);
        } else {
            pager.setCurrentItem(currentItem);
            indicator.setCurrentItem(currentItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        final int index = pager.getCurrentItem();
        switch (index) {
            case HYPED_TRACKS:
            case TOP_TRACKS:
            case MOST_LOVED: {
                inflater.inflate(R.menu.to_playlist_menu, menu);
            }
            break;
            default: {
                inflater.inflate(R.menu.empty_menu, menu);
            }
            break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                finish();
                Intent intent = new Intent(ChartsActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.to_playlist: {
                if (getViewer(pager.getCurrentItem()).isNotLoaded()) return true;
                List<LastfmTrack> items = getViewer(pager.getCurrentItem()).getItems();
                addToMainPlaylist(Converter.convertLastfmTrackList(items));
                Toast.makeText(ChartsActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (getViewer(pager.getCurrentItem()).isNotLoaded()) return true;
                List<LastfmTrack> items = getViewer(pager.getCurrentItem()).getItems();
                saveAsPlaylist(Converter.convertLastfmTrackList(items));
            }
            return true;
        }
        return false;
    }

    private void getMostLoved(final LastfmTracksViewerPage viewer) {
        chartsModel.getLovedTracksChart(TRACKS_IN_TOP_COUNT,
                viewer.getPage(), new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        viewer.fill(lastfmTracks);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(ChartsActivity.this, errorMessage);
                    }
                });
    }

    private void getTopArtists(final LastfmArtistViewerPage viewer) {
        chartsModel.getTopArtists(TRACKS_IN_TOP_COUNT,
                viewer.getPage(), new SimpleCallback<List<LastfmArtist>>() {
                    @Override
                    public void success(List<LastfmArtist> lastfmArtists) {
                        viewer.fill(lastfmArtists);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(ChartsActivity.this, errorMessage);
                    }
                });
    }

    private void getTopTracks(final LastfmTracksViewerPage viewer) {
        chartsModel.getTopTracksChart(TRACKS_IN_TOP_COUNT,
                viewer.getPage(), new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        viewer.fill(lastfmTracks);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(ChartsActivity.this, errorMessage);
                    }
                });
    }

    private void getHypedTracks(final LastfmTracksViewerPage viewer) {
        chartsModel.getHypedTracks(TRACKS_IN_TOP_COUNT,
                viewer.getPage(), new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        viewer.fill(lastfmTracks);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(ChartsActivity.this, errorMessage);
                    }
                });
    }

    private void getHypedArtists(final LastfmArtistViewerPage viewer) {
        chartsModel.getHypedArtists(TRACKS_IN_TOP_COUNT,
                viewer.getPage(), new SimpleCallback<List<LastfmArtist>>() {
                    @Override
                    public void success(List<LastfmArtist> lastfmArtists) {
                        viewer.fill(lastfmArtists);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(ChartsActivity.this, errorMessage);
                    }
                });
    }
}