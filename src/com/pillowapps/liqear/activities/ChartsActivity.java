package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.PagerResultSherlockActivity;
import com.pillowapps.liqear.components.ViewerPage;
import com.pillowapps.liqear.connection.GetResponseCallback;
import com.pillowapps.liqear.connection.Params;
import com.pillowapps.liqear.connection.QueryManager;
import com.pillowapps.liqear.connection.ReadyResult;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.Track;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ChartsActivity extends PagerResultSherlockActivity {
    public static final int MOST_LOVED = 4;
    public static final int TOP_ARTISTS = 3;
    public static final int TOP_TRACKS = 2;
    public static final int HYPED_TRACKS = 1;
    public static final int HYPED_ARTISTS = 0;
    @SuppressWarnings("rawtypes")
    private ViewPager pager;
    private TitlePageIndicator indicator;

    @SuppressWarnings("unchecked")
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

        for (int i = 0; i < viewersCount(); i++) {
            ViewerPage viewer = getViewer(i);
            switch (i) {
                case HYPED_ARTISTS:
                case TOP_ARTISTS:
                    setOpenArtistListener(viewer);
                    break;
                default:
                    setOpenMainPlaylist(viewer);
                    setTrackLongClick(viewer);
                    break;
            }
        }

        getViewer(HYPED_ARTISTS).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getHypedArtists();
            }
        });
        getViewer(HYPED_TRACKS).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getHypedTracks();
            }
        });
        getViewer(TOP_TRACKS).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTopTracks();
            }
        });
        getViewer(TOP_ARTISTS).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTopArtists();
            }
        });
        getViewer(MOST_LOVED).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getMostLoved();
            }
        });
        int defaultIndex = TOP_TRACKS;
        changeViewPagerItem(defaultIndex);
    }

    private void getMostLoved() {
        GetResponseCallback callback = new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (!checkError(result, Params.ApiSource.LASTFM)) {
                    fillTracks(result, getViewer(MOST_LOVED));
                }
            }
        };
        QueryManager.getInstance().getLovedTracksChart(TRACKS_IN_TOP_COUNT,
                getViewer(MOST_LOVED).getPage("getMostLoved"), callback);
    }

    private void getTopArtists() {
        GetResponseCallback callback = new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (!checkError(result, Params.ApiSource.LASTFM)) {
                    fillArtists(result, getViewer(TOP_ARTISTS));
                }
            }
        };
        QueryManager.getInstance().getTopArtists(TRACKS_IN_TOP_COUNT,
                getViewer(TOP_ARTISTS).getPage("getTopArtists"), callback);
    }

    private void getTopTracks() {
        GetResponseCallback callback = new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (!checkError(result, Params.ApiSource.LASTFM)) {
                    fillTracks(result, getViewer(TOP_TRACKS));
                }
            }
        };
        QueryManager.getInstance().getTopTracksChart(TRACKS_IN_TOP_COUNT,
                getViewer(TOP_TRACKS).getPage("getTopTracks"), callback);
    }

    private void getHypedTracks() {
        GetResponseCallback callback = new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (!checkError(result, Params.ApiSource.LASTFM)) {
                    fillTracks(result, getViewer(HYPED_TRACKS));
                }
            }
        };
        QueryManager.getInstance().getHypedTracks(TRACKS_IN_TOP_COUNT,
                getViewer(HYPED_TRACKS).getPage("getHypedTracks"), callback);
    }

    private void getHypedArtists() {
        GetResponseCallback callback = new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (!checkError(result, Params.ApiSource.LASTFM)) {
                    fillArtists(result, getViewer(HYPED_ARTISTS));
                }
            }
        };
        QueryManager.getInstance().getHypedArtists(TRACKS_IN_TOP_COUNT,
                getViewer(HYPED_ARTISTS).getPage("getHypedArtists"), callback);
    }

    private boolean checkError(ReadyResult result, Params.ApiSource apiSource) {
        if (!result.isOk()) {
            Utils.showErrorDialog(result, ChartsActivity.this, apiSource);
        }
        return !result.isOk();
    }

    private void initViewPager() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<View> views = new ArrayList<View>();
        View view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Artist>(view));
        view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Track>(view));
        view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Track>(view));
        view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Artist>(view));
        view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Track>(view));
        final ChartsAdapter adapter = new ChartsAdapter(views);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setFooterColor(getResources().getColor(R.color.darkest_blue));
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int index) {
                invalidateOptionsMenu();
                if (getViewer(index).adapterClean()) {
                    getViewer(index).getProgressBar().setVisibility(View.VISIBLE);
                    switch (index) {
                        case HYPED_ARTISTS:
                            getHypedArtists();
                            break;
                        case HYPED_TRACKS:
                            getHypedTracks();
                            break;
                        case TOP_TRACKS:
                            getTopTracks();
                            break;
                        case TOP_ARTISTS:
                            getTopArtists();
                            break;
                        case MOST_LOVED:
                            getMostLoved();
                            break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
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

    /**
     * Context menu items' positions.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
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
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
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
                if (getViewer(pager.getCurrentItem()).adapterClean()) return true;
                addToMainPlaylist(getViewer(pager.getCurrentItem()).getValues());
                Toast.makeText(ChartsActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
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

    private class ChartsAdapter extends PagerAdapter {
        List<View> views = null;
        private String[] titles;

        public ChartsAdapter(List<View> inViews) {
            views = inViews;
            titles = new String[]{
                    getString(R.string.hyped_artists).toLowerCase(),
                    getString(R.string.hyped_tracks).toLowerCase(),
                    getString(R.string.top_tracks).toLowerCase(),
                    getString(R.string.top_artists).toLowerCase(),
                    getString(R.string.loved_tracks).toLowerCase()};
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