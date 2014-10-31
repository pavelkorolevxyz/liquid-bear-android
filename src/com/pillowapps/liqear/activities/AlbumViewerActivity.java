package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.ListArrayAdapter;
import com.pillowapps.liqear.components.ResultSherlockActivity;
import com.pillowapps.liqear.connection.GetResponseCallback;
import com.pillowapps.liqear.connection.Params;
import com.pillowapps.liqear.connection.QueryManager;
import com.pillowapps.liqear.connection.ReadyResult;
import com.pillowapps.liqear.global.Config;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Track;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AlbumViewerActivity extends ResultSherlockActivity {
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final int ALBUM_INFO_INDEX = 1;
    public static final int TRACKS_INDEX = 0;
    @SuppressWarnings("rawtypes")
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisc()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(500))
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private ListArrayAdapter<Track> tracksAdapter;
    private ListView tracksListView;
    private ProgressBar tracksProgressBar;
    private Album album;
    private View infoTab;
    private View tracksTab;
    private ImageView albumCoverImageView;
    private TextView artistTextView;
    private TextView titleTextView;
    private TextView otherTextView;
    private ProgressBar progressBar;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);
        Bundle extras = getIntent().getExtras();
        album = new Album(extras.getString((ARTIST)), extras.getString(ALBUM));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(album.getNotation());
        initUi();
        getAlbum(album);
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
        final List<View> views = new ArrayList<View>();
        tracksTab = inflater.inflate(R.layout.list_tab, null);
        views.add(tracksTab);
        infoTab = inflater.inflate(R.layout.album_info_layout, null);
        views.add(infoTab);
        final ArtistViewerAdapter adapter = new ArtistViewerAdapter(views);
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
            public void onPageSelected(int i) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * Context menu items' positions.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
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
        if (index == 0) {
            inflater.inflate(R.menu.to_playlist_menu, menu);
        } else {
            inflater.inflate(R.menu.empty_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                finish();
                Intent intent = new Intent(AlbumViewerActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.to_playlist: {
                if (tracksAdapter == null) return true;
                addToMainPlaylist(tracksAdapter.getValues());
                Toast.makeText(AlbumViewerActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
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
            tracksAdapter = new ListArrayAdapter<Track>(
                    AlbumViewerActivity.this, trackList, Track.class, null
            );
            setTracksAdapter(tracksAdapter);
        } else {
            tracksAdapter.addValues(trackList);
            tracksAdapter.notifyDataSetChanged();
        }
    }

    private void getAlbum(Album album) {
        tracksProgressBar.setVisibility(View.VISIBLE);
        QueryManager.getInstance().getAlbumInfo(album, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result, Params.ApiSource.LASTFM)) {
                    tracksProgressBar.setVisibility(View.GONE);
                    return;
                }
                List<Object> list = (List<Object>) result.getObject();
                fillWithTracklist((List<Track>) list.get(1));
                Album album1 = (Album) list.get(0);
                imageLoader.displayImage(album1.getImageUrl(), albumCoverImageView,
                        options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted() {

                    }

                    @Override
                    public void onLoadingFailed(FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(Bitmap bitmap) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                artistTextView.setText(album1.getArtist());
                titleTextView.setText(album1.getTitle());
                otherTextView.setText(album1.getPublishDate());
                tracksProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private class ArtistViewerAdapter extends PagerAdapter {

        List<View> views = null;
        private String[] titles = new String[]{
                Config.resources.getString(R.string.tracks).toLowerCase(),
                Config.resources.getString(R.string.album_info).toLowerCase(),
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