package com.pillowapps.liqear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.pillowapps.liqear.global.Config;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.Track;
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
    private Artist artist;
    private boolean infoLoaded = false;
    private int personalPage = 1;
    private TextView artistInfoTextView;
    private View infoTab;
    private ProgressBar artistInfoProgressBar;

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
        indicator.setFooterColor(getResources().getColor(R.color.darkest_blue));
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
                        getSimilarArtists(artist.getName());
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
        MenuInflater inflater = getSupportMenuInflater();
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
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
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
        QueryManager.getInstance().getArtistTopTracks(artist, limit, page, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result, Params.ApiSource.LASTFM)) {
                    getViewer(TOP_TRACKS_INDEX).getProgressBar().setVisibility(View.GONE);
                    return;
                }
                fillTracks(result, getViewer(TOP_TRACKS_INDEX));
            }
        });
    }

    private void getArtistAlbums(String artistName) {
        QueryManager.getInstance().getArtistAlbums(artistName, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result, Params.ApiSource.LASTFM)) {
                    getViewer(ALBUMS_INDEX).getProgressBar().setVisibility(View.GONE);
                    return;
                }
                fillAlbums(result, getViewer(ALBUMS_INDEX));
            }
        });
    }

    private void getPersonalTop(String artist, String username, int limit, int page) {
        QueryManager.getInstance().getPersonalArtistTop(artist, username, limit, page,
                new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result, Params.ApiSource.LASTFM)) {
                    getViewer(PERSONAL_TOP_INDEX).getProgressBar().setVisibility(View.GONE);
                    return;
                }
                fillTracks(result, getViewer(PERSONAL_TOP_INDEX));
            }
        });
    }

    private void getSimilarArtists(String artistName) {
        QueryManager.getInstance().getSimilarArtists(artistName, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result, Params.ApiSource.LASTFM)) {
                    getViewer(SIMILAR_INDEX).getProgressBar().setVisibility(View.GONE);
                    return;
                }
                fillArtists(result, getViewer(SIMILAR_INDEX));
            }
        });
    }

    private void getArtistInfo(String artist, String username) {
        QueryManager.getInstance().getArtistInfo(artist, username, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                if (checkForError(result, Params.ApiSource.LASTFM)) {
                    artistInfoProgressBar.setVisibility(View.GONE);
                    return;
                }
                String info = ((String) (((List<Object>) result.getObject()).get(1))).trim();
                Spanned text = Html.fromHtml(info.replace("\n", "<br />"));
                if (text.length() == 0) {
                    artistInfoTextView.setText(getString(R.string.not_found));
                } else {
                    artistInfoTextView.setText(text);
                }
                artistInfoProgressBar.setVisibility(View.GONE);
                infoLoaded = true;
            }
        });
    }

    private class ArtistViewerAdapter extends PagerAdapter {

        List<View> views = null;
        private String[] titles;

        public ArtistViewerAdapter(List<View> inViews) {
            views = inViews;
            if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
                titles = new String[]{
                        Config.resources.getString(R.string.albums).toLowerCase(),
                        Config.resources.getString(R.string.top).toLowerCase(),
                        Config.resources.getString(R.string.top).toLowerCase() + " " + AuthorizationInfoManager.getLastfmName(),
                        Config.resources.getString(R.string.similar).toLowerCase(),
                        Config.resources.getString(R.string.artist_info).toLowerCase()
                };
            } else {
                titles = new String[]{
                        Config.resources.getString(R.string.albums).toLowerCase(),
                        Config.resources.getString(R.string.top).toLowerCase(),
                        Config.resources.getString(R.string.similar).toLowerCase(),
                        Config.resources.getString(R.string.artist_info).toLowerCase(),
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