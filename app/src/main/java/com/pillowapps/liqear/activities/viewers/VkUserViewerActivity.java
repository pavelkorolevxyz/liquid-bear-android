package com.pillowapps.liqear.activities.viewers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.adapters.pagers.PagesPagerAdapter;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.components.OnLoadMoreListener;
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.components.viewers.VkAlbumViewerPage;
import com.pillowapps.liqear.components.viewers.VkTracksViewerPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Group;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.models.Page;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkWallModel;

import java.util.ArrayList;
import java.util.List;

public class VkUserViewerActivity extends PagerResultActivity {
    public static final String USER = "user";
    public static final String GROUP = "group";
    public static final String YOU_MODE = "you";
    public static final int FAVORITES = 3;
    public static final int NEWS_FEED = 4;
    public static final int ALBUM_INDEX = 2;
    public static final int USER_AUDIO_INDEX = 1;
    public static final int WALL_INDEX = 0;
    public static final String TAB_INDEX = "tab_index";
    public static final int PAGES_NUMBER = 5;
    private User user;
    private Group group;
    private Mode mode = Mode.USER;
    private int defaultIndex = USER_AUDIO_INDEX;
    private boolean you = false;
    private VkWallModel vkWallModel = new VkWallModel();
    private VkAudioModel vkAudioModel = new VkAudioModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);

        Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable(USER);
        defaultIndex = extras.getInt(TAB_INDEX);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        you = extras.getBoolean(YOU_MODE, false);
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
        List<Page> pages = new ArrayList<>(PAGES_NUMBER);
        pages.add(createWallTracksPage());
        pages.add(createAudioTracksPage());
        pages.add(createAlbumsTracksPage());
        if (you) {
            pages.add(createFavoritesArtistsPage());
            pages.add(createFeedTracksPage());
        }
        setPages(pages);
        final PagesPagerAdapter adapter = new PagesPagerAdapter(pages);
        injectViewPager(adapter);
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

    private ViewerPage createFeedTracksPage() {
        final VkTracksViewerPage viewer = new VkTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.vk_feed
        );
        viewer.setOnLoadMoreListener(
                new OnLoadMoreListener<Track>() {
                    @Override
                    public void onLoadMore() {
                        getNewsFeedTracks(getPageSize(), viewer.getVkPage(), viewer);
                    }
                });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createFavoritesArtistsPage() {
        final VkTracksViewerPage viewer = new VkTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.vk_favorites
        );
        viewer.setOnLoadMoreListener(
                new OnLoadMoreListener<Track>() {
                    @Override
                    public void onLoadMore() {
                        getFavoritesTracks(getPageSize(), viewer.getVkPage(), viewer);
                    }
                });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createAlbumsTracksPage() {
        final VkAlbumViewerPage viewer = new VkAlbumViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.vk_albums
        );
        viewer.setOnLoadMoreListener(new OnLoadMoreListener<Album>() {
            @Override
            public void onLoadMore() {
                getAlbums(getPageSize(), viewer.getVkPage(), viewer);
            }
        });
        if (mode == Mode.USER) {
            viewer.setItemClickListener(vkAlbumClickListener);
        } else {
            viewer.setItemClickListener(vkGroupAlbumClickListener);
        }
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createAudioTracksPage() {
        final VkTracksViewerPage viewer = new VkTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.user_audio
        );
        viewer.setOnLoadMoreListener(new OnLoadMoreListener<Track>() {
            @Override
            public void onLoadMore() {
                getUserAudio(getPageSize(), viewer.getVkPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createWallTracksPage() {
        final VkTracksViewerPage viewer = new VkTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.vk_wall
        );
        viewer.setOnLoadMoreListener(new OnLoadMoreListener<Track>() {
            @Override
            public void onLoadMore() {
                getWallTracks(getPageSize(), viewer.getVkPage(), viewer);

            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
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
                            ErrorNotifier.showError(VkUserViewerActivity.this, error.getErrorMessage());

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
                                getNewsFeedTracks(limit, viewer.getVkPage(), viewer);
                            }
                        }

                        @Override
                        public void failure(VkError error) {
                            ErrorNotifier.showError(VkUserViewerActivity.this, error.getErrorMessage());

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
                ErrorNotifier.showError(VkUserViewerActivity.this, error.getErrorMessage());
            }
        };
        if (mode == Mode.USER) {
            vkAudioModel.getVkUserAudio(user.getUid(), limit, limit * page, callback);
        } else {
            vkAudioModel.getVkGroupAudio(group.getGid(), limit, limit * page, callback);
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
                ErrorNotifier.showError(VkUserViewerActivity.this, error.getErrorMessage());
            }
        };
        if (mode == Mode.USER) {
            vkWallModel.getVkUserWallAudio(user.getUid(), limit, limit * page, callback);
        } else {
            vkWallModel.getVkGroupWallAudio(group.getGid(), limit, limit * page, callback);
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
                Intent intent = new Intent(VkUserViewerActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.to_playlist: {
                VkTracksViewerPage viewer = (VkTracksViewerPage) getViewer(currentItem);
                if (viewer.isNotLoaded()) return true;
                addToMainPlaylist(viewer.getItems());
                Toast.makeText(VkUserViewerActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(currentItem);
                if (viewer.isNotLoaded()) return true;
                saveAsPlaylist(viewer.getItems());
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