package com.pillowapps.liqear.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmUserViewerActivity;
import com.pillowapps.liqear.activities.viewers.VkUserViewerActivity;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.components.ResultActivity;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Group;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTag;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmSetlist;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkGroup;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.entities.vk.VkUser;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.SetlistfmUtils;
import com.pillowapps.liqear.models.lastfm.LastfmAlbumModel;
import com.pillowapps.liqear.models.lastfm.LastfmArtistModel;
import com.pillowapps.liqear.models.lastfm.LastfmTagModel;
import com.pillowapps.liqear.models.lastfm.LastfmUserModel;
import com.pillowapps.liqear.models.setlistsfm.SetlistsfmSetlistModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkFriendModel;
import com.pillowapps.liqear.models.vk.VkGroupModel;
import com.pillowapps.liqear.network.callbacks.SetlistfmSimpleCallback;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class SearchActivity extends ResultActivity implements OnItemClickListener {
    public static final String SEARCH_MODE = "search_destiny";
    public static final int USERS_COUNT = 20;
    private QuickSearchArrayAdapter adapter;
    private ProgressBar progressBar;
    private SearchMode aim;
    private EditText editText;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisc()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(500))
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private LoadMoreListView listView;
    private int page = 1;
    private String searchQuery;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        aim = (SearchMode) extras.getSerializable(SEARCH_MODE);
        editText = (EditText) findViewById(R.id.search_edit_text_quick_search_layout);
        listView = (LoadMoreListView) findViewById(R.id.list);
        emptyTextView = (TextView) findViewById(R.id.empty);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.edit_part_quick_search_layout);
        ImageButton searchButton = (ImageButton) findViewById(
                R.id.search_button_quick_search_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        switch (aim) {
            case ARTIST:
                actionBar.setTitle(getString(R.string.artist_radio));
                editText.setHint(getString(R.string.artist_radio));
                searchButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                listView.setOnCreateContextMenuListener(this);
                loadArtistPresets();
                break;
            case TAG:
                actionBar.setTitle(getString(R.string.tag_radio));
                editText.setHint(getString(R.string.tag_radio));
                searchButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                loadTagPresets();
                break;
            case ALBUM:
                actionBar.setTitle(getString(R.string.album));
                editText.setHint(getString(R.string.album));
                searchButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                loadAlbumPresets();
            case VK_SIMPLE_SEARCH:
                actionBar.setTitle(getString(R.string.vk_simple_search));
                searchButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                setTrackLongClick();
                break;
            case NEIGHBOURS:
                actionBar.setTitle(getResources().getString(R.string.neighbours));
                ll.setVisibility(View.GONE);
                getNeighbours(AuthorizationInfoManager.getLastfmName(), 50);
                break;
            case LASTFM_FRIENDS:
                actionBar.setTitle(getResources().getString(R.string.friends));
                searchButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(SearchActivity.this,
                                LastfmUserViewerActivity.class);
                        intent.putExtra(LastfmUserViewerActivity.USER,
                                new User(editText.getText().toString()));
                        intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                                LastfmUserViewerActivity.LOVED_INDEX);
                        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                    }
                });
                if (AuthorizationInfoManager.isAuthorizedOnLastfm()) {
                    getLastfmFriends(AuthorizationInfoManager.getLastfmName(), USERS_COUNT, 0);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case GROUP:
                actionBar.setTitle(getResources().getString(R.string.group));
                ll.setVisibility(View.GONE);
                getVkGroups();
                break;
            case VK_FRIENDS:
                actionBar.setTitle(getResources().getString(R.string.vk_friends));
                ll.setVisibility(View.GONE);
                getVkFriends();
                break;
            case VK_RECOMMENDATIONS: {
                actionBar.setTitle(getResources().getString(R.string.recommendations));
                searchVkRecommendations(TRACKS_IN_TOP_COUNT, page++);
                ll.setVisibility(View.GONE);
                listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        searchVkRecommendations(TRACKS_IN_TOP_COUNT, page++);
                    }
                });
                setTrackLongClick();
                break;
            }
            case AUDIO_SEARCH_RESULT: {
                actionBar.setTitle(getResources().getString(R.string.pick_good_result));
                ll.setVisibility(View.GONE);
                String target = getIntent().getStringExtra(Constants.TARGET);
                searchButton.setVisibility(View.GONE);
                if (target != null && !target.isEmpty()) {
                    searchVK(target, 100);
                } else {
                    searchVK(Timeline.getInstance().getCurrentTrack().getNotation(), 100);
                }
                setTrackLongClick();
                break;
            }
            case AUDIO_SEARCH_RESULT_ADD_VK:
                actionBar.setTitle(getResources().getString(R.string.pick_good_result));
                ll.setVisibility(View.GONE);
                String target = getIntent().getStringExtra(Constants.TARGET);
                searchButton.setVisibility(View.GONE);
                if (target != null && !target.isEmpty()) {
                    searchVK(target, 100);
                } else {
                    searchVK(Timeline.getInstance().getCurrentTrack().getNotation(), 100);
                }
                break;
            case PLAYLIST_TRACKLIST: {
                ll.setVisibility(View.GONE);
                String title = extras.getString("title");
                actionBar.setTitle(title);
                List<Track> tracks = PlaylistManager.getInstance().getPlaylist(extras.getLong("pid"));
                adapter = new QuickSearchArrayAdapter<>(
                        SearchActivity.this, tracks, Track.class);
                setAdapter();
                listView.setOnCreateContextMenuListener(this);
                setTrackLongClick();
            }
            break;
            case VK_ALBUM_TRACKLIST: {
                actionBar.setTitle(extras.getString("title"));
                long uid = extras.getLong("uid", -1);
                if (uid == -1) {
                    long gid = extras.getLong("gid", -1);
                    getVkGroupAudioFromAlbum(gid, extras.getLong("album_id"));
                } else {
                    getVkUserAudioFromAlbum(uid, extras.getLong("album_id"));
                }
                ll.setVisibility(View.GONE);
                setTrackLongClick();
            }
            break;
            case TRACKLIST_FUNKY: {
                ll.setVisibility(View.GONE);
                List<String> stringArrayList = extras.getStringArrayList("tracks");
                String artist = extras.getString("artist");
                String albumTitle = extras.getString("title");
                actionBar.setTitle(artist + " - " + albumTitle);
                List<Track> tracks = new ArrayList<>();
                for (String trackTitle : stringArrayList) {
                    tracks.add(new Track(artist, trackTitle));
                }
                adapter = new QuickSearchArrayAdapter<>(
                        SearchActivity.this, tracks, Track.class);
                setAdapter();
                setTrackLongClick();
            }
            break;
            case TRACKLIST_SETLIST: {
                ll.setVisibility(View.GONE);
                List<String> stringArrayList = extras.getStringArrayList("tracks");
                String artist = extras.getString("artist");
                actionBar.setTitle(extras.getString("notation"));
                List<Track> tracks = new ArrayList<Track>();
                boolean live = PreferencesManager.getPreferences().getBoolean("search_live", false);
                for (String trackTitle : stringArrayList) {
                    tracks.add(new Track(artist, trackTitle, live));
                }
                adapter = new QuickSearchArrayAdapter<Track>(
                        SearchActivity.this, tracks, Track.class);
                setAdapter();
                setTrackLongClick();
            }
            break;
            case SETLIST: {
                String artist = extras.getString("artist");
                String venue = extras.getString("venue");
                String city = extras.getString("city");
                searchSetlists(artist, venue, city);
                ll.setVisibility(View.GONE);
            }
            break;
            case LOCAL_TRACKS: {
                String albumId = extras.getString("albumId");
                String artistId = extras.getString("artistId");
                String filePath = extras.getString("filePath");
                if (albumId == null && artistId == null && filePath == null) {
                    getLocalTracks();
                } else if (albumId != null) {
                    getTrackFromLocalAlbum(albumId);
                } else if (artistId != null) {
                    getTrackFromLocalArtist(artistId);
                } else {
                    getTracksByFolder(filePath);
                }
                ll.setVisibility(View.GONE);
                actionBar.setTitle(getString(R.string.tracks));
                setTrackLongClick();
                break;
            }
            case LOCAL_FOLDERS: {
                getLocalFolders();
                ll.setVisibility(View.GONE);
                actionBar.setTitle(getString(R.string.folder_mode));
                break;
            }
            case LOCAL_ARTISTS: {
                getLocalArtists();
                ll.setVisibility(View.GONE);
                actionBar.setTitle(getString(R.string.artist_radio));
                break;
            }
            case LOCAL_ALBUMS: {
                getLocalAlbums();
                ll.setVisibility(View.GONE);
                actionBar.setTitle(getString(R.string.album));
                break;
            }
        }
        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchQuery = editText.getText().toString().trim();
                switch (aim) {
                    case ARTIST:
                        if (searchQuery.length() == 0) {
                            loadArtistPresets();
                            return;
                        }
                        searchArtist(searchQuery, TRACKS_IN_TOP_COUNT, 0);
                        break;
                    case TAG:
                        if (searchQuery.length() == 0) {
                            loadTagPresets();
                            return;
                        }
                        searchTag(searchQuery, TRACKS_IN_TOP_COUNT, 0);
                        break;
                    case ALBUM:
                        if (searchQuery.length() == 0) {
                            loadAlbumPresets();
                            return;
                        }
                        searchAlbum(searchQuery, TRACKS_IN_TOP_COUNT, 0);
                        break;
                    case VK_SIMPLE_SEARCH:
                        if (searchQuery.length() == 0) return;
                        searchVK(searchQuery, TRACKS_IN_TOP_COUNT);
                        break;
                }
                if (SearchMode.LASTFM_FRIENDS != aim) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        listView.setOnItemClickListener(this);
    }

    private void setTrackLongClick() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                trackLongClick(adapter.getValues(), i);
                return true;
            }
        });
    }

    private void searchArtist(String searchQuery, int limit, int page) {
        new LastfmArtistModel().searchArtist(searchQuery,
                limit,
                page,
                new SimpleCallback<List<LastfmArtist>>() {
                    @Override
                    public void success(List<LastfmArtist> lastfmArtists) {
                        progressBar.setVisibility(View.GONE);
                        fillWithArtists(Converter.convertArtistList(lastfmArtists));
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void searchTag(String searchQuery, int limit, int page) {
        new LastfmTagModel().searchTag(searchQuery, limit, page,
                new SimpleCallback<List<LastfmTag>>() {
                    @Override
                    public void success(List<LastfmTag> tags) {
                        fillWithTags(Converter.convertTags(tags));
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void searchAlbum(String searchQuery, int limit, int page) {
        new LastfmAlbumModel().searchAlbum(searchQuery, limit, page,
                new SimpleCallback<List<LastfmAlbum>>() {
                    @Override
                    public void success(List<LastfmAlbum> lastfmAlbums) {
                        progressBar.setVisibility(View.GONE);
                        fillWithAlbums(Converter.convertAlbums(lastfmAlbums));
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void searchVK(String searchQuery, int count) {
        if (!searchQuery.equals(this.searchQuery)) {
            if (adapter != null) adapter.clear();
        }
        this.searchQuery = searchQuery;
        new VkAudioModel().searchAudio(searchQuery, 0, count, new VkSimpleCallback<List<VkTrack>>() {
            @Override
            public void success(List<VkTrack> data) {
                fillWithVkTracklist(data);
                adapter.setHighlighted(PreferencesManager.getUrlNumberPreferences()
                        .getInt(getIntent().getStringExtra(Constants.TARGET), 0));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(VkError error) {

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getVkUserAudioFromAlbum(long uid, long albumId) {
        new VkAudioModel().getUserAudioFromAlbum(uid, albumId, 0, 0,
                vkTracksCallback());
    }

    private VkSimpleCallback<List<VkTrack>> vkTracksCallback() {
        return new VkSimpleCallback<List<VkTrack>>() {
            @Override
            public void success(List<VkTrack> data) {
                fillWithVkTracklist(data);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(VkError error) {
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    private void getVkGroupAudioFromAlbum(long gid, long albumId) {
        new VkAudioModel().getGroupAudioFromAlbum(gid, albumId, 0, 0,
                vkTracksCallback());
    }

    private void loadAlbumPresets() {
        LinkedHashSet<Album> albums = new LinkedHashSet<Album>(Constants.PRESET_WANTED_COUNT);
        SharedPreferences artistPreferences = PreferencesManager.getAlbumPreferences();
        int albumCount = artistPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
        if (albumCount >= Constants.PRESET_WANTED_COUNT) {
            for (int i = albumCount - 1; i >= albumCount - Constants.PRESET_WANTED_COUNT; i--) {
                albums.add(new Album(
                        artistPreferences.getString(Constants.ALBUM_ARTIST_NUMBER + (i % Constants.PRESET_WANTED_COUNT), ""),
                        artistPreferences.getString(Constants.ALBUM_TITLE_NUMBER + (i % Constants.PRESET_WANTED_COUNT), ""),
                        null,
                        null,
                        artistPreferences.getString(Constants.IMAGE + (i % Constants.PRESET_WANTED_COUNT), "")
                ));
            }
        } else {
            for (int i = albumCount - 1; i >= 0; i--) {
                albums.add(new Album(
                        artistPreferences.getString(Constants.ALBUM_ARTIST_NUMBER + i, ""),
                        artistPreferences.getString(Constants.ALBUM_TITLE_NUMBER + i, ""),
                        null,
                        null,
                        artistPreferences.getString(Constants.IMAGE + i, "")
                ));
            }
        }
        fillWithAlbums(new ArrayList<Album>(albums));
    }

    private void loadTagPresets() {
        LinkedHashSet<Tag> tags = new LinkedHashSet<Tag>(Constants.PRESET_WANTED_COUNT);
        SharedPreferences tagPreferences = PreferencesManager.getTagPreferences();
        int tagsCount = tagPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
        if (tagsCount >= Constants.PRESET_WANTED_COUNT) {
            for (int i = tagsCount - 1; i >= tagsCount - Constants.PRESET_WANTED_COUNT; i--) {
                tags.add(new Tag(tagPreferences.getString(Constants.TAG_NUMBER
                        + (i % Constants.PRESET_WANTED_COUNT), "")));
            }
        } else {
            for (int i = tagsCount - 1; i >= 0; i--) {
                tags.add(new Tag(tagPreferences.getString(Constants.TAG_NUMBER + i, "")));
            }
        }
        String[] presets = getResources().getStringArray(R.array.tag_presets);
        int i = 0;
        while (tags.size() < Constants.PRESET_WANTED_COUNT) {
            tags.add(new Tag(presets[i++]));
        }
        fillWithTags(new ArrayList<Tag>(tags));
    }

    private void loadArtistPresets() {
        LinkedHashSet<Artist> artists = new LinkedHashSet<Artist>(Constants.PRESET_WANTED_COUNT);
        SharedPreferences artistPreferences = PreferencesManager.getArtistPreferences();
        int artistCount = artistPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
        if (artistCount >= Constants.PRESET_WANTED_COUNT) {
            for (int i = artistCount - 1; i >= artistCount - Constants.PRESET_WANTED_COUNT; i--) {
                Artist artist = new Artist(artistPreferences
                        .getString(Constants.ARTIST_NUMBER + (i % Constants.PRESET_WANTED_COUNT), ""));
                artist.setPreviewUrl(artistPreferences
                        .getString(Constants.IMAGE + (i % Constants.PRESET_WANTED_COUNT), ""));
                artists.add(artist);
            }
        } else {
            for (int i = artistCount - 1; i >= 0; i--) {
                Artist artist = new Artist(artistPreferences
                        .getString(Constants.ARTIST_NUMBER + i, ""));
                artist.setPreviewUrl(artistPreferences.getString(Constants.IMAGE + i, ""));
                artists.add(artist);
            }
        }
        fillWithArtists(new ArrayList<Artist>(artists));
    }

    /**
     * Context menu items' positions.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v == listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            if (SearchMode.ARTIST == aim) {
                menu.setHeaderTitle(((Artist) listView.getAdapter()
                        .getItem(info.position)).getName());
                String[] menuItems = getResources()
                        .getStringArray(R.array.artist_item_context_menu);
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            } else if (SearchMode.PLAYLIST_TRACKLIST == aim) {
                menu.setHeaderTitle(((Track) listView.getAdapter()
                        .getItem(info.position)).getNotation());
                String[] menuItems = getResources()
                        .getStringArray(R.array.playlist_tracklist_item_menu);
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        if (SearchMode.ARTIST == aim) {
            switch (item.getItemId()) {
                case 0:
                    Intent intent = new Intent(SearchActivity.this,
                            LastfmArtistViewerActivity.class);
                    intent.putExtra(LastfmArtistViewerActivity.ARTIST,
                            ((Artist) adapter.getItem(info.position)).getName());
                    intent.putExtra(LastfmArtistViewerActivity.TAB_INDEX,
                            AuthorizationInfoManager.isAuthorizedOnLastfm() ? 3 : 2);
                    startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                    break;
                default:
                    break;
            }
        } else if (SearchMode.PLAYLIST_TRACKLIST == aim) {
            switch (item.getItemId()) {
                case 0:
                    adapter.notifyDataSetChanged();
                    PlaylistManager.getInstance().removeTrack(((Track) adapter.get(info.position))
                            .getDbId());
                    adapter.getValues().remove(info.position);
                    break;
                default:
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                finish();
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
            case R.id.to_playlist: {
                if (adapter == null) return true;
                addToMainPlaylist(adapter.getValues());
                Toast.makeText(SearchActivity.this, R.string.added,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
            case R.id.save_as_playlist: {
                if (adapter == null) return true;
                saveAsPlaylist(adapter.getValues());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isTrackMode()) {
            inflater.inflate(R.menu.to_playlist_menu, menu);
        } else {
            inflater.inflate(R.menu.empty_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (aim) {
            case ARTIST: {
                SharedPreferences artistPreferences = PreferencesManager.getArtistPreferences();
                SharedPreferences.Editor editor = artistPreferences.edit();
                int artistLastNumberAll = artistPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
                int artistsLastNumberMod = artistLastNumberAll % Constants.PRESET_WANTED_COUNT;
                editor.putInt(Constants.PRESET_LAST_NUMBER, artistLastNumberAll + 1);
                Artist artist = (Artist) adapter.get(position);
                editor.putString(Constants.ARTIST_NUMBER + artistsLastNumberMod, artist.getName());
                editor.putString(Constants.IMAGE + artistsLastNumberMod,
                        artist.getPreviewUrl());
                editor.apply();
                openArtistByName(artist.getName());
            }
            break;
            case TAG: {
                SharedPreferences tagPreferences = PreferencesManager.getTagPreferences();
                SharedPreferences.Editor editor = tagPreferences.edit();
                int tagLastNumberAll = tagPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
                int tagsLastNumberMod = tagLastNumberAll % Constants.PRESET_WANTED_COUNT;
                editor.putInt(Constants.PRESET_LAST_NUMBER, tagLastNumberAll + 1);
                Tag tag = (Tag) adapter.get(position);
                editor.putString(Constants.TAG_NUMBER + tagsLastNumberMod, tag.getName());
                editor.apply();
                openTag(tag);
            }
            break;
            case ALBUM: {
                SharedPreferences albumPreferences = PreferencesManager.getAlbumPreferences();
                SharedPreferences.Editor editor = albumPreferences.edit();
                int albumLastNumberAll = albumPreferences.getInt(Constants.PRESET_LAST_NUMBER, 0);
                int albumsLastNumberMod = albumLastNumberAll % Constants.PRESET_WANTED_COUNT;
                editor.putInt(Constants.PRESET_LAST_NUMBER, albumLastNumberAll + 1);
                Album album = (Album) adapter.get(position);
                editor.putString(Constants.ALBUM_ARTIST_NUMBER + albumsLastNumberMod,
                        album.getArtist());
                editor.putString(Constants.ALBUM_TITLE_NUMBER + albumsLastNumberMod,
                        album.getTitle());
                editor.putString(Constants.IMAGE + albumsLastNumberMod, album.getImageUrl());
                editor.apply();
                openAlbum(album);
            }
            break;
            case VK_SIMPLE_SEARCH: {
                openMainPlaylist(adapter.getValues(), position);
            }
            break;
            case LASTFM_FRIENDS:
            case NEIGHBOURS: {
                Intent intent = new Intent(SearchActivity.this,
                        LastfmUserViewerActivity.class);
                intent.putExtra(LastfmUserViewerActivity.USER, (User) adapter.get(position));
                intent.putExtra(LastfmUserViewerActivity.TAB_INDEX,
                        LastfmUserViewerActivity.LOVED_INDEX);
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
            break;
            case GROUP: {
                Group group = (Group) adapter.getValues().get(position);
                Intent userViewerIntent = new Intent(SearchActivity.this,
                        VkUserViewerActivity.class);
                userViewerIntent.putExtra(VkUserViewerActivity.GROUP, group);
                startActivityForResult(userViewerIntent, Constants.MAIN_REQUEST_CODE);
            }
            break;
            case VK_FRIENDS: {
                User user = (User) adapter.getValues().get(position);
                Intent userViewerIntent = new Intent(SearchActivity.this,
                        VkUserViewerActivity.class);
                userViewerIntent.putExtra(VkUserViewerActivity.USER, user);
                startActivityForResult(userViewerIntent, Constants.MAIN_REQUEST_CODE);
            }
            break;
            case TRACKLIST_FUNKY:
            case TRACKLIST_SETLIST:
            case VK_ALBUM_TRACKLIST:
            case PLAYLIST_TRACKLIST:
            case VK_RECOMMENDATIONS: {
                openMainPlaylist(adapter.getValues(), position);
                break;

            }
            case LOCAL_TRACKS: {
                openMainPlaylist(adapter.getValues(), position, true);
                break;
            }
            case AUDIO_SEARCH_RESULT: {
                Intent resultData = new Intent();
                int type = getIntent().getIntExtra("type", 1);
                resultData.putExtra("position", position);
                if (type == 1) {
                    Track track = (Track) adapter.get(position);
                    resultData.putExtra("aid", track.getAid());
                    resultData.putExtra("oid", track.getOwnerId());
                }
                setResult(RESULT_OK, resultData);
                finish();
                break;
            }
            case AUDIO_SEARCH_RESULT_ADD_VK: {
                int type = getIntent().getIntExtra("type", 1);
                if (type == 1) {
                    Track track = (Track) adapter.get(position);
                    new VkAudioModel().addToUserAudio(track.getAid(), track.getOwnerId(), new VkSimpleCallback<VkResponse>() {
                        @Override
                        public void success(VkResponse data) {
                            Toast.makeText(LBApplication.getAppContext(),
                                    R.string.added, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(VkError error) {

                        }
                    });
                }
                finish();
                break;
            }
            case SETLIST: {
                Intent searchIntent = new Intent(SearchActivity.this,
                        SearchActivity.class);
                SetlistfmSetlist setlist = (SetlistfmSetlist) adapter.getValues().get(position);
                searchIntent.putStringArrayListExtra("tracks",
                        SetlistfmUtils.getStringTracks(setlist));
                searchIntent.putExtra("artist", setlist.getArtist().getName());
                searchIntent.putExtra("notation", setlist.getNotation());
                searchIntent.putExtra(SEARCH_MODE, SearchMode.TRACKLIST_SETLIST);
                startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
                break;
            }
            case LOCAL_ALBUMS: {
                Intent searchIntent = new Intent(SearchActivity.this,
                        SearchActivity.class);
                Album album = (Album) adapter.getValues().get(position);
                searchIntent.putExtra("albumId", album.getId());
                searchIntent.putExtra(SEARCH_MODE, SearchMode.LOCAL_TRACKS);
                startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
                break;
            }
            case LOCAL_ARTISTS: {
                Intent searchIntent = new Intent(SearchActivity.this,
                        SearchActivity.class);
                Artist artist = (Artist) adapter.getValues().get(position);
                searchIntent.putExtra("artistId", artist.getId());
                searchIntent.putExtra(SEARCH_MODE, SearchMode.LOCAL_TRACKS);
                startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
                break;
            }
            case LOCAL_FOLDERS: {
                Intent searchIntent = new Intent(SearchActivity.this,
                        SearchActivity.class);
                File artist = (File) adapter.getValues().get(position);
                searchIntent.putExtra("filePath", artist.getAbsolutePath());
                searchIntent.putExtra(SEARCH_MODE, SearchMode.LOCAL_TRACKS);
                startActivityForResult(searchIntent, Constants.MAIN_REQUEST_CODE);
                break;
            }
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void setAdapter() {
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void fillWithVkTracklist(List<VkTrack> vkTracks) {
        List<Track> trackList = Converter.convertVkTrackList(vkTracks);
        if (adapter == null) {
            adapter = new QuickSearchArrayAdapter<>(SearchActivity.this,
                    trackList, Track.class);
            emptyTextView.setVisibility(trackList.size() == 0 ? View.VISIBLE : View.GONE);
        } else {
            adapter.addAll(trackList);
        }
        setAdapter();
    }

    private void fillWithTracklist(List<Track> trackList) {
        if (adapter == null) {
            adapter = new QuickSearchArrayAdapter<Track>(SearchActivity.this,
                    trackList, Track.class);
            emptyTextView.setVisibility(trackList.size() == 0 ? View.VISIBLE : View.GONE);
        } else {
            adapter.addAll(trackList);
        }
        setAdapter();
    }

    private void fillWithUsers(List<User> users) {
        emptyTextView.setVisibility(users.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new QuickSearchArrayAdapter<User>(this, users, User.class);
        setAdapter();
    }

    private void fillWithArtists(List<Artist> artists) {
        emptyTextView.setVisibility(artists.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new QuickSearchArrayAdapter<Artist>(this, artists, Artist.class);
        setAdapter();
    }

    private void fillWithAlbums(List<Album> albums) {
        emptyTextView.setVisibility(albums.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new QuickSearchArrayAdapter<Album>(this, albums, Album.class);
        setAdapter();
    }

    private void fillWithSetlists(List<SetlistfmSetlist> setlists) {
        emptyTextView.setVisibility(setlists.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new QuickSearchArrayAdapter<>(this, setlists, SetlistfmSetlist.class);
        setAdapter();
    }

    private void fillWithTags(List<Tag> tags) {
        emptyTextView.setVisibility(tags.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new QuickSearchArrayAdapter<>(this, tags, Tag.class);
        setAdapter();
    }

    private void fillWithFolders(List<File> files) {
        emptyTextView.setVisibility(files.size() == 0 ? View.VISIBLE : View.GONE);
        adapter = new QuickSearchArrayAdapter<>(this, files, File.class);
        setAdapter();
    }

    private void getLastfmFriends(String username, int limit, int page) {
        new LastfmUserModel().getLastfmFriends(username, limit, page, new SimpleCallback<List<LastfmUser>>() {
                    @Override
                    public void success(List<LastfmUser> lastfmUsers) {
                        fillWithUsers(Converter.convertUsers(lastfmUsers));
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void getNeighbours(String username, int limit) {
        new LastfmUserModel().getNeighbours(username, limit,
                new SimpleCallback<List<LastfmUser>>() {
                    @Override
                    public void success(List<LastfmUser> lastfmUsers) {
                        fillWithUsers(Converter.convertUsers(lastfmUsers));
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(String error) {
                        showError(error);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void getVkGroups() {
        new VkGroupModel().getGroups(0, 0, new VkSimpleCallback<List<VkGroup>>() {
            @Override
            public void success(List<VkGroup> data) {
                List<Group> groups = Converter.convertGroups(data);
                adapter = new QuickSearchArrayAdapter<>(SearchActivity.this,
                        groups, Group.class);
                setAdapter();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(VkError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getVkFriends() {
        new VkFriendModel().getFriends(0, 0, new VkSimpleCallback<List<VkUser>>() {
            @Override
            public void success(List<VkUser> data) {
                progressBar.setVisibility(View.GONE);
                fillWithUsers(Converter.convertVkUserList(data));
            }

            @Override
            public void failure(VkError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void searchVkRecommendations(int limit, int page) {
        new VkAudioModel().getVkRecommendations(limit, page * limit,
                new VkSimpleCallback<List<VkTrack>>() {
                    @Override
                    public void success(List<VkTrack> data) {
                        progressBar.setVisibility(View.GONE);
                        fillWithVkTracklist(data);
                    }

                    @Override
                    public void failure(VkError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void searchSetlists(String artist, String venue, final String city) {
        new SetlistsfmSetlistModel().getSetlists(artist, venue, city, new SetlistfmSimpleCallback<List<SetlistfmSetlist>>() {
            @Override
            public void success(List<SetlistfmSetlist> setlists) {
                fillWithSetlists(setlists);
            }

            @Override
            public void failure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getLocalTracks() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Track> result = (List<Track>) msg.obj;
                fillWithTracklist(result);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
                String[] projection = {
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                };

                Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, null, null);

                List<Track> tracks = new ArrayList<Track>();
                while (cursor.moveToNext()) {
                    tracks.add(new Track(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), true));
                }
                Message message = new Message();
                message.obj = tracks;
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    private void getLocalFolders() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<File> files = (List<File>) msg.obj;
                fillWithFolders(files);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
                String[] projection = {
                        MediaStore.Audio.Media.DATA,
                };

                Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, null, null);

                LinkedHashSet<File> files = new LinkedHashSet<File>();
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    files.add(new File(path.substring(0, path.lastIndexOf("/"))));
                }
                Message message = new Message();
                message.obj = new ArrayList<File>(files);
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    private void getLocalArtists() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Artist> result = (List<Artist>) msg.obj;
                fillWithArtists(result);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] projection = {
                        MediaStore.Audio.Artists._ID,
                        MediaStore.Audio.Artists.ARTIST,
                };

                Cursor cursor = managedQuery(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                        projection, null, null, null);
                List<Artist> artists = new ArrayList<Artist>();

                while (cursor.moveToNext()) {
                    Artist artist = new Artist(cursor.getString(1), cursor.getString(0));
                    if (artist.getName().length() > 0)
                        artists.add(artist);
                }
                Message message = new Message();
                message.obj = artists;
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    private void getLocalAlbums() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Album> result = (List<Album>) msg.obj;
                fillWithAlbums(result);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] projection = {
                        MediaStore.Audio.Albums._ID,
                        MediaStore.Audio.Albums.ARTIST,
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums.ALBUM_ART
                };

                Cursor cursor = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        projection, null, null, null);
                List<Album> albums = new ArrayList<Album>();
                while (cursor.moveToNext()) {
                    Album album = new Album(cursor.getString(1), cursor.getString(2), null,
                            cursor.getString(0), cursor.getString(3));
                    if (album.getTitle().length() > 0)
                        albums.add(album);
                }
                Message message = new Message();
                message.obj = albums;
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    private void getTrackFromLocalAlbum(final String id) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Track> result = (List<Track>) msg.obj;
                fillWithTracklist(result);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";

                String[] projection = {
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                };
                String[] whereVal = {id};

                Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, whereVal, null);
                List<Track> tracks = new ArrayList<Track>();
                while (cursor.moveToNext()) {
                    Track track = new Track(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), true);
                    if (track.getArtist().length() > 0 && track.getTitle().length() > 0)
                        tracks.add(track);
                }
                Message message = new Message();
                message.obj = tracks;
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    private void getTrackFromLocalArtist(final String id) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Track> result = (List<Track>) msg.obj;
                fillWithTracklist(result);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String selection = MediaStore.Audio.Media.ARTIST_ID + "=?";

                String[] projection = {
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                };

                String[] whereVal = {id};

                Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, whereVal, null);
                List<Track> tracks = new ArrayList<Track>();
                while (cursor.moveToNext()) {
                    Track track = new Track(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), true);
                    if (track.getArtist().length() > 0 && track.getTitle().length() > 0)
                        tracks.add(track);
                }
                Message message = new Message();
                message.obj = tracks;
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    private void getTracksByFolder(final String filePath) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Track> result = (List<Track>) msg.obj;
                fillWithTracklist(result);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String selection = MediaStore.Audio.Media.DATA + " like ?";

                String[] projection = {
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                };

                String[] whereVal = {filePath + "%"};

                Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, whereVal, null);
                List<Track> tracks = new ArrayList<Track>();
                while (cursor.moveToNext()) {
                    Track track = new Track(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), true);
                    if (track.getArtist().length() > 0 && track.getTitle().length() > 0)
                        tracks.add(track);
                }
                Message message = new Message();
                message.obj = tracks;
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    private boolean isTrackMode() {
        switch (aim) {
            case ARTIST:
            case TAG:
            case ALBUM:
            case NEIGHBOURS:
            case LASTFM_FRIENDS:
            case GROUP:
            case LOCAL_FOLDERS:
            case LOCAL_ARTISTS:
            case LOCAL_ALBUMS:
            case SETLIST:
            case VK_FRIENDS:
                return false;
            case VK_SIMPLE_SEARCH:
            case VK_RECOMMENDATIONS:
            case AUDIO_SEARCH_RESULT:
            case AUDIO_SEARCH_RESULT_ADD_VK:
            case PLAYLIST_TRACKLIST:
            case VK_ALBUM_TRACKLIST:
            case TRACKLIST_FUNKY:
            case TRACKLIST_SETLIST:
            case LOCAL_TRACKS:
                return true;
            default:
                break;
        }
        return false;
    }

    public enum SearchMode {
        ARTIST,
        TAG,
        ALBUM,
        NEIGHBOURS,
        LASTFM_FRIENDS,
        VK_SIMPLE_SEARCH,
        GROUP,
        VK_FRIENDS,
        VK_RECOMMENDATIONS,
        AUDIO_SEARCH_RESULT,
        PLAYLIST_TRACKLIST,
        VK_ALBUM_TRACKLIST,
        TRACKLIST_FUNKY,
        TRACKLIST_SETLIST,
        LOCAL_TRACKS,
        LOCAL_FOLDERS,
        LOCAL_ARTISTS,
        LOCAL_ALBUMS,
        AUDIO_SEARCH_RESULT_ADD_VK, SETLIST
    }

    private class QuickSearchArrayAdapter<T> extends ArrayAdapter<T> {

        private final Context context;
        private final List<T> values;
        private final Class<T> clazz;
        private int highlighted = -1;

        public QuickSearchArrayAdapter(Context context, List<T> values, Class<T> clazz) {
            super(context, R.layout.image_list_item, values);
            this.context = context;
            this.values = values;
            this.clazz = clazz;
        }

        public T get(int position) {
            return values.get(position);
        }

        public List<T> getValues() {
            return values;
        }

        public void addAll(Collection collection) {
            values.addAll(collection);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                if (clazz == Track.class) {
                    convertView = inflater.inflate(R.layout.track_list_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.artist_list_item);
                    holder.secondTextView = (TextView) convertView.findViewById(
                            R.id.title_list_item);
                    holder.positionTextView = (TextView) convertView.findViewById(
                            R.id.position_text_view_list_item);
                    holder.mainLayout = convertView.findViewById(
                            R.id.playlist_tab_item_main_layout);
                } else {
                    convertView = inflater.inflate(R.layout.image_list_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text_list_item);
                    holder.imageView = (ImageView) convertView.findViewById(
                            R.id.image_view_list_item);
                    holder.loadImages = PreferencesManager.getPreferences()
                            .getBoolean("download_images_check_box_preferences", true);
                    holder.mainLayout = convertView.findViewById(
                            R.id.playlist_tab_item_main_layout);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                if (clazz != Track.class) {
                    holder.imageView.setImageBitmap(null);
                }
            }

            T currentItem = values.get(position);
            if (clazz == Artist.class) {
                Artist artist = (Artist) currentItem;
                holder.textView.setText(Html.fromHtml(artist.getName()));
                if (holder.loadImages) {
                    imageLoader.displayImage(artist.getPreviewUrl(), holder.imageView, options);
                } else {
                    holder.imageView.setVisibility(View.GONE);
                }
            } else if (clazz == Tag.class) {
                holder.textView.setText(Html.fromHtml(((Tag) currentItem).getName()));
                holder.imageView.setVisibility(View.GONE);
            } else if (clazz == File.class) {
                holder.textView.setText(Html.fromHtml(((File) currentItem).getPath()));
                holder.imageView.setVisibility(View.GONE);
            } else if (clazz == Group.class) {
                Group group = (Group) currentItem;
                holder.textView.setText(Html.fromHtml(group.getName()));
                if (holder.loadImages) {
                    imageLoader.displayImage(group.getImageUrl(), holder.imageView, options);
                } else {
                    holder.imageView.setVisibility(View.GONE);
                }
            } else if (clazz == User.class) {
                User user = (User) currentItem;
                holder.textView.setText(Html.fromHtml(user.getName()));
                if (holder.loadImages) {
                    imageLoader.displayImage(user.getImageUrl(), holder.imageView, options);
                } else {
                    holder.imageView.setVisibility(View.GONE);
                }
            } else if (clazz == Track.class) {
                Track track = (Track) currentItem;
                holder.textView.setText(Html.fromHtml(track.getArtist()));
                holder.secondTextView.setText(Html.fromHtml(track.getTitle()));
                holder.positionTextView.setText(Integer.toString(position + 1));
                if (aim == SearchMode.AUDIO_SEARCH_RESULT_ADD_VK
                        || aim == SearchMode.AUDIO_SEARCH_RESULT)
                    holder.mainLayout.setBackgroundResource(highlighted == position ?
                                    R.drawable.list_item_background_tinted :
                                    R.drawable.list_item_background
                    );
            } else if (clazz == Album.class) {
                Album album = (Album) currentItem;
                holder.textView.setText(Html.fromHtml(album.getNotation()));
                if (holder.loadImages) {
                    imageLoader.displayImage(album.getImageUrl(), holder.imageView, options);
                } else {
                    holder.imageView.setVisibility(View.GONE);
                }
            } else if (clazz == SetlistfmSetlist.class) {
                holder.textView.setText(((SetlistfmSetlist) currentItem).getNotation());
                holder.imageView.setVisibility(View.GONE);
            }
            return convertView;
        }

        public void setHighlighted(int highlighted) {
            this.highlighted = highlighted;
        }

        private class ViewHolder {
            TextView textView;
            TextView secondTextView;
            TextView positionTextView;
            ImageView imageView;
            boolean loadImages;
            View mainLayout;
        }
    }
}
