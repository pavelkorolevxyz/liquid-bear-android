package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.PagerResultSherlockActivity;
import com.pillowapps.liqear.components.ViewerPage;
import com.pillowapps.liqear.connection.Params;
import com.pillowapps.liqear.connection.QueryManager;
import com.pillowapps.liqear.connection.ReadyResult;
import com.pillowapps.liqear.connection.VkRequestManager;
import com.pillowapps.liqear.connection.VkSimpleCallback;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Group;
import com.pillowapps.liqear.models.Track;
import com.pillowapps.liqear.models.User;
import com.pillowapps.liqear.models.vk.VkAlbum;
import com.pillowapps.liqear.models.vk.VkError;
import com.pillowapps.liqear.models.vk.VkTrack;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@SuppressWarnings("unchecked")
public class UserViewerVkActivity extends PagerResultSherlockActivity {
    public static final String USER = "user";
    public static final String GROUP = "group";
    public static final String YOU_MODE = "you";
    public static final int FAVORITES = 3;
    public static final int NEWS_FEED = 4;
    public static final int ALBUM_INDEX = 2;
    public static final int USER_AUDIO_INDEX = 1;
    public static final int WALL_INDEX = 0;
    public static final String TAB_INDEX = "tab_index";
    @SuppressWarnings("rawtypes")
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private User user;
    private int wallPage = 0;
    private Group group;
    private Mode mode = Mode.USER;
    private int defaultIndex = USER_AUDIO_INDEX;
    private boolean youMode = false;
    private ActionBar actionBar;
    private int favoritesPage = 0;
    private int newsFeedPage = 0;
    private int albumPage = 0;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable(USER);
        defaultIndex = extras.getInt(TAB_INDEX);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        youMode = extras.getBoolean(YOU_MODE, false);
        if (user == null) {
            mode = Mode.GROUP;
            group = (Group) extras.getSerializable(GROUP);
            actionBar.setTitle(group.getName());
        } else {
            actionBar.setTitle(user.getName());
        }
        initUi();
    }

    private void initUi() {
        initViewPager();

        if (youMode) {
            getViewer(FAVORITES).getListView().setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,
                                        int position, long l) {
                    openMainPlaylist(getViewer(FAVORITES).getValues(), position);
                }
            });
            setTrackLongClick(getViewer(FAVORITES));
            getViewer(FAVORITES).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    getFavoritesTracks();
                }
            });
            getViewer(NEWS_FEED).getListView().setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,
                                        int position, long l) {
                    openMainPlaylist(getViewer(NEWS_FEED).getValues(), position);
                }
            });
            getViewer(NEWS_FEED).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    getNewsFeedTracks();
                }
            });
            setTrackLongClick(getViewer(NEWS_FEED));
        }

        getViewer(USER_AUDIO_INDEX).getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openMainPlaylist(getViewer(USER_AUDIO_INDEX).getValues(), position);
            }
        });
        setTrackLongClick(getViewer(USER_AUDIO_INDEX));
        getViewer(WALL_INDEX).getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openMainPlaylist(getViewer(WALL_INDEX).getValues(), position);
            }
        });
        setTrackLongClick(getViewer(WALL_INDEX));
        getViewer(ALBUM_INDEX).getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent searchIntent = new Intent(UserViewerVkActivity.this,
                        SearchSherlockListActivity.class);
                Album vkAlbum = (Album) getViewer(ALBUM_INDEX).getValues().get(position);
                searchIntent.putExtra("title", vkAlbum.getTitle());
                if (mode == Mode.USER) {
                    searchIntent.putExtra("uid", user.getUid());
                } else {
                    searchIntent.putExtra("gid", group.getGid());
                }
                searchIntent.putExtra("album_id", vkAlbum.getAlbumId());
                searchIntent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.VK_ALBUM_TRACKLIST);
                startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
            }
        });
        getViewer(WALL_INDEX).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getWallTracks();
            }
        });
        getViewer(ALBUM_INDEX).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getAlbums();
            }
        });
        changeViewPagerItem(defaultIndex);
    }

    private boolean checkError(ReadyResult result, Params.ApiSource apiSource) {
        if (!result.isOk()) {
            Utils.showErrorDialog(result, UserViewerVkActivity.this, apiSource);
        }
        return !result.isOk();
    }

    private void initViewPager() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<View> views = new ArrayList<View>();
        View view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Track>(view));
        view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Track>(view));
        view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Album>(view));
        if (youMode) {
            view = inflater.inflate(R.layout.list_tab, null);
            views.add(view);
            addViewer(new ViewerPage<Track>(view));
            view = inflater.inflate(R.layout.list_tab, null);
            views.add(view);
            addViewer(new ViewerPage<Track>(view));
        }
        final UserViewerAdapter adapter = new UserViewerAdapter(views);
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
            public void onPageSelected(final int index) {
                invalidateOptionsMenu();
                if (getViewer(index).adapterClean()) {
                    getViewer(index).getProgressBar().setVisibility(View.VISIBLE);
                    switch (index) {
                        case WALL_INDEX:
                            getWallTracks();
                            break;
                        case USER_AUDIO_INDEX:
                            getUserAudio();
                            break;
                        case ALBUM_INDEX:
                            getAlbums();
                            break;
                        case FAVORITES:
                            getFavoritesTracks();
                            break;
                        case NEWS_FEED:
                            getNewsFeedTracks();
                            break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void getFavoritesTracks() {
        if (mode == Mode.USER) {
            VkRequestManager.getInstance().getVkUserFavoritesAudio(TRACKS_IN_TOP_COUNT,
                    TRACKS_IN_TOP_COUNT * favoritesPage++, new VkSimpleCallback<List<VkTrack>>() {
                        @Override
                        public void success(List<VkTrack> data) {
                            fillTracks(Converter.convertVkTrackList(data), getViewer(FAVORITES));

                        }

                        @Override
                        public void failure(VkError error) {
                            ErrorNotifier.showVkError(UserViewerVkActivity.this, error);

                        }
                    });
        }
    }

    private void getNewsFeedTracks() {
        if (mode == Mode.USER) {
            VkRequestManager.getInstance().getVkNewsFeedTracks(100, 100 * newsFeedPage++,
                    new VkSimpleCallback<List<VkTrack>>() {
                        @Override
                        public void success(List<VkTrack> data) {
                            fillTracks(Converter.convertVkTrackList(data), getViewer(NEWS_FEED));
                            if (getViewer(NEWS_FEED).getValues().size() < 20) {
                                getNewsFeedTracks();
                            }
                        }

                        @Override
                        public void failure(VkError error) {
                            ErrorNotifier.showVkError(UserViewerVkActivity.this, error);

                        }
                    });
        }
    }

    private void getAlbums() {
        VkSimpleCallback<List<VkAlbum>> callback = new VkSimpleCallback<List<VkAlbum>>() {
            @Override
            public void success(List<VkAlbum> data) {
                fillVkAlbums(data, getViewer(ALBUM_INDEX));

            }

            @Override
            public void failure(VkError error) {

            }
        };
        if (mode == Mode.USER) {
            VkRequestManager.getInstance().getUserVkAlbums(user.getUid(), TRACKS_IN_TOP_COUNT,
                    TRACKS_IN_TOP_COUNT * albumPage++, callback);
        } else {
            VkRequestManager.getInstance().getGroupVkAlbums(group.getGid(), TRACKS_IN_TOP_COUNT,
                    TRACKS_IN_TOP_COUNT * albumPage++, callback);
        }
    }

    private void getUserAudio() {
        VkSimpleCallback<List<VkTrack>> callback = new VkSimpleCallback<List<VkTrack>>() {
            @Override
            public void success(List<VkTrack> data) {
                fillTracks(Converter.convertVkTrackList(data), getViewer(USER_AUDIO_INDEX));
            }

            @Override
            public void failure(VkError error) {
                ErrorNotifier.showVkError(UserViewerVkActivity.this, error);
            }
        };
        if (mode == Mode.USER) {
            VkRequestManager.getInstance().getVkUserAudio(user.getUid(), 0, 0, callback);
        } else {
            VkRequestManager.getInstance().getVkGroupAudio(group.getGid(), 0, 0, callback);
        }
    }

    private void getWallTracks() {
        VkSimpleCallback<List<VkTrack>> callback = new VkSimpleCallback<List<VkTrack>>() {
            @Override
            public void success(List<VkTrack> data) {
                fillTracks(Converter.convertVkTrackList(data), getViewer(WALL_INDEX));
            }

            @Override
            public void failure(VkError error) {
                ErrorNotifier.showVkError(UserViewerVkActivity.this, error);
            }
        };
        if (mode == Mode.USER) {
            VkRequestManager.getInstance().getVkUserWallAudio(user.getUid(), TRACKS_IN_TOP_COUNT,
                    TRACKS_IN_TOP_COUNT * wallPage++, callback);
        } else {
            VkRequestManager.getInstance().getVkUserWallAudio(group.getGid(), TRACKS_IN_TOP_COUNT,
                    TRACKS_IN_TOP_COUNT * wallPage++, callback);
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

    /**
     * Context menu items' positions.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        final int index = pager.getCurrentItem();
        switch (index) {
            case WALL_INDEX:
            case USER_AUDIO_INDEX:
            case FAVORITES:
            case NEWS_FEED: {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        int currentItem = pager.getCurrentItem();
        switch (itemId) {
            case android.R.id.home: {
                finish();
                Intent intent = new Intent(UserViewerVkActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.to_playlist: {
                if (getViewer(currentItem).adapterClean()) return true;
                addToMainPlaylist(getViewer(currentItem).getValues());
                Toast.makeText(UserViewerVkActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (getViewer(currentItem).adapterClean()) return true;
                saveAsPlaylist(getViewer(currentItem).getValues());
            }
            return true;
        }
        return false;
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
            if (inViews.size() == 5) {
                titles = new String[]{
                        getString(R.string.vk_wall),
                        getString(R.string.audio),
                        getString(R.string.vk_albums),
                        getString(R.string.vk_favorites),
                        getString(R.string.vk_feed)};
            } else {
                titles = new String[]{
                        getString(R.string.vk_wall),
                        getString(R.string.audio),
                        getString(R.string.vk_albums)};
            }
        }

        public String getTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object instantiateItem(ViewGroup pager, int position) {
            View v = views.get(position);
            pager.addView(v, 0);
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