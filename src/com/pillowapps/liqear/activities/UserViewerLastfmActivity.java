package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.ViewerAdapter;
import com.pillowapps.liqear.components.viewers.LastfmArtistViewerPage;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.viewers.SpinnerLastfmArtistViewerPage;
import com.pillowapps.liqear.components.viewers.SpinnerLastfmTracksViewerPage;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class UserViewerLastfmActivity extends PagerResultActivity {
    public static final String USER = "user";
    public static final String YOU_MODE = "you";
    public static final int RECENT_INDEX = 3;
    public static final int TOP_ARTISTS_INDEX = 2;
    public static final int TOP_TRACKS_INDEX = 1;
    public static final int LOVED_INDEX = 0;
    public static final String TAB_INDEX = "tab_index";
    private static final int PAGES_LENGTH = RECENT_INDEX + 1;
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private User user;
    private Mode mode = Mode.USER;
    private int defaultIndex = TOP_TRACKS_INDEX;
    private boolean youMode = false;
    private ActionBar actionBar;
    private String topArtistsPeriod;
    private String topTracksPeriod;
    private LastfmUserModel userModel = new LastfmUserModel();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable(USER);
        defaultIndex = extras.getInt(TAB_INDEX);
        youMode = extras.getBoolean(YOU_MODE, false);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (user != null) {
            actionBar.setTitle(user.getName());
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

        int savedTopTrackPeriod = PreferencesManager.getSavePreferences().getInt(Constants.TIME_TOP_TRACKS, 0);
        topTracksPeriod = Constants.PERIODS_ARRAY[savedTopTrackPeriod];
        topTracksSpinner.setSelection(savedTopTrackPeriod);

        topTracksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int itemPosition, long l) {
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(getResources().getColor(R.color.primary_text));
                }
                topTracksPeriod = Constants.PERIODS_WITHOUT_ONEMONTH_ARRAY[itemPosition];
                SharedPreferences savePreferences = PreferencesManager.getSavePreferences();
                SharedPreferences.Editor editor = savePreferences.edit();
                editor.putInt(Constants.TIME_TOP_TRACKS, itemPosition).apply();
                if (topTracksViewer.isNotLoaded() || !topTracksPeriod.equals(topTracksViewer.getPeriod())) {
                    if (!topTracksViewer.isNotLoaded()) {
                        topTracksViewer.clear();
                    }
                    LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(TOP_TRACKS_INDEX);
                    getTopTracks(topTracksPeriod, getPageSize(), viewer.getPage(), viewer);
                }
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

        int savedTopArtistPeriod = PreferencesManager.getSavePreferences().getInt(Constants.TIME_TOP_ARTISTS, 0);
        topArtistsPeriod = Constants.PERIODS_ARRAY[savedTopArtistPeriod];
        topArtistsSpinner.setSelection(savedTopArtistPeriod);

        topArtistsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int itemPosition, long l) {
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(getResources().getColor(R.color.primary_text));
                }
                topArtistsPeriod = Constants.PERIODS_ARRAY[itemPosition];
                SharedPreferences.Editor editor = PreferencesManager.getSavePreferences().edit();
                editor.putInt(Constants.TIME_TOP_ARTISTS, itemPosition).apply();
                if (artistsViewer.isNotLoaded() || !topArtistsPeriod.equals(artistsViewer.getPeriod())) {
                    if (!artistsViewer.isNotLoaded()) {
                        artistsViewer.clear();
                    }
                    LastfmArtistViewerPage viewer = (LastfmArtistViewerPage) getViewer(TOP_ARTISTS_INDEX);
                    getTopArtists(topArtistsPeriod, getPageSize(), viewer.getPage(), viewer);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        changeViewPagerItem(defaultIndex);
    }

    private void initViewPager() {
        List<ViewerPage> pages = new ArrayList<>(5);
        LayoutInflater inflater = LayoutInflater.from(this);
        pages.add(createLovedTracksPage(inflater));
        pages.add(createTopTracksPage(inflater));
        pages.add(createTopArtistsPage(inflater));
        pages.add(createRecentTracksPage(inflater));
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

    private ViewerPage createRecentTracksPage(LayoutInflater inflater) {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.recent
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getRecent(getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createTopArtistsPage(LayoutInflater inflater) {
        final SpinnerLastfmArtistViewerPage viewer = new SpinnerLastfmArtistViewerPage(this,
                inflater.inflate(R.layout.list_spinner_tab, null),
                R.string.top_artists);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTopArtists(topArtistsPeriod, getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(artistClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createTopTracksPage(LayoutInflater inflater) {
        final SpinnerLastfmTracksViewerPage viewer = new SpinnerLastfmTracksViewerPage(this,
                inflater.inflate(R.layout.list_spinner_tab, null),
                R.string.top_tracks
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTopTracks(topTracksPeriod, getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createLovedTracksPage(LayoutInflater inflater) {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.loved_tracks
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getLoved(getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private void getTopArtists(String period, int limit, int page, final LastfmArtistViewerPage viewer) {
        if (!viewer.isNotLoaded()) {
            return;
        } else {
            viewer.showProgressBar(true);
        }
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
        if (!viewer.isNotLoaded()) {
            return;
        } else {
            viewer.showProgressBar(true);
        }
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

    private void getTopTracks(String period, int limit, int page, final LastfmTracksViewerPage viewer) {
        if (!viewer.isNotLoaded()) {
            return;
        }
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
        if (!viewer.isNotLoaded()) {
            return;
        } else {
            viewer.showProgressBar(true);
        }
        if (page > 0) {
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
            case android.R.id.home: {
                finish();
                Intent intent = new Intent(UserViewerLastfmActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.all_button: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(LOVED_INDEX);
                viewer.clear();
                getLoved(getPageSize(), viewer.getPage(), viewer);
            }
            return true;
            case R.id.to_playlist: {
                ViewerPage viewer = getViewer(pager.getCurrentItem());
                if (viewer.isNotLoaded()) return true;
                addToMainPlaylist(Converter.convertLastfmTrackList(viewer.getItems()));
                Toast.makeText(UserViewerLastfmActivity.this, R.string.added,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                ViewerPage viewer = getViewer(pager.getCurrentItem());
                if (viewer.isNotLoaded()) return true;
                saveAsPlaylist(Converter.convertLastfmTrackList(viewer.getItems()));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        final int index = pager.getCurrentItem();
        switch (index) {
            case LOVED_INDEX: {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                inflater.inflate(R.menu.load_all_menu, menu);
            }
            break;
            case TOP_TRACKS_INDEX: {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                inflater.inflate(R.menu.to_playlist_menu, menu);
            }
            break;
            case TOP_ARTISTS_INDEX: {
                inflater.inflate(R.menu.empty_menu, menu);
            }
            break;
            case RECENT_INDEX:
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                inflater.inflate(R.menu.to_playlist_menu, menu);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private enum Mode {
        USER,
        GROUP
    }
}