package com.pillowapps.liqear.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.michaelnovakjr.numberpicker.NumberPicker;
import com.pillowapps.liqear.BuildConfig;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.adapters.ModeAdapter;
import com.pillowapps.liqear.adapters.ModeListAdapter;
import com.pillowapps.liqear.adapters.PhoneFragmentAdapter;
import com.pillowapps.liqear.adapters.PlaylistItemsAdapter;
import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.components.ActivityResult;
import com.pillowapps.liqear.components.ArtistTrackComparator;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.MainActivityStartEnum;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.fragments.ModeListFragment;
import com.pillowapps.liqear.fragments.PhoneFragment;
import com.pillowapps.liqear.fragments.PlaybackControlFragment;
import com.pillowapps.liqear.fragments.TabletFragment;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.PlayingState;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkWallModel;
import com.pillowapps.liqear.network.callbacks.SimpleCallback;
import com.pillowapps.liqear.network.callbacks.VkPassiveCallback;
import com.pillowapps.liqear.network.callbacks.VkSimpleCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.AppRateTheme;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;
import timber.log.Timber;

public class MainActivity extends ActionBarActivity implements ModeListFragment.NavigationDrawerCallbacks {
    public Menu mainMenu;
    private PhoneFragment phoneFragment;
    private ServiceConnection serviceConnection;
    private MusicService musicService;
    private ActivityResult activityResult;
    private PlaylistItemsAdapter playlistItemsAdapter;
    @InjectView(R.id.progressBar)
    protected ProgressBar progressBar;
    private TabletFragment tabletFragment;
    private PlaybackControlFragment playbackControlFragment;
    private ModeAdapter modeAdapter;
    private ModeListFragment modeListFragment;
    private ProgressDialog finalProgress;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AuthorizationInfoManager.isAuthScreenNeeded()) {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.putExtra(Constants.SHOW_AUTHSCREEN_AUTO, true);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.main);

        AppRate.with(this)
                .theme(AppRateTheme.DARK)
                .debug(BuildConfig.DEBUG)
                .fromTop(true)
                .delay(1000)
                .retryPolicy(RetryPolicy.EXPONENTIAL)
                .checkAndShow();

        boolean tabletMode = findViewById(R.id.tablet_layout) != null;
        if (tabletMode) {
            initTabletLayout();
        } else {
            initPhoneLayout();
        }

        ButterKnife.inject(this);
        startMusicService();
    }

    private void initPhoneLayout() {
        phoneFragment = (PhoneFragment) getSupportFragmentManager()
                .findFragmentById(R.id.handset_fragment);
    }

    private void initTabletLayout() {
        playlistItemsAdapter = new PlaylistItemsAdapter(MainActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        playbackControlFragment = (PlaybackControlFragment)
                getSupportFragmentManager().findFragmentById(R.id.playback_controls);

        modeListFragment = (ModeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            drawerLayout.setDrawerListener(toggle);
            toggle.setDrawerIndicatorEnabled(true);

            modeListFragment.setUp(
                    R.id.navigation_drawer,
                    drawerLayout);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toggle.syncState();
        }

        tabletFragment = (TabletFragment)
                getSupportFragmentManager().findFragmentById(R.id.tablet_fragment);
        tabletFragment.init();
    }

    @Override
    protected void onStart() {
        if (!BuildConfig.DEBUG) {
            EasyTracker.getInstance(this).activityStart(this);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        if (isTablet()) showContent();
        super.onSaveInstanceState(outState);
    }

    public void init() {
        playlistItemsAdapter = new PlaylistItemsAdapter(MainActivity.this);
        modeAdapter = new ModeAdapter(MainActivity.this);
        Timber.d(modeAdapter.getValues().toString());
    }

    public void restorePreviousState() {
        updateAdapter();
        setRealPositions();
    }

    private boolean isTablet() {
        return phoneFragment == null && tabletFragment != null;
    }

    public void openDropButton() {
        mainMenu.performIdentifierAction(R.id.track_button, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (Timeline.getInstance().isPlaylistChanged()) {
            updateAdapter();
            changePlaylistWithoutTrackChange();
        }
    }

    public void changePlaylistWithoutTrackChange() {
        Timeline.getInstance().clearQueue();
        setRealPositions();
        Timeline.getInstance().clearPreviousIndexes();
        if (Timeline.getInstance().getPlaylistTracks().size() > 0) {
            PlaylistManager.getInstance().saveUnsavedPlaylist(Timeline.getInstance().getPlaylistTracks());
        }
//        AudioTimeline.setPlaylistChanged(false);
    }

    public void setRealPositions() {
        List<Track> playlist = Timeline.getInstance().getPlaylistTracks();
        for (int i = 0; i < playlist.size(); i++) {
            playlist.get(i).setRealPosition(i);
        }
    }

    @Override
    public void onBackPressed() {
        boolean fixed = false;
        if (playlistItemsAdapter != null && playlistItemsAdapter.isEditMode()) {
            playlistItemsAdapter.setEditMode(false);
            fixed = true;
        }
        if (!isTablet()) {
            if (modeAdapter != null && ModeItemsHelper.isEditMode()) {
                ModeItemsHelper.setEditMode(false);
                modeAdapter.notifyChanges();
                fixed = true;
            }
        } else {
            if (modeListFragment.getAdapter() != null && ModeItemsHelper.isEditMode()) {
                ModeListAdapter modeListAdapter = modeListFragment.getAdapter();
                ModeItemsHelper.setEditMode(false);
                modeListAdapter.notifyChanges();
                fixed = true;
            } else {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
                    drawerLayout.closeDrawers();
                    return;
                }
            }
        }
        if (fixed) return;
        if (PreferencesManager.getPreferences().getBoolean("exit_anyway", false)
                || Timeline.getInstance().getPlayingState() == PlayingState.DEFAULT) {
            destroy();
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                if (toggle != null) toggle.onOptionsItemSelected(item);
            }
            return true;
            case R.id.photo_artist_button: {
                if (currentTrack == null || currentTrack.getArtist() == null) return true;
                if (!Utils.isOnline()) {
                    Toast.makeText(MainActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, ImagePagerActivity.class);
                intent.putExtra(ImagePagerActivity.ARTIST, currentTrack.getArtist());
                startActivity(intent);
            }
            return true;
            case R.id.show_artist_button: {
                if (currentTrack == null || currentTrack.getArtist() == null) return true;
                if (!Utils.isOnline()) {
                    Toast.makeText(MainActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, LastfmArtistViewerActivity.class);
                intent.putExtra(LastfmArtistViewerActivity.ARTIST, currentTrack.getArtist());
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
            return true;
            case R.id.share_track_button: {
                if (currentTrack == null) return true;
                showShareDialog();
            }
            return true;
            case R.id.next_url_button: {
                if (currentTrack == null) return true;
                if (Timeline.getInstance().getCurrentTrack() != null
                        && Timeline.getInstance().getCurrentTrack().isLocal()) {
                    Toast.makeText(MainActivity.this, R.string.track_local,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(MainActivity.this, R.string.vk_not_authorized,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (!Utils.isOnline()) {
                    Toast.makeText(MainActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra(Constants.TARGET, currentTrack.getNotation());
                intent.putExtra(SearchActivity.SEARCH_MODE,
                        SearchActivity.SearchMode.AUDIO_SEARCH_RESULT);
                intent.putExtra(Constants.TYPE, 2);
                startActivityForResult(intent, 7463);
            }
            return true;
            case R.id.lyrics_button: {
                if (currentTrack == null) return true;
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(MainActivity.this, R.string.vk_not_authorized,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (!Utils.isOnline()) {
                    Toast.makeText(MainActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                openLyrics(currentTrack);
            }
            return true;
            case R.id.youtube_video_button: {
                if (currentTrack == null) return true;
                openVideo(currentTrack);
            }
            return true;
            case R.id.add_to_vk_button: {
                if (currentTrack == null) return true;
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(MainActivity.this, R.string.vk_not_authorized,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (!Utils.isOnline()) {
                    Toast.makeText(MainActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                addToVk(currentTrack);
            }
            return true;
            case R.id.love_button: {
                if (currentTrack == null) return true;
                if (!AuthorizationInfoManager.isAuthorizedOnLastfm()) {
                    Toast.makeText(MainActivity.this, R.string.last_fm_not_authorized,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (!Utils.isOnline()) {
                    Toast.makeText(MainActivity.this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                progressBar.setVisibility(View.VISIBLE);
                final Track track = Timeline.getInstance().getCurrentTrack();
                if (!track.isLoved()) {
                    new LastfmTrackModel().love(track, new SimpleCallback<Object>() {
                        @Override
                        public void success(Object data) {
                            progressBar.setVisibility(View.GONE);
                            track.setLoved(true);
                            MainActivity.this.invalidateMenu();
                        }

                        @Override
                        public void failure(String errorMessage) {
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                } else {
                    new LastfmTrackModel().unlove(track, new SimpleCallback<Object>() {
                        @Override
                        public void success(Object o) {
                            progressBar.setVisibility(View.GONE);
                            track.setLoved(false);
                            MainActivity.this.invalidateMenu();
                        }

                        @Override
                        public void failure(String error) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
            return true;
            case R.id.settings: {
                Intent preferencesIntent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(preferencesIntent);
            }
            return true;
            case R.id.equalizer: {
                if (musicService == null) {
                    Toast.makeText(MainActivity.this, R.string.service_not_connected,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, EqualizerActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.timer_button: {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.seekbar_layout, null);
                final NumberPicker sb = (NumberPicker) layout.findViewById(R.id.minutes_picker);
                sb.setRange(1, 1440);
                int timerDefault = PreferencesManager.getPreferences()
                        .getInt(Constants.TIMER_DEFAULT, 10);
                sb.setCurrent(timerDefault);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setView(layout);
                builder.setTitle(R.string.timer);
                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                musicService.setTimer(0);
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                musicService.setTimer(sb.getCurrent() * 60);
                                SharedPreferences.Editor editor =
                                        PreferencesManager.getPreferences().edit();
                                editor.putInt(Constants.TIMER_DEFAULT, sb.getCurrent());
                                editor.apply();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            return true;
            case R.id.exit_button: {
                destroy();
            }
            return true;
            case R.id.playlists_button: {
                Intent intent = new Intent(MainActivity.this, PlaylistsActivity.class);
                intent.putExtra(PlaylistsActivity.AIM,
                        PlaylistsActivity.Aim.SHOW_PLAYLISTS);
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
            return true;
            case R.id.clean_titles: {
                clearTitles();
            }
            return true;
            case R.id.shuffle_button: {
                shufflePlaylist();
            }
            return true;
            case R.id.playlist_edit_mode_button: {
                boolean editMode = !playlistItemsAdapter.isEditMode();
                playlistItemsAdapter.setEditMode(editMode);
            }
            return true;
            case R.id.fixate_search_result_button: {
                fixateSearchResult();
            }
            return true;
            case R.id.find_current_button: {
                findCurrentTrack();
            }
            return true;
            case R.id.sort_by_artist_button: {
                sortByArtist();
            }
            return true;
            case R.id.edit_modes_button: {
                ModeItemsHelper.setEditMode(!ModeItemsHelper.isEditMode());
                if (!isTablet()) {
                    modeAdapter.notifyChanges();
                } else {
//                    if (!landscapeTablet) showMenu();
                    modeListFragment.getAdapter().notifyChanges();
                }
            }
            return true;
            case R.id.menu_search: {
                SharedPreferences savePreferences = PreferencesManager.getSavePreferences();
                boolean visibility = !savePreferences.getBoolean(
                        Constants.SEARCH_PLAYLIST_VISIBILITY, false);
                savePreferences.edit().putBoolean(
                        Constants.SEARCH_PLAYLIST_VISIBILITY, visibility).apply();
                updateSearchVisibility();
            }
            return true;
            default:
                return false;
        }
    }

    private void updateSearchVisibility() {
        if (isTablet()) {
            tabletFragment.updateSearchVisibility();
        } else {
            phoneFragment.updateSearchVisibility();
        }
    }

    private void showShareDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.share_dialog_layout);
        dialog.setTitle(R.string.share_track);
        final Track currentTrack = Timeline.getInstance().getCurrentTrack();
        ImageButton vkButton = (ImageButton) dialog.findViewById(R.id.vk_button);
        Button otherButton = (Button) dialog.findViewById(R.id.other_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        String template = PreferencesManager.getPreferences().getString(Constants.SHARE_FORMAT, getString(R.string.listening_now));
        final Album album = Timeline.getInstance().getCurrentAlbum();
        String artist = "";
        String trackTitle = "";
        String albumTitle = "";
        if (currentTrack != null && currentTrack.getArtist() != null) {
            artist = currentTrack.getArtist();
        }
        if (currentTrack != null && currentTrack.getTitle() != null) {
            trackTitle = currentTrack.getTitle();
        }
        if (album != null && album.getTitle() != null) {
            albumTitle = album.getTitle();
        }
        final String shareBody = template.replace("%a%", artist).replace("%t%", trackTitle).replace("%r%", albumTitle);

        vkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(MainActivity.this, R.string.vk_not_authorized, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                } else if (!Utils.isOnline()) {
                    Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                if (currentTrack == null) return;

                String imageUrl = album != null && album.getArtist().equals(currentTrack.getArtist()) ? album.getImageUrl() : null;
                new VkWallModel().postMessage(shareBody, imageUrl, currentTrack, new VkPassiveCallback());
                Toast.makeText(MainActivity.this, R.string.shared, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_track)));
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void removeTrack(int position) {
        int index = Timeline.getInstance().getIndex();
        if (position < index) {
            Timeline.getInstance().setIndex(index - 1);
        } else if (position == index) {
            Timeline.getInstance().setPlaylistChanged(false);
        }
        Timeline.getInstance().getPlaylistTracks().remove(position);
        updateAdapter();
        changePlaylistWithoutTrackChange();
    }

    private void changePlaylist() {
        changePlaylist(0, true);
    }

    public void updateAdapter() {
        List<Track> playlist = Timeline.getInstance().getPlaylistTracks();
        playlistItemsAdapter.setValues(playlist);
    }

    private void changePlaylist(int position, boolean play) {
        Timeline.getInstance().clearQueue();
        setRealPositions();
        Timeline.getInstance().clearPreviousIndexes();
        if (musicService != null) {
            musicService.pause();
        }
        List<Track> tracks = Timeline.getInstance().getPlaylistTracks();
        if (tracks.size() > 0) {
            if (play) {
                getListView().performItemClick(getPlaylistItemsAdapter().getView(position, null, null), position, position);
            }
            PlaylistManager.getInstance().saveUnsavedPlaylist(tracks);
        }
        if (!isTablet()) phoneFragment.changeViewPagerItem(0);
    }

    private void sortByArtist() {
        if (playlistItemsAdapter == null) return;
        List<Track> tracks = new ArrayList<>(playlistItemsAdapter.getValues());
        Collections.sort(tracks, new ArtistTrackComparator());
        Timeline.getInstance().setPlaylist(new Playlist(tracks));
        updateAdapter();
        Timeline.getInstance().setPlaylistChanged(false);
        changePlaylistWithoutTrackChange();
    }

    private void findCurrentTrack() {
        int currentIndex = Timeline.getInstance().getIndex();
        if (currentIndex >= 0 && currentIndex < Timeline.getInstance().getPlaylistTracks().size()) {
            getListView().setSelection(currentIndex);
        }
    }

    private void shufflePlaylist() {
        if (playlistItemsAdapter == null) return;
        List<Track> tracks = new ArrayList<>(playlistItemsAdapter.getValues());
        Collections.shuffle(tracks);
        Timeline.getInstance().setPlaylist(new Playlist(tracks));
        updateAdapter();
        Timeline.getInstance().setPlaylistChanged(false);
        changePlaylistWithoutTrackChange();
    }

    private void fixateSearchResult() {
        if (playlistItemsAdapter == null) return;
        Timeline.getInstance().setPlaylist(new Playlist(playlistItemsAdapter.getValues()));
        updateAdapter();
        clearSearch();
        Timeline.getInstance().clearPreviousIndexes();
        changePlaylist();
    }

    private void clearSearch() {
        if (isTablet()) {
            tabletFragment.clearFilter();
        } else {
            phoneFragment.clearFilter();
        }
    }

    private void setStartActions(Intent data) {
        MainActivityStartEnum mainActivityStartEnum = (MainActivityStartEnum) data.getSerializableExtra(Constants.ACTION_ENUM);
        if (mainActivityStartEnum != null) {
            switch (mainActivityStartEnum) {
                case PLAY_TRACKS: {
                    if (playlistItemsAdapter.isEditMode()) playlistItemsAdapter.setEditMode(false);
                    int positionToPlay = data.getIntExtra(Constants.POSITION_TO_PLAY, 0);
                    if (!isTablet()) phoneFragment.changeViewPagerItem(0);
                    updateAdapter();
                    changePlaylist(positionToPlay, true);
                }
                break;
                case UPDATE_ADAPTER: {
                    updateAdapter();
                }
                break;
                default:
                    break;
            }
        }
    }

    private void openVideo(Track track) {
        musicService.pause();
        try {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", String.format("%s %s official video",
                    track.getArtist(), track.getTitle()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            String url = String.format("http://m.youtube.com/results?&q=%s",
                    Uri.encode(track.getArtist() + " " + track.getTitle() + " official video"));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    private void clearTitles() {
        String regexp = "[^\\w\\s\\?!&#\\-'\\(\\[\\)\\.,:/]+";
        String regexpBrackets = "\\(((?![^)]*(ft.|live|remix|cover|feat)).*?)\\)";
        List<Track> playlist = Timeline.getInstance().getPlaylistTracks();
        if (playlist == null) return;
        for (int i = 0; i < playlist.size(); i++) {
            Track track = playlist.get(i);
            String title = track.getTitle().replaceAll(regexp, "").replaceAll(regexpBrackets, "");
            String artist = track.getArtist().replaceAll(regexp, "").replaceAll(regexpBrackets, "");
            track.setTitle(title);
            track.setArtist(artist);
        }
        playlistItemsAdapter.notifyDataSetChanged();
        if (playlist.size() > 0) {
            PlaylistManager.getInstance().saveUnsavedPlaylist(playlist);
        }
    }

    private void addToVk(Track track) {
        if (PreferencesManager.getPreferences().getBoolean("add_to_vk_slow", true)) {
            if (track != null) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra(SearchActivity.SEARCH_MODE,
                        SearchActivity.SearchMode.AUDIO_SEARCH_RESULT_ADD_VK);
                intent.putExtra(Constants.TARGET, track.getNotation());
                startActivity(intent);
            }
        } else {
            new VkAudioModel().addToUserAudioFast(track.getNotation(), new VkSimpleCallback<VkResponse>() {
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
    }

    private void openLyrics(Track currentTrack) {
        Intent intent = new Intent(MainActivity.this, TextActivity.class);
        intent.putExtra("artist", currentTrack.getArtist());
        intent.putExtra("title", currentTrack.getTitle());
        intent.putExtra(TextActivity.TEXT_AIM, TextActivity.Aim.LYRICS);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mainMenu = menu;
        if (isTablet()) {
            int menuLayout = R.menu.tablet_menu_no_track;
            if (Timeline.getInstance().getCurrentTrack() != null) {
                menuLayout = R.menu.tablet_menu;
                if (Timeline.getInstance().getCurrentTrack().isLoved()) {
                    menuLayout = R.menu.tablet_menu_loved;
                }
            }
            inflater.inflate(menuLayout, menu);
        } else {
            switch (phoneFragment.getCurrentItem()) {
                case PhoneFragmentAdapter.PLAY_TAB_INDEX: {
                    int menuLayout = R.menu.menu_play_tab_no_current_track;
                    if (Timeline.getInstance().getCurrentTrack() != null) {
                        menuLayout = R.menu.menu_play_tab;
                        if (Timeline.getInstance().getCurrentTrack().isLoved()) {
                            menuLayout = R.menu.menu_play_tab_loved;
                        }
                    }
                    inflater.inflate(menuLayout, menu);
                }
                break;
                case PhoneFragmentAdapter.PLAYLIST_TAB_INDEX:
                    inflater.inflate(R.menu.menu_playlist_tab, menu);
                    break;
                case PhoneFragmentAdapter.MODE_TAB_INDEX:
                    inflater.inflate(R.menu.menu_mode_tab, menu);
                    break;
                default:
                    break;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (finalProgress != null)
            finalProgress.dismiss();
        finalProgress = null;
    }

    private void startMusicService() {
        Timeline.getInstance().clearQueue();
        ProgressDialog progress = null;
        try {
            progress = ProgressDialog.show(MainActivity.this, null, getString(R.string.wait), true);
        } catch (Exception e) {
            // No operations.
        }

        finalProgress = progress;
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
                musicService = binder.getService();
                if (isTablet()) {
                    tabletFragment.setServiceConnected();
                } else {
                    phoneFragment.setServiceConnected();
                }
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
//                if (AudioTimeline.isStateActive()) musicService.showTrackInNotification();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalProgress != null && finalProgress.isShowing())
                            finalProgress.dismiss();
                    }
                });
                if (activityResult != null) {
                    onActivityResult(activityResult.getRequestCode(),
                            activityResult.getResultCode(), activityResult.getData());
                }
            }

            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
        Intent intent = new Intent(LBApplication.getAppContext(), MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        ListView listView = null;
        listView = getListView();
        final Track track = (Track) listView.getAdapter().getItem(info.position);
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(MainActivity.this, PlaylistsActivity.class);
                intent.putExtra(PlaylistsActivity.AIM,
                        PlaylistsActivity.Aim.ADD_TO_PLAYLIST);
                intent.putExtra(Constants.ARTIST, track.getArtist());
                intent.putExtra(Constants.TITLE, track.getTitle());
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                break;
            case 1:
                openLyrics(track);
                break;
            case 2:
                Timeline.getInstance().queueTrack(info.position);
                updateView(Arrays.asList(info.position));
                break;
            case 3:
                if (!Utils.isOnline()) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.vk_not_authorized), Toast.LENGTH_SHORT).show();
                } else {
                    Intent artistInfoIntent = new Intent(MainActivity.this,
                            LastfmArtistViewerActivity.class);
                    artistInfoIntent.putExtra(LastfmArtistViewerActivity.ARTIST, track.getArtist());
                    startActivityForResult(artistInfoIntent, Constants.MAIN_REQUEST_CODE);
                }
                break;
            case 4: //Remove from playlist
                removeTrack(info.position);
                break;
            case 5: //Add to...
                final Track targetTrack = Timeline.getInstance().getPlaylistTracks().get(info.position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.choose_target).setItems(R.array.add_targets,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        new LastfmTrackModel().love(targetTrack, new SimpleCallback<Object>() {
                                            @Override
                                            public void success(Object o) {
                                                progressBar.setVisibility(View.GONE);
                                                targetTrack.setLoved(true);
                                                MainActivity.this.invalidateMenu();
                                            }

                                            @Override
                                            public void failure(String error) {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                        break;
                                    case 1:
                                        addToVk(targetTrack);
                                        break;
                                }
                            }
                        }
                );
                AlertDialog chooseAddTargetDialog = builder.create();
                chooseAddTargetDialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    private ListView getListView() {
        ListView listView;
        if (isTablet()) {
            listView = tabletFragment.getPlaylistListView();
        } else {
            listView = phoneFragment.getPlaylistListView();
        }
        return listView;
    }

    public void updateView(List<Integer> integers) {
        ListView list = getListView();
        for (Integer position : integers) {
            int start = list.getFirstVisiblePosition();
            for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++)
                if (position == i) {
                    View view = list.getChildAt(i - start);
                    list.getAdapter().getView(i, view, list);
                    break;
                }
        }
    }

    private void stopMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        try {
            if (serviceConnection != null) {
                unbindService(serviceConnection);
            }
        } catch (IllegalArgumentException ignored) {
        }
        stopService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
//            if (getSlidingMenu() != null) {
//                getSlidingMenu().showContent();
//            }
            if (musicService == null) {
                activityResult = new ActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == Constants.MAIN_REQUEST_CODE) {
                setStartActions(data);
            } else if (requestCode == 7463) {
                long aid = data.getLongExtra("aid", -1);
                long oid = data.getLongExtra("oid", -1);
                if (aid == -1 || oid == -1) {
                    musicService.nextUrl(data.getIntExtra("position", 0));
                }
            }
        }
    }

    public void showRenameDialog(final Track track, final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.rename_dialog_layout);
        dialog.setTitle(R.string.rename_track);

        final EditText artistEditText = (EditText) dialog.findViewById(R.id.artist);
        artistEditText.setText(track.getArtist());
        final EditText titleEditText = (EditText) dialog.findViewById(R.id.title);
        titleEditText.setText(track.getTitle());
        Button okButton = (Button) dialog.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                String artist = artistEditText.getText().toString();
                if (title.length() == 0 || artist.length() == 0) {
                    Toast.makeText(MainActivity.this,
                            R.string.couldnt_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                track.setTitle(title);
                track.setArtist(artist);
                updateView(Arrays.asList(position));
                List<Track> playlistTracks = Timeline.getInstance().getPlaylistTracks();
                if (playlistTracks.size() > 0) {
                    PlaylistManager.getInstance().saveUnsavedPlaylist(playlistTracks);
                }
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void destroy() {
        stopMusicService();
        finish();
    }

    @Override
    public boolean onKeyUp(int keycode, KeyEvent e) {
        if (isTablet()) {
            switch (keycode) {
                case KeyEvent.KEYCODE_MENU: {
                    mainMenu.performIdentifierAction(R.id.more_button, 0);
                }
                return true;
                case KeyEvent.KEYCODE_BACK: {
                    onBackPressed();
                }
                return true;
                default:
                    return true;
            }
        } else {
            switch (keycode) {
                case KeyEvent.KEYCODE_MENU: {
                    switch (phoneFragment.getCurrentItem()) {
                        case 0:
                            mainMenu.performIdentifierAction(R.id.root_menu, 0);
                            break;
                        case 1:
                            mainMenu.performIdentifierAction(R.id.more_button, 0);
                            break;
                        case 2:
                            mainMenu.performIdentifierAction(R.id.more, 0);
                            break;
                        default:
                            break;
                    }
                }
                return true;
            }
        }

        return super.onKeyUp(keycode, e);
    }

    public void openRadiomix() {
        progressBar.setVisibility(View.VISIBLE);
//        if (isTablet()) showContent();
        new LastfmLibraryModel().getRadiomix(AuthorizationInfoManager.getLastfmName(),
                new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> tracks) {
                        int positionToPlay = 0;
                        List<Track> trackList = Converter.convertLastfmTrackList(tracks);
                        if (!isTablet()) phoneFragment.changeViewPagerItem(0);
                        Timeline.getInstance().setPlaylist(new Playlist(trackList));
                        updateAdapter();
                        changePlaylist(positionToPlay, true);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(MainActivity.this, errorMessage);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void openLibrary() {
        progressBar.setVisibility(View.VISIBLE);
//        if (isTablet()) showContent();
        new LastfmLibraryModel().getLibrary(AuthorizationInfoManager.getLastfmName(),
                new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> tracks) {
                        int positionToPlay = 0;
                        List<Track> trackList = Converter.convertLastfmTrackList(tracks);
                        if (!isTablet()) phoneFragment.changeViewPagerItem(0);
                        Timeline.getInstance().setPlaylist(new Playlist(trackList));
                        updateAdapter();
                        changePlaylist(positionToPlay, true);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        ErrorNotifier.showError(MainActivity.this, errorMessage);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public PlaylistItemsAdapter getPlaylistItemsAdapter() {
        return playlistItemsAdapter;
    }

    public MusicService getMusicPlaybackService() {
        return musicService;
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public ModeAdapter getModeAdapter() {
        return modeAdapter;
    }

    public PlaybackControlFragment getPlaybackControlFragment() {
        return playbackControlFragment;
    }

    public ModeListFragment getModeListFragment() {
        return modeListFragment;
    }

    public void playPause() {
        startService(new Intent(MainActivity.this, MusicService.class)
                .setAction(MusicService.ACTION_PLAY_PAUSE));
    }

    public void invalidateMenu() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                supportInvalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}
