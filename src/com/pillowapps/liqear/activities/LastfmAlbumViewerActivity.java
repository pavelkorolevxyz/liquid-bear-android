package com.pillowapps.liqear.activities;

import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.adapters.ListArrayAdapter;
import com.pillowapps.liqear.components.ResultActivity;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class AlbumViewerActivity extends ResultActivity {
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final int ALBUM_INFO_INDEX = 1;
    public static final int TRACKS_INDEX = 0;
    private ViewPager pager;
    private ListArrayAdapter<Track> tracksAdapter;
    private ListView tracksListView;
    private ProgressBar tracksProgressBar;
    private View infoTab;
    private View tracksTab;
    private ImageView albumCoverImageView;
    private TextView artistTextView;
    private TextView titleTextView;
    private TextView otherTextView;
    private ProgressBar progressBar;
    private LastfmAlbumModel albumModel = new LastfmAlbumModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);


        Bundle extras = getIntent().getExtras();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Album album = new Album(extras.getString((ARTIST)), extras.getString(ALBUM));
        actionBar.setTitle(album.getNotation());

        initUi();

        getAlbumInfo(album);
    }

    private void initUi() {
        initViewPager();
        tracksProgressBar = (ProgressBar) tracksTab.findViewById(R.id.pageProgressBar);
        tracksListView = (ListView) tracksTab.findViewById(R.id.list);
        tracksListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openMainPlaylist(tracksAdapter.getValues(), position);
            }
        });
        tracksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                trackLongClick(tracksAdapter.getValues(), i);
                return true;
            }
        });
        albumCoverImageView = (ImageView) infoTab.findViewById(R.id.album_cover_image_view);
        progressBar = (ProgressBar) infoTab.findViewById(R.id.progressBar);
        artistTextView = (TextView) infoTab.findViewById(R.id.artist_text_view);
        titleTextView = (TextView) infoTab.findViewById(R.id.title_text_view);
        otherTextView = (TextView) infoTab.findViewById(R.id.other_text_view);
    }

    private void initViewPager() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<View> views = new ArrayList<>();
        tracksTab = inflater.inflate(R.layout.list_tab, null);
        views.add(tracksTab);
        infoTab = inflater.inflate(R.layout.album_info_layout, null);
        views.add(infoTab);
        final ArtistViewerAdapter adapter = new ArtistViewerAdapter(views);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);
        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
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
            public void onPageSelected(int i) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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
                if (tracksAdapter == null) return true;
                addToMainPlaylist(tracksAdapter.getValues());
                Toast.makeText(LastfmAlbumViewerActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (tracksAdapter == null) return true;
                saveAsPlaylist(tracksAdapter.getValues());
            }
        }
        return false;
    }

    private void setTracksAdapter(ListAdapter adapter) {
        tracksListView.setAdapter(adapter);
        tracksProgressBar.setVisibility(View.GONE);
    }

    private void fillWithTracklist(List<Track> trackList) {
        if (tracksAdapter == null) {
            tracksAdapter = new ListArrayAdapter<>(
                    LastfmAlbumViewerActivity.this, trackList, Track.class, null
            );
            setTracksAdapter(tracksAdapter);
        } else {
            tracksAdapter.addValues(trackList);
            tracksAdapter.notifyDataSetChanged();
        }
    }

    private void getAlbumInfo(Album album) {
        tracksProgressBar.setVisibility(View.VISIBLE);
        albumModel.getAlbumInfo(album, new SimpleCallback<LastfmAlbum>() {
            @Override
            public void success(LastfmAlbum album) {
                List<LastfmTrack> tracks = album.getTracks().getTracks();
                List<Track> list = Converter.convertLastfmTrackList(tracks);
                fillWithTracklist(list);
                List<LastfmImage> images = album.getImages();
                String imageUrl = null;
                if (images != null) {
                    imageUrl = images.get(images.size() - 1).getUrl();
                }
                Picasso.with(LastfmAlbumViewerActivity.this).load(imageUrl).into(albumCoverImageView,
                        new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                albumCoverImageView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                artistTextView.setText(album.getArtistName());
                titleTextView.setText(album.getTitle());
                String releaseDate = album.getReleaseDate();
                if (releaseDate != null && !releaseDate.isEmpty()) {
                    otherTextView.setText(releaseDate.substring(0, releaseDate.indexOf(",")).trim());
                }
                tracksProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(String errorMessage) {
                tracksProgressBar.setVisibility(View.GONE);
                ErrorNotifier.showError(LastfmAlbumViewerActivity.this, errorMessage);
            }
        });
    }

    private class ArtistViewerAdapter extends PagerAdapter {
        List<View> views = null;
        private String[] titles = new String[]{
                LastfmAlbumViewerActivity.this.getString(R.string.tracks).toLowerCase(),
                LastfmAlbumViewerActivity.this.getString(R.string.album_info).toLowerCase(),
        };

        public ArtistViewerAdapter(List<View> inViews) {
            views = inViews;
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