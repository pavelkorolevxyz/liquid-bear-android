package com.pillowapps.liqear.activities.modes.viewers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.PagerResultActivity;
import com.pillowapps.liqear.adapters.pagers.PagesPagerAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.components.viewers.LastfmArtistViewerPage;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.viewers.SpinnerLastfmArtistViewerPage;
import com.pillowapps.liqear.components.viewers.SpinnerLastfmTracksViewerPage;
import com.pillowapps.liqear.components.viewers.base.ViewerPage;
import com.pillowapps.liqear.entities.Page;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.LBPreferencesManager;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class LastfmUserViewerActivity extends PagerResultActivity {

    public static final String[] PERIODS_ARRAY = new String[]{
            "overall",
            "7day",
            "1month",
            "3month",
            "6month",
            "12month"
    };
    public static final String[] PERIODS_WITHOUT_ONEMONTH_ARRAY = new String[]{
            "overall",
            "7day",
            "3month",
            "6month",
            "12month"
    };

    public static final String USER = "user";
    public static final int RECENT_INDEX = 3;
    public static final int TOP_ARTISTS_INDEX = 2;
    public static final int TOP_TRACKS_INDEX = 1;
    public static final int LOVED_INDEX = 0;
    public static final String TAB_INDEX = "tab_index";
    public static final int PAGES_NUMBER = 5;
    private User user;
    private int defaultIndex = TOP_TRACKS_INDEX;
    private String topArtistsPeriod;
    private String topTracksPeriod;
    private int savedTopTrackPosition;
    private int savedTopArtistPeriod;

    @Inject
    LastfmUserModel userModel;
    @Inject
    LBPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setContentView(R.layout.viewer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable(USER);
        defaultIndex = extras.getInt(TAB_INDEX);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (user != null) {
                actionBar.setTitle(user.getName());
            }
        }
        initUi();
    }

    private void initUi() {
        initViewPager();

        final SpinnerLastfmTracksViewerPage topTracksViewer = (SpinnerLastfmTracksViewerPage) getViewer(TOP_TRACKS_INDEX);
        final SpinnerLastfmArtistViewerPage artistsViewer = (SpinnerLastfmArtistViewerPage) getViewer(TOP_ARTISTS_INDEX);
        Spinner topTracksSpinner = topTracksViewer.getSpinner();
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_lastfm_without_1month, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topTracksSpinner.setAdapter(adapter);

        savedTopTrackPosition = SharedPreferencesManager.getSavePreferences(this).getInt(Constants.TIME_TOP_TRACKS, 0);
        topTracksPeriod = PERIODS_ARRAY[savedTopTrackPosition];
        topTracksSpinner.setSelection(savedTopTrackPosition);

        topTracksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int itemPosition, long l) {
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(ContextCompat.getColor(LastfmUserViewerActivity.this, R.color.primary_text));
                }
                topTracksPeriod = PERIODS_WITHOUT_ONEMONTH_ARRAY[itemPosition];
                SharedPreferences savePreferences = SharedPreferencesManager.getSavePreferences(LastfmUserViewerActivity.this);
                SharedPreferences.Editor editor = savePreferences.edit();
                editor.putInt(Constants.TIME_TOP_TRACKS, itemPosition).apply();
                if (topTracksViewer.isNotLoaded() || itemPosition != savedTopTrackPosition) {
                    if (!topTracksViewer.isNotLoaded()) {
                        topTracksViewer.clear();
                    }
                    LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(TOP_TRACKS_INDEX);
                    getTopTracks(topTracksPeriod, getPageSize(), viewer.getPage(), true, viewer);
                }
                savedTopTrackPosition = itemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner topArtistsSpinner = artistsViewer.getSpinner();
        ArrayAdapter<CharSequence> mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_lastfm, android.R.layout.simple_dropdown_item_1line);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topArtistsSpinner.setAdapter(mSpinnerAdapter);

        savedTopArtistPeriod = SharedPreferencesManager.getSavePreferences(this).getInt(Constants.TIME_TOP_ARTISTS, 0);
        topArtistsPeriod = PERIODS_ARRAY[savedTopArtistPeriod];
        topArtistsSpinner.setSelection(savedTopArtistPeriod);

        topArtistsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int itemPosition, long l) {
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(ContextCompat.getColor(LastfmUserViewerActivity.this, R.color.primary_text));
                }
                topArtistsPeriod = PERIODS_ARRAY[itemPosition];
                SharedPreferences.Editor editor = SharedPreferencesManager.getSavePreferences(LastfmUserViewerActivity.this).edit();
                editor.putInt(Constants.TIME_TOP_ARTISTS, itemPosition).apply();
                if (artistsViewer.isNotLoaded() || itemPosition != savedTopArtistPeriod) {
                    if (!artistsViewer.isNotLoaded()) {
                        artistsViewer.clear();
                    }
                    LastfmArtistViewerPage viewer = (LastfmArtistViewerPage) getViewer(TOP_ARTISTS_INDEX);
                    getTopArtists(topArtistsPeriod, getPageSize(), viewer.getPage(), true, viewer);
                }
                savedTopArtistPeriod = itemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        changeViewPagerItem(defaultIndex);
    }

    private void initViewPager() {
        List<Page> pages = new ArrayList<>(PAGES_NUMBER);
        pages.add(createLovedTracksPage());
        pages.add(createTopTracksPage());
        pages.add(createTopArtistsPage());
        pages.add(createRecentTracksPage());
        setPages(pages);
        final PagesPagerAdapter adapter = new PagesPagerAdapter(pages);
        injectViewPager(adapter);
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

    private ViewerPage createRecentTracksPage() {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.recent
        );
        viewer.setOnLoadMoreListener(() -> getRecent(getPageSize(), viewer.getPage(), viewer)
        );
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createTopArtistsPage() {
        final SpinnerLastfmArtistViewerPage viewer = new SpinnerLastfmArtistViewerPage(this,
                View.inflate(this, R.layout.list_spinner_tab, null),
                R.string.top_artists,
                preferencesManager.isDownloadImagesEnabled());
        viewer.setOnLoadMoreListener(() -> getTopArtists(topArtistsPeriod, getPageSize(), viewer.getPage(), false, viewer)
        );
        viewer.setItemClickListener(artistClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createTopTracksPage() {
        final SpinnerLastfmTracksViewerPage viewer = new SpinnerLastfmTracksViewerPage(this,
                View.inflate(this, R.layout.list_spinner_tab, null),
                R.string.top_tracks
        );
        viewer.setOnLoadMoreListener(() -> {
                    Timber.d("onLoadMore LastmfUser top tracks");
                    getTopTracks(topTracksPeriod, getPageSize(), viewer.getPage(), false, viewer);
                }
        );
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createLovedTracksPage() {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.loved_tracks
        );
        viewer.setOnLoadMoreListener(() -> getLoved(getPageSize(), viewer.getPage(), viewer));
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private void getTopArtists(String period, int limit, int page, boolean force, final LastfmArtistViewerPage viewer) {
//        if (!viewer.isNotLoaded() && !force) return;
        viewer.showProgressBar(true);
        userModel.getUserTopArtists(user.getName(), period, limit, page,
                new SimpleCallback<List<LastfmArtist>>() {
                    @Override
                    public void success(List<LastfmArtist> lastfmArtists) {
                        viewer.fill(lastfmArtists);
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                    }
                });
    }

    private void getRecent(int limit, int page, final LastfmTracksViewerPage viewer) {
//        if (!viewer.isNotLoaded()) return;

        viewer.showProgressBar(true);
        userModel.getUserRecentTracks(user.getName(), limit, page,
                new SimpleCallback<List<LastfmTrack>>() {
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

    private void getTopTracks(final String period, int limit, int page, boolean force, final LastfmTracksViewerPage viewer) {
//        if (!viewer.isNotLoaded() && !force) {
//            return;
//        }
        viewer.showProgressBar(true);
        userModel.getUserTopTracks(user.getName(), period,
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

    private void getLoved(int limit, int page, final LastfmTracksViewerPage viewer) {
//        if (!viewer.isNotLoaded()) return;

        viewer.showProgressBar(true);
        userModel.getLovedTracks(user.getName(), limit, page,
                new SimpleCallback<List<LastfmTrack>>() {
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

    private void changeViewPagerItem(int currentItem) {
        if (pager.getCurrentItem() == currentItem) {
            indicator.onPageSelected(currentItem);
        } else {
            pager.setCurrentItem(currentItem);
            indicator.setCurrentItem(currentItem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.all_button: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(LOVED_INDEX);
                viewer.clear();
                getLoved(getPageSize(), viewer.getPage(), viewer);
            }
            return true;
            case R.id.to_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(pager.getCurrentItem());
                if (viewer.isNotLoaded()) return true;
                addToMainPlaylist(viewer.getItems());
                Toast.makeText(LastfmUserViewerActivity.this, R.string.added,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(pager.getCurrentItem());
                if (viewer.isNotLoaded()) return true;
                saveAsPlaylist(viewer.getItems());
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        final int index = pager.getCurrentItem();
        switch (index) {
            case LOVED_INDEX: {
                inflater.inflate(R.menu.load_all_menu, menu);
            }
            break;
            case TOP_TRACKS_INDEX: {
                inflater.inflate(R.menu.to_playlist_menu, menu);
            }
            break;
            case TOP_ARTISTS_INDEX: {
                inflater.inflate(R.menu.empty_menu, menu);
            }
            break;
            case RECENT_INDEX:
                inflater.inflate(R.menu.to_playlist_menu, menu);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }
}