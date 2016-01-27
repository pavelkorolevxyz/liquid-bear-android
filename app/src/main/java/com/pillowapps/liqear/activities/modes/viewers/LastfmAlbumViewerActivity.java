package com.pillowapps.liqear.activities.modes.viewers;

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

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.PagerResultActivity;
import com.pillowapps.liqear.adapters.pagers.PagesPagerAdapter;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.components.viewers.LastfmTracksViewerPage;
import com.pillowapps.liqear.components.viewers.base.ViewerPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Page;
import com.pillowapps.liqear.entities.ViewPage;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LastfmAlbumViewerActivity extends PagerResultActivity {
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final int TRACKS_INDEX = 0;
    public static final int PAGES_NUMBER = 2;

    private View infoTab;
    @Bind(R.id.album_cover_image_view)
    protected ImageView albumCoverImageView;
    @Bind(R.id.artist_text_view)
    protected TextView artistTextView;
    @Bind(R.id.title_text_view)
    protected TextView titleTextView;
    @Bind(R.id.other_text_view)
    protected TextView otherTextView;
    @Bind(R.id.progressBar)
    protected ProgressBar progressBar;

    @Inject
    LastfmAlbumModel albumModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(this).applicationComponent().inject(this);

        setContentView(R.layout.viewer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        Album album = new Album(extras.getString((ARTIST)), extras.getString(ALBUM));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(album.getNotation());
        }

        initUi();

        getAlbumInfo(album, (LastfmTracksViewerPage) getViewer(TRACKS_INDEX));
    }

    private void initUi() {
        initViewPager();

        ButterKnife.bind(this, infoTab);
    }

    private void initViewPager() {
        List<Page> pages = new ArrayList<>(PAGES_NUMBER);
        ViewerPage albumsPage = createTracksPage();
        pages.add(albumsPage);
        ViewPage albumInfoPage = createAlbumInfoPage();
        pages.add(albumInfoPage);
        infoTab = albumInfoPage.getView();
        setPages(pages);
        final PagesPagerAdapter adapter = new PagesPagerAdapter(pages);
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
        return new ViewPage(
                View.inflate(this, R.layout.album_info_layout, null),
                R.string.album_info);
    }

    private ViewerPage createTracksPage() {
        final LastfmTracksViewerPage viewer = new LastfmTracksViewerPage(this,
                View.inflate(this, R.layout.list_tab, null),
                R.string.tracks);
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
            case R.id.to_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(TRACKS_INDEX);
                if (viewer.isNotLoaded()) return true;
                addToMainPlaylist(viewer.getItems());
                Toast.makeText(LastfmAlbumViewerActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                LastfmTracksViewerPage viewer = (LastfmTracksViewerPage) getViewer(TRACKS_INDEX);
                if (viewer.isNotLoaded()) return true;
                saveAsPlaylist(viewer.getItems());
            }
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    LastfmImage lastImage = images.get(images.size() - 1);
                    if (lastImage.getSize().isEmpty() && images.size() > 1) {
                        lastImage = images.get(images.size() - 2);
                    }
                    imageUrl = lastImage.getUrl();
                }
                new ImageModel().loadImage(imageUrl, albumCoverImageView, bitmap -> {
                    albumCoverImageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
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