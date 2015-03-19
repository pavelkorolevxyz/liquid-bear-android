package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.ViewerViewAdapter;
import com.pillowapps.liqear.components.viewers.LastfmAlbumViewerPage;
import com.pillowapps.liqear.components.viewers.LastfmArtistViewerPage;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.TextViewerPage;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.lastfm.LastfmDiscographyModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ArtistViewerActivity extends PagerResultActivity {
    public static final String ARTIST = "artist";
    public static final int PERSONAL_TOP_INDEX = 2;
    public static final int TOP_TRACKS_INDEX = 1;
    public static final int ALBUMS_INDEX = 0;
    public int ARTIST_INFO_INDEX = AuthorizationInfoManager.isAuthorizedOnLastfm() ? 4 : 3;
    public int SIMILAR_INDEX = AuthorizationInfoManager.isAuthorizedOnLastfm() ? 3 : 2;
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private Artist artist;
    private boolean infoLoaded = false;
    private LastfmArtistModel artistModel = new LastfmArtistModel();

    private View infoTab;
    @InjectView(R.id.progress_bar_scrallable_text_layout)
    protected ProgressBar artistInfoProgressBar;
    @InjectView(R.id.text_view_scrollable_text_layout)
    protected TextView artistInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        Bundle extras = getIntent().getExtras();
        artist = new Artist(extras.getString(ARTIST));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(artist.getName());
        initUi();
    }

    private void initUi() {
        initViewPager();
        ButterKnife.inject(this, infoTab);
        artistInfoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        int defaultIndex = TOP_TRACKS_INDEX;
        changeViewPagerItem(defaultIndex);
    }

    private void initViewPager() {
        List<ViewerPage> pages = new ArrayList<>(5);
        List<View> views = new ArrayList<>(5);
        List<String> titles = new ArrayList<>(5);
        LayoutInflater inflater = LayoutInflater.from(this);
        ViewerPage albumsPage = createAlbumsPage(inflater);
        views.add(albumsPage.getView());
        titles.add(albumsPage.getTitle());
        pages.add(albumsPage);
        ViewerPage topTracksPage = createTopTracksPage(inflater);
        views.add(topTracksPage.getView());
        titles.add(topTracksPage.getTitle());
        pages.add(topTracksPage);
        if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            ViewerPage personalTopTracksPage = createPersonalTopTracksPage(inflater);
            views.add(personalTopTracksPage.getView());
            titles.add(personalTopTracksPage.getTitle());
            pages.add(personalTopTracksPage);
        }
        ViewerPage similarArtistsPage = createSimilarArtistsPage(inflater);
        views.add(similarArtistsPage.getView());
        titles.add(similarArtistsPage.getTitle());
        pages.add(similarArtistsPage);
        TextViewerPage bioPage = createBioPage(inflater);
        infoTab = bioPage.getView();
        views.add(infoTab);
        titles.add(bioPage.getTitle());
        setViewers(pages);
        final ViewerViewAdapter adapter = new ViewerViewAdapter(views, titles);

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
                if (index == ARTIST_INFO_INDEX) {
                    if (!infoLoaded) {
                        getArtistInfo(artist.getName(), AuthorizationInfoManager.getLastfmName());
                    }
                } else {
                    ViewerPage viewer = getViewer(index);
                    if (viewer.isNotLoaded()) {
                        viewer.showProgressBar(true);
                        viewer.onLoadMore();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private TextViewerPage createBioPage(LayoutInflater inflater) {
        return new TextViewerPage(this,
                inflater.inflate(R.layout.scrollable_text_layout, null),
                R.string.artist_info);
    }

    private ViewerPage createSimilarArtistsPage(LayoutInflater inflater) {
        final LastfmArtistViewerPage viewer = new LastfmArtistViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.similar_artists);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getSimilarArtists(artist.getName(), getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(artistClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createPersonalTopTracksPage(LayoutInflater inflater) {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                String.format("%s %s", getString(R.string.top).toLowerCase(), AuthorizationInfoManager.getLastfmName())
        );
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getPersonalTop(artist.getName(), AuthorizationInfoManager.getLastfmName(),
                        getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private ViewerPage createAlbumsPage(LayoutInflater inflater) {
        final LastfmAlbumViewerPage viewer = new LastfmAlbumViewerPage(this,
                inflater.inflate(R.layout.list_tab, null),
                R.string.albums);
        viewer.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getArtistAlbums(artist.getName(), viewer);
            }
        });
        viewer.setItemClickListener(albumClickListener);
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
                getArtistTopTracks(artist, getPageSize(), viewer.getPage(), viewer);
            }
        });
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    private void changeViewPagerItem(int currentItem) {
        pager.setCurrentItem(currentItem);
        indicator.setCurrentItem(currentItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        final int index = pager.getCurrentItem();
        if (index == PERSONAL_TOP_INDEX) {
            inflater.inflate(R.menu.to_playlist_menu, menu);
        } else if (index == TOP_TRACKS_INDEX) {
            inflater.inflate(R.menu.to_playlist_menu, menu);
        } else if (index == SIMILAR_INDEX) {
            inflater.inflate(R.menu.empty_menu, menu);
        } else if (index == ALBUMS_INDEX) {
            inflater.inflate(R.menu.play_discography, menu);
        } else if (index == ARTIST_INFO_INDEX) {
            inflater.inflate(R.menu.empty_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                finish();
                Intent intent = new Intent(ArtistViewerActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.to_playlist: {
                if (getViewer(pager.getCurrentItem()).isNotLoaded()) return true;
                List<Track> tracks = Converter.convertLastfmTrackList(getViewer(pager.getCurrentItem()).getItems());
                addToMainPlaylist(tracks);
                Toast.makeText(ArtistViewerActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (getViewer(pager.getCurrentItem()).isNotLoaded()) return true;
                List<Track> tracks = Converter.convertLastfmTrackList(getViewer(pager.getCurrentItem()).getItems());
                saveAsPlaylist(tracks);
            }
            return true;
            case R.id.play_discography: {
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(ArtistViewerActivity.this, R.string.vk_not_authorized,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if (getViewer(ALBUMS_INDEX).isNotLoaded()) {
                    break;
                }
                getViewer(ALBUMS_INDEX).showProgressBar(true);
                List<LastfmAlbum> values = getViewer(ALBUMS_INDEX).getItems();
                new LastfmDiscographyModel().getDiscographyTracks(values, new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> tracks) {
                        openMainPlaylist(Converter.convertLastfmTrackList(tracks), 0);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(ArtistViewerActivity.this, errorMessage);
                    }
                });
            }
            return true;
        }
        return false;
    }

    private void getArtistTopTracks(Artist artist, int limit, int page, final LastfmTracksViewerPage viewer) {
        artistModel.getArtistTopTracks(artist,
                limit, page, new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        viewer.fill(lastfmTracks);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage);
                    }
                });
    }

    private void getArtistAlbums(String artistName, final LastfmAlbumViewerPage viewer) {
        artistModel.getArtistAlbums(artistName, new SimpleCallback<List<LastfmAlbum>>() {
            @Override
            public void success(List<LastfmAlbum> albums) {
                viewer.fill(albums);
            }

            @Override
            public void failure(String errorMessage) {
                showError(errorMessage);
            }
        });
    }

    private void getPersonalTop(String artist, String username, int limit, int page, final LastfmTracksViewerPage viewer) {
        artistModel.getPersonalArtistTop(artist, username, limit, page,
                new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        viewer.fill(lastfmTracks);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage);
                    }
                });
    }

    private void getSimilarArtists(String artistName, int limit, int page,
                                   final LastfmArtistViewerPage viewer) {
        artistModel.getSimilarArtists(artistName, limit, page,
                new SimpleCallback<List<LastfmArtist>>() {
                    @Override
                    public void success(List<LastfmArtist> lastfmArtists) {
                        viewer.fill(lastfmArtists);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage);
                    }
                });
    }

    private void getArtistInfo(String artist, String username) {
        artistInfoProgressBar.setVisibility(View.VISIBLE);
        artistModel.getArtistInfo(artist, username,
                new SimpleCallback<LastfmArtist>() {
                    @Override
                    public void success(LastfmArtist lastfmArtist) {
                        artistInfoProgressBar.setVisibility(View.GONE);
                        String info = lastfmArtist.getBio().getContent().trim();
                        Spanned text = Html.fromHtml(info.replace("\n", "<br />"));
                        if (text.length() == 0) {
                            artistInfoTextView.setText(getString(R.string.not_found));
                        } else {
                            artistInfoTextView.setText(text);
                        }
                        artistInfoProgressBar.setVisibility(View.GONE);
                        infoLoaded = true;
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage);
                    }
                });
    }
}