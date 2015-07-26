package com.pillowapps.liqear.activities.viewers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.adapters.ViewerViewAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.components.PagerResultActivity;
import com.pillowapps.liqear.components.ViewPage;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.viewers.ViewerPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;
import com.pillowapps.liqear.network.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LastfmAlbumViewerActivity extends PagerResultActivity {
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final int TRACKS_INDEX = 0;
    public static final int PAGES_NUMBER = 2;

    private View infoTab;
    @InjectView(R.id.album_cover_image_view)
    protected ImageView albumCoverImageView;
    @InjectView(R.id.artist_text_view)
    protected TextView artistTextView;
    @InjectView(R.id.title_text_view)
    protected TextView titleTextView;
    @InjectView(R.id.other_text_view)
    protected TextView otherTextView;
    @InjectView(R.id.progressBar)
    protected ProgressBar progressBar;
    private LastfmAlbumModel albumModel = new LastfmAlbumModel();
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        album = new Album(extras.getString((ARTIST)), extras.getString(ALBUM));
        actionBar.setTitle(album.getNotation());

        initUi();

        getAlbumInfo(album, (LastfmTracksViewerPage) getViewer(TRACKS_INDEX));
    }

    private void initUi() {
        initViewPager();

        ButterKnife.inject(this, infoTab);
    }

    private void initViewPager() {
        List<ViewerPage> pages = new ArrayList<>(PAGES_NUMBER);
        List<View> views = new ArrayList<>(PAGES_NUMBER);
        List<String> titles = new ArrayList<>(PAGES_NUMBER);
        ViewerPage albumsPage = createTracksPage();
        views.add(albumsPage.getView());
        titles.add(albumsPage.getTitle());
        pages.add(albumsPage);
        ViewPage albumInfoPage = createAlbumInfoPage();
        infoTab = albumInfoPage.getView();
        views.add(infoTab);
        titles.add(albumInfoPage.getTitle());
        setViewers(pages);
        final ViewerViewAdapter adapter = new ViewerViewAdapter(views, titles);
        injectViewPager(adapter);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private ViewPage createAlbumInfoPage() {
        return new ViewPage(this,
                View.inflate(this, R.layout.album_info_layout, null),
                R.string.album_info);
    }

    private ViewerPage createTracksPage() {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.top_tracks);
        viewer.setSinglePage(true);
        viewer.setItemClickListener(trackClickListener);
        viewer.setItemLongClickListener(trackLongClickListener);
        addViewer(viewer);
        return viewer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        final int index = pager.getCurrentItem();
        if (index == 0) {
            inflater.inflate(R.menu.to_playlist_menu, menu);
        } else {
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
                Intent intent = new Intent(LastfmAlbumViewerActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.to_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(TRACKS_INDEX);
                if (viewer.isNotLoaded()) return true;
                addToMainPlaylist(Converter.convertLastfmTrackList(viewer.getItems()));
                Toast.makeText(LastfmAlbumViewerActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(TRACKS_INDEX);
                if (viewer.isNotLoaded()) return true;
                saveAsPlaylist(Converter.convertLastfmTrackList(viewer.getItems()));
            }
        }
        return false;
    }

    private void getAlbumInfo(Album album, final LastfmTracksViewerPage viewer) {
        albumModel.getAlbumInfo(album, new SimpleCallback<LastfmAlbum>() {
            @Override
            public void success(LastfmAlbum album) {
                List<LastfmTrack> tracks = album.getTracks().getTracks();
                viewer.fill(tracks);
                List<LastfmImage> images = album.getImages();
                String imageUrl = null;
                if (images != null) {
                    imageUrl = images.get(images.size() - 1).getUrl();
                }
                new ImageModel().loadImage(imageUrl, albumCoverImageView, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted() {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String message) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(Bitmap bitmap) {
                        albumCoverImageView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                artistTextView.setText(album.getArtistName());
                titleTextView.setText(album.getTitle());
                String releaseDate = album.getReleaseDate();
                if (releaseDate != null && !releaseDate.trim().isEmpty()) {
                    otherTextView.setText(releaseDate.substring(0, releaseDate.indexOf(",")).trim());
                }
            }

            @Override
            public void failure(String errorMessage) {
                showError(errorMessage);
            }
        });
    }
}