package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.PagerResultSherlockActivity;
import com.pillowapps.liqear.components.ViewerPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.network.GetResponseCallback;
import com.pillowapps.liqear.network.callbacks.LastfmSimpleCallback;
import com.pillowapps.liqear.network.QueryManager;
import com.pillowapps.liqear.network.ReadyResult;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ArtistViewerActivity extends PagerResultSherlockActivity {
    public static final String ARTIST = "artist";
    public static final int PERSONAL_TOP_INDEX = 2;
    public static final int TOP_TRACKS_INDEX = 1;
    public static final int ALBUMS_INDEX = 0;
    public int ARTIST_INFO_INDEX = AuthorizationInfoManager.isAuthorizedOnLastfm() ? 4 : 3;
    public int SIMILAR_INDEX = AuthorizationInfoManager.isAuthorizedOnLastfm() ? 3 : 2;
    @SuppressWarnings("rawtypes")
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private int topTracksPage = 1;
    private int personalPage = 1;
    private int similarPage = 1;
    private Artist artist;
    private boolean infoLoaded = false;
    private TextView artistInfoTextView;
    private View infoTab;
    private ProgressBar artistInfoProgressBar;
    private LastfmArtistModel artistModel = new LastfmArtistModel();

    @SuppressWarnings("unchecked")
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
        getViewer(TOP_TRACKS_INDEX).getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openMainPlaylist(getViewer(TOP_TRACKS_INDEX).getValues(), position);
            }
        });
        getViewer(TOP_TRACKS_INDEX).getListView().setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getArtistTopTracks(artist, TRACKS_IN_TOP_COUNT, topTracksPage++);
            }
        });
        getViewer(TOP_TRACKS_INDEX).getListView()
                .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        trackLongClick(getViewer(TOP_TRACKS_INDEX).getValues(), i);
                        return true;
                    }
                });
        changeViewPagerItem(getIntent().getIntExtra(TAB_INDEX, TOP_TRACKS_INDEX));

        OnItemClickListener similarListener = new

                OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Artist clickedArtist = (Artist) adapterView.getAdapter().getItem(position);
                        Intent intent = new Intent(ArtistViewerActivity.this,
                                ArtistViewerActivity.class);
                        intent.putExtra(ARTIST, clickedArtist.getName());
                        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                    }
                };
        getViewer(SIMILAR_INDEX).getListView().setOnItemClickListener(similarListener);
        OnItemClickListener albumsListener = new

                OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent intent = new Intent(ArtistViewerActivity.this,
                                AlbumViewerActivity.class);
                        Album album = (Album) getViewer(ALBUMS_INDEX).get(position);
                        intent.putExtra(AlbumViewerActivity.ALBUM, album.getTitle());
                        intent.putExtra(AlbumViewerActivity.ARTIST, album.getArtist());
                        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                    }
                };
        getViewer(ALBUMS_INDEX).getListView().setOnItemClickListener(albumsListener);

        if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            getViewer(PERSONAL_TOP_INDEX).getListView()
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view,
                                                int position, long l) {
                            openMainPlaylist(getViewer(PERSONAL_TOP_INDEX).getValues(), position);
                        }
                    });
            getViewer(PERSONAL_TOP_INDEX).getListView().setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView,
                                                       View view, int i, long l) {
                            trackLongClick(getViewer(PERSONAL_TOP_INDEX).getValues(), i);
                            return true;
                        }
                    });
        }

        artistInfoTextView = (TextView) infoTab.findViewById(
                R.id.text_view_scrollable_text_layout);
        artistInfoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        artistInfoProgressBar = (ProgressBar) infoTab.findViewById(
                R.id.progress_bar_scrallable_text_layout);
    }

    private void initViewPager() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<View> views = new ArrayList<View>();
        View view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Album>(view));
        view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Track>(view));
        if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            view = inflater.inflate(R.layout.list_tab, null);
            views.add(view);
            addViewer(new ViewerPage<Track>(view));
        }
        view = inflater.inflate(R.layout.list_tab, null);
        views.add(view);
        addViewer(new ViewerPage<Artist>(view));
        infoTab = inflater.inflate(R.layout.scrollable_text_layout, null);
        views.add(infoTab);
        final ArtistViewerAdapter adapter = new ArtistViewerAdapter(views);
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
                ViewerPage viewer = getViewer(index);
                if (viewer != null && viewer.adapterClean()) {
                    viewer.getProgressBar().setVisibility(View.VISIBLE);
                    if (index == ALBUMS_INDEX) {
                        getArtistAlbums(artist.getName());
                    } else if (index == TOP_TRACKS_INDEX) {
                        getArtistTopTracks(artist, TRACKS_IN_TOP_COUNT, topTracksPage++);
                    } else if (index == PERSONAL_TOP_INDEX
                            && AuthorizationInfoManager.isAuthorizedOnLastfm()) {
                        getPersonalTop(artist.getName(), AuthorizationInfoManager.getLastfmName(),
                                0, personalPage++);
                    } else if (index == SIMILAR_INDEX) {
                        getSimilarArtists(artist.getName(), TRACKS_IN_TOP_COUNT, similarPage++);
                    }
                } else if (index == ARTIST_INFO_INDEX) {
                    if (!infoLoaded) {
                        artistInfoProgressBar.setVisibility(View.VISIBLE);
                        getArtistInfo(artist.getName(), AuthorizationInfoManager.getLastfmName());
                    }
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void changeViewPagerItem(int currentItem) {
        pager.setCurrentItem(currentItem);
        indicator.setCurrentItem(currentItem);
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
                if (getViewer(pager.getCurrentItem()).adapterClean()) return true;
                addToMainPlaylist(getViewer(pager.getCurrentItem()).getValues());
                Toast.makeText(ArtistViewerActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (getViewer(pager.getCurrentItem()).adapterClean()) return true;
                saveAsPlaylist(getViewer(pager.getCurrentItem()).getValues());
            }
            return true;
            case R.id.play_discography: {
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(ArtistViewerActivity.this, R.string.vk_not_authorized,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if (getViewer(ALBUMS_INDEX).adapterClean()) {
                    break;
                }
                getViewer(ALBUMS_INDEX).getProgressBar().setVisibility(View.VISIBLE);
                QueryManager.getInstance().getAlbumsInfo(getViewer(ALBUMS_INDEX).getValues(),
                        new GetResponseCallback() {
                            @Override
                            public void onDataReceived(ReadyResult result) {
                                openMainPlaylist((List<Track>) result.getObject(), 0);
                            }
                        });
            }
            return true;
        }
        return false;
    }

    private void getArtistTopTracks(Artist artist, int limit, int page) {
        final ProgressBar progressBar = getViewer(TOP_TRACKS_INDEX).getProgressBar();
        artistModel.getArtistTopTracks(artist,
                limit, page, new LastfmSimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        progressBar.setVisibility(View.GONE);
                        fillTracks(Converter.convertLastfmTrackList(lastfmTracks),
                                getViewer(TOP_TRACKS_INDEX));
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage, progressBar);
                    }
                });
    }

    private void getArtistAlbums(String artistName) {
        final ProgressBar progressBar = getViewer(ALBUMS_INDEX).getProgressBar();
        artistModel.getArtistAlbums(artistName, new LastfmSimpleCallback<List<LastfmAlbum>>() {
            @Override
            public void success(List<LastfmAlbum> albums) {
                progressBar.setVisibility(View.GONE);
                fillAlbums(albums, getViewer(ALBUMS_INDEX));
            }

            @Override
            public void failure(String errorMessage) {
                showError(errorMessage, progressBar);
            }
        });
    }

    private void getPersonalTop(String artist, String username, int limit, int page) {
        final ProgressBar progressBar = getViewer(PERSONAL_TOP_INDEX).getProgressBar();
        artistModel.getPersonalArtistTop(artist, username, limit, page,
                new LastfmSimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> lastfmTracks) {
                        fillTracks(Converter.convertLastfmTrackList(lastfmTracks),
                                getViewer(PERSONAL_TOP_INDEX));
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage, progressBar);
                    }
                });
    }

    private void showError(String errorMessage, ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
        ErrorNotifier.showError(ArtistViewerActivity.this, errorMessage);
    }

    private void getSimilarArtists(String artistName, int limit, int page) {
        final ProgressBar progressBar = getViewer(SIMILAR_INDEX).getProgressBar();
        artistModel.getSimilarArtists(artistName, limit, page,
                new LastfmSimpleCallback<List<LastfmArtist>>() {
                    @Override
                    public void success(List<LastfmArtist> lastfmArtists) {
                        progressBar.setVisibility(View.GONE);
                        fillArtists(Converter.convertArtistList(lastfmArtists),
                                getViewer(SIMILAR_INDEX));
                    }

                    @Override
                    public void failure(String errorMessage) {
                        showError(errorMessage, progressBar);
                    }
                });
    }

    private void getArtistInfo(String artist, String username) {
        artistModel.getArtistInfo(artist, username,
                new LastfmSimpleCallback<LastfmArtist>() {
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
                        showError(errorMessage, artistInfoProgressBar);
                    }
                });
    }

    private class ArtistViewerAdapter extends PagerAdapter {
        List<View> views = null;
        private String[] titles;

        public ArtistViewerAdapter(List<View> inViews) {
            views = inViews;
            ArtistViewerActivity context = ArtistViewerActivity.this;
            if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
                titles = new String[]{
                        context.getString(R.string.albums).toLowerCase(),
                        context.getString(R.string.top).toLowerCase(),
                        context.getString(R.string.top).toLowerCase() + " " + AuthorizationInfoManager.getLastfmName(),
                        context.getString(R.string.similar).toLowerCase(),
                        context.getString(R.string.artist_info).toLowerCase()
                };
            } else {
                titles = new String[]{
                        context.getString(R.string.albums).toLowerCase(),
                        context.getString(R.string.top).toLowerCase(),
                        context.getString(R.string.similar).toLowerCase(),
                        context.getString(R.string.artist_info).toLowerCase(),
                };
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