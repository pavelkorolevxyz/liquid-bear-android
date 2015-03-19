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
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.components.viewers.VkAlbumViewerPage;
import com.pillowapps.liqear.components.viewers.VkTracksViewerPage;
import com.pillowapps.liqear.entities.Group;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkWallModel;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class UserViewerVkActivity extends PagerResultActivity {
    public static final String USER = "user";
    public static final String GROUP = "group";
    public static final String YOU_MODE = "you";
    public static final int FAVORITES = 3;
    public static final int NEWS_FEED = 4;
    public static final int ALBUM_INDEX = 2;
    public static final int USER_AUDIO_INDEX = 1;
    public static final int WALL_INDEX = 0;
    public static final String TAB_INDEX = "tab_index";
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private User user;
    private int wallPage = 0;
    private Group group;
    private Mode mode = Mode.USER;
    private int defaultIndex = USER_AUDIO_INDEX;
    private boolean youMode = false;
    private ActionBar actionBar;
    private VkWallModel vkWallModel = new VkWallModel();
    private VkAudioModel vkAudioModel = new VkAudioModel();

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
        changeViewPagerItem(defaultIndex);
    }

    private void initViewPager() {
        List<ViewerPage> pages = new ArrayList<>(5);
        LayoutInflater inflater = LayoutInflater.from(this);
        pages.add(createWallTracksPage(inflater));
        pages.add(createAudioTracksPage(inflater));
        pages.add(createAlbumsTracksPage(inflater));
        if (youMode) {
            pages.add(createFavoritesArtistsPage(inflater));
            pages.add(createFeedTracksPage(inflater));
        }
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
            public void onPageSelected(final int index) {
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

    private ViewerPage createFeedTracksPage(LayoutInflater inflater) {
        final VkTracksViewerPage viewer = new VkTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.vk_feed
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getNewsFeedTracks(getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(vkTrackClickListener);
        viewer.setItemLongClickListener(vkTrackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createFavoritesArtistsPage(LayoutInflater inflater) {
        final VkTracksViewerPage viewer = new VkTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.vk_favorites
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getFavoritesTracks(getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(vkTrackClickListener);
        viewer.setItemLongClickListener(vkTrackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createAlbumsTracksPage(LayoutInflater inflater) {
        final VkAlbumViewerPage viewer = new VkAlbumViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.vk_albums
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getAlbums(getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(vkAlbumClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createAudioTracksPage(LayoutInflater inflater) {
        final VkTracksViewerPage viewer = new VkTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.user_audio
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getUserAudio(getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(vkTrackClickListener);
        viewer.setItemLongClickListener(vkTrackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createWallTracksPage(LayoutInflater inflater) {
        final VkTracksViewerPage viewer = new VkTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.vk_wall
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getWallTracks(getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(vkTrackClickListener);
        viewer.setItemLongClickListener(vkTrackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private void getFavoritesTracks(int limit, int page, final VkTracksViewerPage viewer) {
        if (mode == Mode.USER) {
            vkWallModel.getVkUserFavoritesAudio(limit,
                    limit * page, new VkSimpleCallback<List<VkTrack>>() {
                        @Override
                        public void success(List<VkTrack> tracks) {
                            viewer.fill(tracks);
                        }

                        @Override
                        public void failure(VkError error) {
                            ErrorNotifier.showError(UserViewerVkActivity.this, error.getErrorMessage());

                        }
                    });
        }
    }

    private void getNewsFeedTracks(final int limit, int page, final VkTracksViewerPage viewer) {
        if (mode == Mode.USER) {
            vkWallModel.getVkNewsFeedTracks(limit, limit * page,
                    new VkSimpleCallback<List<VkTrack>>() {
                        @Override
                        public void success(List<VkTrack> tracks) {
                            viewer.fill(tracks);
                            if (getViewer(NEWS_FEED).getItems().size() < 20) {
                                getNewsFeedTracks(limit, viewer.getPage(), viewer);
                            }
                        }

                        @Override
                        public void failure(VkError error) {
                            ErrorNotifier.showError(UserViewerVkActivity.this, error.getErrorMessage());

                        }
                    });
        }
    }

    private void getAlbums(int limit, int page, final VkAlbumViewerPage viewer) {
        VkSimpleCallback<List<VkAlbum>> callback = new VkSimpleCallback<List<VkAlbum>>() {
            @Override
            public void success(List<VkAlbum> albums) {
                viewer.fill(albums);
            }

            @Override
            public void failure(VkError error) {

            }
        };
        if (mode == Mode.USER) {
            vkAudioModel.getUserVkAlbums(user.getUid(), limit * page, limit, callback);
        } else {
            vkAudioModel.getGroupVkAlbums(group.getGid(), limit * page, limit, callback);
        }
    }

    private void getUserAudio(int limit, int page, final VkTracksViewerPage viewer) {
        VkSimpleCallback<List<VkTrack>> callback = new VkSimpleCallback<List<VkTrack>>() {
            @Override
            public void success(List<VkTrack> tracks) {
                viewer.fill(tracks);
            }

            @Override
            public void failure(VkError error) {
                ErrorNotifier.showError(UserViewerVkActivity.this, error.getErrorMessage());
            }
        };
        if (mode == Mode.USER) {
            vkAudioModel.getVkUserAudio(user.getUid(), limit, page, callback);
        } else {
            vkAudioModel.getVkGroupAudio(group.getGid(), limit, page, callback);
        }
    }

    private void getWallTracks(int limit, int page, final VkTracksViewerPage viewer) {
        VkSimpleCallback<List<VkTrack>> callback = new VkSimpleCallback<List<VkTrack>>() {
            @Override
            public void success(List<VkTrack> tracks) {
                viewer.fill(tracks);
            }

            @Override
            public void failure(VkError error) {
                ErrorNotifier.showError(UserViewerVkActivity.this, error.getErrorMessage());
            }
        };
        if (mode == Mode.USER) {
            vkWallModel.getVkUserWallAudio(user.getUid(), limit,
                    limit * page, callback);
        } else {
            vkWallModel.getVkGroupWallAudio(group.getGid(), TRACKS_IN_TOP_COUNT,
                    limit * page, callback);
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
                if (getViewer(currentItem).isNotLoaded()) return true;
                addToMainPlaylist(Converter.convertLastfmTrackList(getViewer(currentItem).getItems()));
                Toast.makeText(UserViewerVkActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (getViewer(currentItem).isNotLoaded()) return true;
                saveAsPlaylist(Converter.convertLastfmTrackList(getViewer(currentItem).getItems()));
            }
            return true;
        }
        return false;
    }

    public enum Mode {
        USER,
        GROUP
    }
}