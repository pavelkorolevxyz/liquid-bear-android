package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
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
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.PagerResultSherlockActivity;
import com.pillowapps.liqear.components.SpinnerViewerPage;
import com.pillowapps.liqear.components.ViewerPage;
import com.pillowapps.liqear.connection.LastfmRequestManager;
import com.pillowapps.liqear.connection.Params;
import com.pillowapps.liqear.connection.ReadyResult;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.lastfm.LastfmArtist;
import com.pillowapps.liqear.models.lastfm.LastfmTrack;
import com.pillowapps.liqear.models.Track;
import com.pillowapps.liqear.models.User;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@SuppressWarnings("unchecked")
public class UserViewerLastfmActivity extends PagerResultSherlockActivity {
    public static final String USER = "user";
    public static final String YOU_MODE = "you";
    public static final int RECENT_INDEX = 3;
    public static final int TOP_ARTISTS_INDEX = 2;
    public static final int TOP_TRACKS_INDEX = 1;
    public static final int LOVED_INDEX = 0;
    public static final String TAB_INDEX = "tab_index";
    private static final int PAGES_LENGTH = RECENT_INDEX + 1;
    @SuppressWarnings("rawtypes")
    //    private CancellableThread searchThread;
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private User user;
    private Mode mode = Mode.USER;
    private int defaultIndex = TOP_TRACKS_INDEX;
    private boolean youMode = false;
    private ActionBar actionBar;
    private String topArtistsPeriod;
    private String topTracksPeriod;

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

        for (int i = 0; i < viewersCount(); i++) {
            ViewerPage viewer = getViewer(i);
            switch (i) {
                case TOP_ARTISTS_INDEX:
                    setOpenArtistListener(viewer);
                    break;
                default:
                    setOpenMainPlaylist(viewer);
                    setTrackLongClick(viewer);
                    break;
            }
        }


        getViewer(LOVED_INDEX).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getLoved(TRACKS_IN_TOP_COUNT, true);
            }
        });
        final SpinnerViewerPage topTracksViewer = (SpinnerViewerPage) getViewer(TOP_TRACKS_INDEX);
        topTracksViewer.getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTopTracks(true, false);
            }
        });
        final ViewerPage recentViewer = getViewer(RECENT_INDEX);
        recentViewer.getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getRecent(true);
            }
        });
        final SpinnerViewerPage artistsViewer = (SpinnerViewerPage) getViewer(TOP_ARTISTS_INDEX);
        artistsViewer.getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTopArtists(true);
            }
        });
        Spinner topTracksSpinner = topTracksViewer.getSpinner();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_lastfm_without_1month, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topTracksSpinner.setAdapter(adapter);

        int anInt = PreferencesManager.getSavePreferences().getInt(Constants.TIME_TOP_TRACKS, 0);
        topTracksPeriod = Constants.PERIODS_ARRAY[anInt];
        topTracksSpinner.setSelection(anInt);

        topTracksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int itemPosition, long l) {
                topTracksPeriod = Constants.PERIODS_WITHOUT_ONEMONTH_ARRAY[itemPosition];
                SharedPreferences savePreferences = PreferencesManager.getSavePreferences();
                SharedPreferences.Editor editor = savePreferences.edit();
                editor.putInt(Constants.TIME_TOP_TRACKS, itemPosition).commit();
                if (topTracksViewer.getAdapter() == null
                        || !(topTracksPeriod.equals(topTracksViewer.getAdapter().getPeriod()))) {
                    if (!topTracksViewer.adapterClean()) {
                        topTracksViewer.clear();
                    }
                    topTracksViewer.getProgressBar().setVisibility(View.VISIBLE);
                    getTopTracks(true, true);
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

        anInt = PreferencesManager.getSavePreferences().getInt(Constants.TIME_TOP_ARTISTS, 0);
        topArtistsPeriod = Constants.PERIODS_ARRAY[anInt];
        topArtistsSpinner.setSelection(anInt);

        topArtistsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int itemPosition, long l) {
                topArtistsPeriod = Constants.PERIODS_ARRAY[itemPosition];
                SharedPreferences.Editor editor = PreferencesManager.getSavePreferences().edit();
                editor.putInt(Constants.TIME_TOP_ARTISTS, itemPosition).commit();
                if (artistsViewer.getAdapter() == null
                        || !(topArtistsPeriod.equals(artistsViewer.getAdapter().getPeriod()))) {
                    if (!artistsViewer.adapterClean()) {
                        artistsViewer.clear();
                    }
                    artistsViewer.getProgressBar().setVisibility(View.VISIBLE);
                    getTopArtists(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        changeViewPagerItem(defaultIndex);
    }

    private boolean checkError(ReadyResult result, Params.ApiSource apiSource) {
        if (!result.isOk()) {
            Utils.showErrorDialog(result, UserViewerLastfmActivity.this, apiSource);
        }
        return !result.isOk();
    }

    private void initViewPager() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<View> views = new ArrayList<View>();

        View tab = inflater.inflate(R.layout.list_tab, null);
        views.add(tab);
        addViewer(new ViewerPage<Track>(tab));
        tab = inflater.inflate(R.layout.list_spinner_tab, null);
        views.add(tab);
        addViewer(new SpinnerViewerPage<Track>(tab));
        tab = inflater.inflate(R.layout.list_spinner_tab, null);
        views.add(tab);
        addViewer(new SpinnerViewerPage<Artist>(tab));
        tab = inflater.inflate(R.layout.list_tab, null);
        views.add(tab);
        addViewer(new ViewerPage<Track>(tab));

        final UserViewerAdapter adapter = new UserViewerAdapter(views);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);

        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setTextColor(getResources().getColor(R.color.secondary_text)); indicator.setSelectedColor(getResources().getColor(R.color.primary_text));
        indicator.setFooterColor(getResources().getColor(R.color.accent));
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int index) {
                invalidateOptionsMenu();
                switch (index) {
                    case LOVED_INDEX:
                        getLoved(TRACKS_IN_TOP_COUNT, false);
                        break;
                    case RECENT_INDEX:
                        getRecent(false);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void getTopArtists(boolean loadIfFull) {
        final ViewerPage viewer = getViewer(TOP_ARTISTS_INDEX);
        if (!viewer.adapterClean()) {
            if (!loadIfFull) {
                return;
            }
        } else {
            viewer.getProgressBar().setVisibility(View.VISIBLE);
        }
        LastfmRequestManager.getInstance().getUserTopArtists(user.getName(), topArtistsPeriod,
                TRACKS_IN_TOP_COUNT, viewer.getPage("getTopArtists"), new Callback<List<LastfmArtist>>() {
                    @Override
                    public void success(List<LastfmArtist> lastfmArtists, Response response) {
                        fillArtists(Converter.convertArtistList(lastfmArtists), viewer);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showError(error);
                    }
                });
    }

    private void getRecent(boolean loadIfFull) {
        final ViewerPage viewer = getViewer(RECENT_INDEX);
        if (!viewer.adapterClean()) {
            if (!loadIfFull) {
                return;
            }
        } else {
            viewer.getProgressBar().setVisibility(View.VISIBLE);
        }
        LastfmRequestManager.getInstance().getUserRecentTracks(user.getName(), TRACKS_IN_TOP_COUNT,
                viewer.getPage("getRecent"), new Callback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks, Response response) {
                        fillTracks(Converter.convertTrackList(lastfmTracks), viewer);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showError(error);
                    }
                });
    }

    private void getTopTracks(boolean loadIfFull, boolean loadAnyway) {
        final ViewerPage viewer = getViewer(TOP_TRACKS_INDEX);
        if (!viewer.adapterClean()) {
            if (!loadIfFull) {
                return;
            }
        } else if (loadAnyway) {
            viewer.getProgressBar().setVisibility(View.VISIBLE);
        }
        LastfmRequestManager.getInstance().getUserTopTracks(user.getName(), topTracksPeriod,
                TRACKS_IN_TOP_COUNT, viewer.getPage("getTracks"), new Callback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks, Response response) {
                        fillTracks(Converter.convertTrackList(lastfmTracks), viewer);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showError(error);
                    }
                });
    }

    private void showError(RetrofitError error) {
        ErrorNotifier.showLastfmError(UserViewerLastfmActivity.this, error);
    }

    private void getLoved(int limit, boolean loadIfFull) {
        final ViewerPage viewer = getViewer(LOVED_INDEX);
        if (!viewer.adapterClean()) {
            if (!loadIfFull) {
                return;
            }
        } else {
            viewer.getProgressBar().setVisibility(View.VISIBLE);
        }
        int page = viewer.getPage("getLoved");
        if (page > 0) {
            LastfmRequestManager.getInstance().getLovedTracks(user.getName(), limit, page,
                    new Callback<List<LastfmTrack>>() {
                        @Override
                        public void success(List<LastfmTrack> lastfmTracks, Response response) {
                            fillTracks(Converter.convertTrackList(lastfmTracks), viewer);
                        }

                        @Override
                        public void failure(RetrofitError error) {
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
                getViewer(LOVED_INDEX).clear();
                getLoved(0, true);
                getViewer(LOVED_INDEX).getProgressBar().setVisibility(View.VISIBLE);
            }
            return true;
            case R.id.to_playlist: {
                if (getViewer(pager.getCurrentItem()).adapterClean()) return true;
                addToMainPlaylist(getViewer(pager.getCurrentItem()).getValues());
                Toast.makeText(UserViewerLastfmActivity.this, R.string.added,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (getViewer(pager.getCurrentItem()).adapterClean()) return true;
                saveAsPlaylist(getViewer(pager.getCurrentItem()).getValues());
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

    private class UserViewerAdapter extends PagerAdapter {

        List<View> views = null;
        private String[] titles;

        public UserViewerAdapter(List<View> inViews) {
            views = inViews;
            titles = new String[]{
                    getString(R.string.loved).toLowerCase(),
                    getString(R.string.top_tracks).toLowerCase(),
                    getString(R.string.top_artists).toLowerCase(),
                    getString(R.string.recent).toLowerCase()};
        }

        public String getTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            View v = views.get(position);
            ((ViewPager) pager).addView(v, 0);
            return v;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager) pager).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void finishUpdate(View view) {
        }

        @Override
        public void restoreState(Parcelable p, ClassLoader c) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View view) {
        }
    }
}