package com.pillowapps.liqear.activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.michaelnovakjr.numberpicker.NumberPicker;
import com.pillowapps.liqear.BuildConfig;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.base.TrackedActivity;
import com.pillowapps.liqear.activities.modes.VkAudioSearchActivity;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.adapters.ModeGridAdapter;
import com.pillowapps.liqear.adapters.ModeListAdapter;
import com.pillowapps.liqear.adapters.PlaylistItemsAdapter;
import com.pillowapps.liqear.audio.MusicService;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.components.ActivityResult;
import com.pillowapps.liqear.components.ArtistTrackComparator;
import com.pillowapps.liqear.components.HintMaterialEditText;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
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
import com.pillowapps.liqear.helpers.LBPreferencesManager;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.LyricsModel;
import com.pillowapps.liqear.models.PlayingState;
import com.pillowapps.liqear.models.PlaylistModel;
import com.pillowapps.liqear.models.ShareModel;
import com.pillowapps.liqear.models.TrackModel;
import com.pillowapps.liqear.models.VideoModel;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.AppRateTheme;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class MainActivity extends TrackedActivity {

    private ModeGridAdapter modeAdapter;
    private ProgressBar progressBar;
    private Menu mainMenu;

    /**
     * Phone
     */
    private PhoneFragment phoneFragment;

    /**
     * Tablet
     */
    private TabletFragment tabletFragment;
    private DrawerLayout drawerLayout;
    private ModeListFragment modeListFragment;
    private PlaybackControlFragment playbackControlFragment;
    private ActionBarDrawerToggle toggle;

    /**
     * Music Service
     */
    private ServiceConnection serviceConnection;
    private MusicService musicService;
    private ProgressDialog serviceConnectionProgressDialog;

    /**
     * App Restoring
     */
    private ActivityResult activityResult;
    private PlaylistItemsAdapter playlistItemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                .delay(1000)
                .retryPolicy(RetryPolicy.EXPONENTIAL)
                .checkAndShow();

        boolean tabletMode = findViewById(R.id.tablet_layout) != null;
        if (tabletMode) {
            initTabletLayout();
        } else {
            initPhoneLayout();
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        startMusicService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateMenu();
        if (Timeline.getInstance().isPlaylistChanged()) {
            updateAdapter();
            changePlaylistWithoutTrackChange();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (serviceConnectionProgressDialog != null) {
            serviceConnectionProgressDialog.dismiss();
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
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                    return;
                }
            }
        }
        if (fixed) return;
        if (SharedPreferencesManager.getPreferences().getBoolean("exit_anyway", false)
                || Timeline.getInstance().getPlayingState() == PlayingState.DEFAULT) {
            destroy();
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mainMenu = menu;
        if (isTablet()) {
            int menuLayout = R.menu.tablet_menu_no_track;
            if (Timeline.getInstance().getCurrentTrack() != null) {
                menuLayout = R.menu.tablet_menu;
            }
            inflater.inflate(menuLayout, menu);
        }
        return super.onCreateOptionsMenu(menu);
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
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, ImagePagerActivity.class);
                intent.putExtra(ImagePagerActivity.ARTIST, currentTrack.getArtist());
                startActivity(intent);
            }
            return true;
            case R.id.show_artist_button: {
                if (currentTrack == null || currentTrack.getArtist() == null) return true;
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, LastfmArtistViewerActivity.class);
                intent.putExtra(LastfmArtistViewerActivity.ARTIST, currentTrack.getArtist());
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
            return true;
            case R.id.share_track_button: {
                if (currentTrack == null) return true;
                new ShareModel().showShareCurrentTrackDialog(MainActivity.this);
            }
            return true;
            case R.id.next_url_button: {
                if (currentTrack == null) return true;
                if (Timeline.getInstance().getCurrentTrack() != null
                        && Timeline.getInstance().getCurrentTrack().isLocal()) {
                    toast(R.string.track_local);
                    return true;
                }
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    toast(R.string.vk_not_authorized);
                    return true;
                }
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, VkAudioSearchActivity.class);
                intent.putExtra(Constants.TARGET, TrackUtils.getNotation(currentTrack));
                intent.putExtra(Constants.TYPE, 2);
                startActivityForResult(intent, Constants.VK_ADD_REQUEST_CODE);
            }
            return true;
            case R.id.lyrics_button: {
                if (currentTrack == null) return true;
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    toast(R.string.vk_not_authorized);
                    return true;
                }
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                new LyricsModel().openLyrics(MainActivity.this, currentTrack);
            }
            return true;
            case R.id.youtube_video_button: {
                if (currentTrack == null) return true;
                musicService.pause();
                new VideoModel().openVideo(MainActivity.this, currentTrack);
            }
            return true;
            case R.id.add_to_vk_button: {
                if (currentTrack == null) return true;
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    toast(R.string.vk_not_authorized);
                    return true;
                }
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                addToVk(currentTrack);
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
                int timerDefault = SharedPreferencesManager.getPreferences()
                        .getInt(Constants.TIMER_DEFAULT, 10);
                sb.setCurrent(timerDefault);

                final MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(R.string.timer)
                        .customView(layout, true)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .build();

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        musicService.setTimer(sb.getCurrent() * 60);
                        SharedPreferences.Editor editor =
                                SharedPreferencesManager.getPreferences().edit();
                        editor.putInt(Constants.TIMER_DEFAULT, sb.getCurrent());
                        editor.apply();
                    }
                });
                dialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        musicService.setTimer(0);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
            return true;
            case R.id.exit_button: {
                destroy();
            }
            return true;
            case R.id.playlists_button: {
                Intent intent = new Intent(MainActivity.this, PlaylistsActivity.class);
                intent.putExtra(PlaylistsActivity.AIM, PlaylistsActivity.Aim.SHOW_PLAYLISTS);
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
            return true;
            case R.id.clean_titles: {
                List<Track> playlistTracks = Timeline.getInstance().getPlaylistTracks();
                new TrackModel().clearTitles(playlistTracks);
                playlistItemsAdapter.notifyDataSetChanged();
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
//                    if (!landscapeTablet) showMenu();todo
                    modeListFragment.getAdapter().notifyChanges();
                }
            }
            return true;
            case R.id.menu_search: {
                SharedPreferences savePreferences = SharedPreferencesManager.getSavePreferences();
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        int position = info.position;
        final Track track = playlistItemsAdapter.getItem(position);
        switch (item.getItemId()) {
            case R.id.add_to_playlist_track_menu_item:
                Intent intent = new Intent(MainActivity.this, PlaylistsActivity.class);
                intent.putExtra(PlaylistsActivity.AIM,
                        PlaylistsActivity.Aim.ADD_TO_PLAYLIST);
                intent.putExtra(Constants.ARTIST, track.getArtist());
                intent.putExtra(Constants.TITLE, track.getTitle());
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                break;
            case R.id.lyrics_track_menu_item:
                new LyricsModel().openLyrics(MainActivity.this, track);
                break;
            case R.id.add_to_queue_track_menu_item:
                Timeline.getInstance().queueTrack(position);
                updateView(Arrays.asList(position));
                break;
            case R.id.show_artist_track_menu_item:
                if (!NetworkUtils.isOnline()) {
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
            case R.id.remove_from_list_track_menu_item:
                removeTrack(position);
                break;
            case R.id.add_to_track_menu_item:
                final Track targetTrack = Timeline.getInstance().getPlaylistTracks().get(position);

                final MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(R.string.choose_target)
                        .items(R.array.add_targets)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                                switch (which) {
                                    case 0:
                                        new LastfmTrackModel().love(targetTrack, new SimpleCallback<Object>() {
                                            @Override
                                            public void success(Object o) {
                                                progressBar.setVisibility(View.GONE);
                                                targetTrack.setLoved(true);
                                                updateLoveButton();
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
                        })
                        .build();
                dialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keycode, @NonNull KeyEvent event) {
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

        return super.onKeyUp(keycode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
//            if (getSlidingMenu() != null) {
//                getSlidingMenu().showContent(); todo
//            }
            if (musicService == null) {
                activityResult = new ActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == Constants.MAIN_REQUEST_CODE) {
                setStartActions(data);
            } else if (requestCode == Constants.VK_ADD_REQUEST_CODE) { //todo somewhere else
                long aid = data.getLongExtra("aid", -1);
                long oid = data.getLongExtra("oid", -1);
                if (aid == -1 || oid == -1) {
                    musicService.changeUrl(data.getIntExtra("position", 0));
                }
            }
        }
    }

    public void destroy() {
        stopMusicService();
        finish();
    }

    private void invalidateMenu() {
        if (phoneFragment != null) {
            phoneFragment.updatePlaybackToolbar();
        } else {
            MainActivity.this.supportInvalidateOptionsMenu();
        }
    }

    private void initPhoneLayout() {
        phoneFragment = (PhoneFragment) getSupportFragmentManager()
                .findFragmentById(R.id.handset_fragment);
    }

    private void initTabletLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        playbackControlFragment = (PlaybackControlFragment) getSupportFragmentManager()
                .findFragmentById(R.id.playback_controls);

        modeListFragment = (ModeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);

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

            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setDisplayHomeAsUpEnabled(true);
                supportActionBar.setHomeButtonEnabled(true);
            }
            toggle.syncState();
        }

        tabletFragment = (TabletFragment)
                getSupportFragmentManager().findFragmentById(R.id.tablet_fragment);
        tabletFragment.init();
    }

    public void init() {
        modeAdapter = new ModeGridAdapter(MainActivity.this);
    }

    private OnRecyclerItemClickListener getPlaylistItemsClickListener() {
        if (!isTablet()) {
            if (phoneFragment != null) {
                return phoneFragment.getPlaylistItemClickListener();
            }
        }
        return null;
    }

    private void startMusicService() {
        Timeline.getInstance().clearQueue();
        serviceConnectionProgressDialog = null;
        try {
            serviceConnectionProgressDialog = ProgressDialog.show(MainActivity.this, null,
                    getString(R.string.wait), true);
        } catch (Exception e) {
            // No operations.
        }

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (serviceConnectionProgressDialog != null
                                && serviceConnectionProgressDialog.isShowing())
                            serviceConnectionProgressDialog.dismiss();
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

    private void stopMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        try {
            if (serviceConnection != null) {
                unbindService(serviceConnection);
            }
        } catch (IllegalArgumentException ignored) {
            // No operations.
        }
        stopService(intent);
    }

    public void restorePreviousState() {
        updateAdapter();
        Timeline.getInstance().updateRealTrackPositions();
    }

    private boolean isTablet() {
        return phoneFragment == null && tabletFragment != null;
    }

    private void changePlaylist() {
        changePlaylist(0, true);
    }

    public void updateAdapter() {
        List<Track> playlist = Timeline.getInstance().getPlaylistTracks();
        playlistItemsAdapter.setValues(playlist);
        if (!isTablet()) {
            phoneFragment.updateEmptyTextView();
        }
    }

    private void changePlaylist(int position, boolean play) {
        Timeline.getInstance().clearQueue();
        Timeline.getInstance().updateRealTrackPositions();
        Timeline.getInstance().clearPreviousIndexes();
        if (musicService != null) {
            musicService.pause();
        }
        List<Track> tracks = Timeline.getInstance().getPlaylistTracks();
        if (tracks.size() > 0) {
            if (play) {
                if (isTablet()) {
                    tabletFragment.playTrack(position);
                } else {
                    phoneFragment.playTrack(position);
                }
            }
            new PlaylistModel().saveMainPlaylist();
        }
        if (!isTablet()) {
            phoneFragment.updateMainPlaylistTitle();
            phoneFragment.changeViewPagerItem(0);
        }
    }

    public void changePlaylistWithoutTrackChange() {
        Timeline.getInstance().clearQueue();
        Timeline.getInstance().updateRealTrackPositions();

        Timeline.getInstance().clearPreviousIndexes();
        if (Timeline.getInstance().getPlaylistTracks().size() > 0) {
            new PlaylistModel().saveMainPlaylist();
        }
//        AudioTimeline.setPlaylistChanged(false);
    }

    private void updateSearchVisibility() {
        if (isTablet()) {
            tabletFragment.updateSearchVisibility();
        } else {
            phoneFragment.updateSearchVisibility();
        }
    }

    public void removeTrack(int position) {
        int index = Timeline.getInstance().getIndex();
        if (position < index) {
            Timeline.getInstance().setIndex(index - 1);
        } else if (position == index) {
            Timeline.getInstance().setPlaylistChanged(true);
        }
        Timeline.getInstance().getPlaylistTracks().remove(position);
        updateAdapter();
        changePlaylistWithoutTrackChange();
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
        Timeline.getInstance().setPlaylistChanged(true);
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

    public void playPause() {
        startService(new Intent(MainActivity.this, MusicService.class)
                .setAction(MusicService.ACTION_PLAY_PAUSE));
    }

    public void updateView(List<Integer> playlistItemsIndexesToUpdate) {
//        UltimateRecyclerView list = getListView();
//        for (Integer position : playlistItemsIndexesToUpdate) {
//            int start = list.getFirstVisiblePosition();
//            for (int i = start, j = list.getLastVisiblePosition(); i <= j; i++)
//                if (position == i) {
//                    View view = list.getChildAt(i - start);
//                    list.getAdapter().getView(i, view, list);
//                    break;
//                }
//        } todo
    }

    public void showRenameDialog(final Track track, final int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.rename_track)
                .customView(R.layout.rename_dialog_layout, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();

        View customView = dialog.getCustomView();
        if (customView == null) return;
        final HintMaterialEditText artistEditText = (HintMaterialEditText) customView.findViewById(R.id.artist);
        artistEditText.setText(track.getArtist());
        artistEditText.updateHint(getString(R.string.artist));
        final HintMaterialEditText titleEditText = (HintMaterialEditText) customView.findViewById(R.id.title);
        titleEditText.setText(track.getTitle());
        titleEditText.updateHint(getString(R.string.title));

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    new PlaylistModel().saveMainPlaylist();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void openDropButton() {
        mainMenu.performIdentifierAction(R.id.track_button, 0);
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

    public void toggleLoveCurrentTrack() {
        if (!AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            toast(R.string.last_fm_not_authorized);
            return;
        }
        if (!NetworkUtils.isOnline()) {
            toast(R.string.no_internet);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        final Track track = Timeline.getInstance().getCurrentTrack();
        if (!track.isLoved()) {
            new LastfmTrackModel().love(track, new SimpleCallback<Object>() {
                @Override
                public void success(Object data) {
                    progressBar.setVisibility(View.GONE);
                    track.setLoved(true);
                    updateLoveButton();
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
                    updateLoveButton();
                }

                @Override
                public void failure(String error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void updateLoveButton() {
        if (isTablet()) {
            tabletFragment.updateLoveButton();
        } else {
            phoneFragment.updateLoveButton();
        }
    }

    private void addToVk(Track track) {
        if (LBPreferencesManager.isVkAddSlow()) {
            if (track == null) return;
            Intent intent = new Intent(MainActivity.this, VkAudioSearchActivity.class);
            intent.putExtra(Constants.TARGET, TrackUtils.getNotation(track));
            startActivity(intent);
        } else {
            new VkAudioModel().addToUserAudioFast(TrackUtils.getNotation(track), new VkSimpleCallback<VkResponse>() {
                @Override
                public void success(VkResponse data) {
                    toast(R.string.added);
                }

                @Override
                public void failure(VkError error) {

                }
            });
        }
    }

    public void setMainMenu(Menu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public PlaylistItemsAdapter getPlaylistItemsAdapter() {
        return playlistItemsAdapter;
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public PlaybackControlFragment getPlaybackControlFragment() {
        return playbackControlFragment;
    }

    public ModeGridAdapter getModeAdapter() {
        return modeAdapter;
    }

    public void setPlaylistItemsAdapter(PlaylistItemsAdapter playlistItemsAdapter) {
        this.playlistItemsAdapter = playlistItemsAdapter;
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
}
