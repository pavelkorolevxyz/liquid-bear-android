package com.pillowapps.liqear.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.michaelnovakjr.numberpicker.NumberPicker;
import com.pillowapps.liqear.LiqearApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.audio.MusicPlaybackService;
import com.pillowapps.liqear.components.ActivityResult;
import com.pillowapps.liqear.components.ArtistTrackComparator;
import com.pillowapps.liqear.adapter.ModeListAdapter;
import com.pillowapps.liqear.network.GetResponseCallback;
import com.pillowapps.liqear.network.LastfmRequestManager;
import com.pillowapps.liqear.network.QueryManager;
import com.pillowapps.liqear.network.ReadyResult;
import com.pillowapps.liqear.network.VkRequestManager;
import com.pillowapps.liqear.network.VkSimpleCallback;
import com.pillowapps.liqear.fragments.HandsetFragment;
import com.pillowapps.liqear.fragments.ModeListFragment;
import com.pillowapps.liqear.fragments.PlaybackControlFragment;
import com.pillowapps.liqear.fragments.RightFragment;
import com.pillowapps.liqear.fragments.TabletFragment;
import com.pillowapps.liqear.global.Config;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.adapter.MainActivityAdapter;
import com.pillowapps.liqear.adapter.ModeAdapter;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.adapter.PlaylistItemsAdapter;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.MainActivityStartEnum;
import com.pillowapps.liqear.models.Track;
import com.pillowapps.liqear.models.vk.VkError;
import com.pillowapps.liqear.models.vk.VkResponse;

import org.codechimp.apprater.AppRater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends SlidingFragmentActivity {
    public Menu mainMenu;
    private HandsetFragment handsetFragment;
    private ServiceConnection serviceConnection;
    private MusicPlaybackService musicPlaybackService;
    private ActivityResult activityResult;
    private PlaylistItemsAdapter playlistItemsAdapter;
    private ProgressBar progressBar;
    private TabletFragment tabletFragment;
    private PlaybackControlFragment playbackControlFragment;
    private ModeAdapter modeAdapter;
    private ModeListFragment modeListFragment;
    private RightFragment rightFragment;
    private boolean landscapeTablet = false;
    private ProgressDialog finalProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureInitialSettings();
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR);
        if ((!AuthorizationInfoManager.isAuthorizedOnVk()
                || !AuthorizationInfoManager.isAuthorizedOnLastfm())
                && !AuthorizationInfoManager.isAuthSkipped()) {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.putExtra(Constants.FIRST_START, true);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayShowTitleEnabled(true);

        AppRater.app_launched(this);

        if (findViewById(R.id.tablet_layout) != null) {
            playlistItemsAdapter = new PlaylistItemsAdapter(MainActivity.this);
            if (findViewById(R.id.menu_frame) == null) {
                landscapeTablet = false;
                setBehindContentView(R.layout.menu_frame);
                getSlidingMenu().setSlidingEnabled(true);
                getSlidingMenu().setMode(SlidingMenu.LEFT);
                getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                View v = new View(this);
                setBehindContentView(v);
                getSlidingMenu().setSlidingEnabled(false);
                getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                landscapeTablet = true;
            }

            playbackControlFragment = new PlaybackControlFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.playback_controls_frame, playbackControlFragment)
                    .commit();

            if (tabletFragment == null) {
                tabletFragment = new TabletFragment();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, tabletFragment)
                    .commit();

            modeListFragment = new ModeListFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.menu_frame, modeListFragment)
                    .commit();

            SlidingMenu sm = getSlidingMenu();
            sm.setShadowWidthRes(R.dimen.shadow_width);
            sm.setBehindWidthRes(R.dimen.behind_width);
            setSlidingActionBarEnabled(false);
            sm.setShadowDrawable(R.drawable.shadow);
            sm.setBehindScrollScale(0.25f);
            sm.setFadeDegree(0.25f);
        } else {
            handsetFragment = (HandsetFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.handset_fragment);
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                int navBarHeight = getNavigationBarHeight();
                findViewById(R.id.main_layout).setPadding(0, 0, 0, navBarHeight);
                View menuFrame = findViewById(R.id.menu_frame);
                if (menuFrame != null) {
                    menuFrame.setPadding(0, 0, 0, navBarHeight);
                }
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int navBarWidth = getNavigationBarHeight();
                findViewById(R.id.main_layout).setPadding(0, 0, navBarWidth, 0);
                View menuFrame = findViewById(R.id.menu_frame);
                if (menuFrame != null) {
                    menuFrame.setPadding(0, 0, navBarWidth, 0);
                }
            }
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        startMusicService();

//        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onStart() {
        if (getResources().getBoolean(R.bool.analytics_enabled)) {
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
        if (isTablet()) showContent();
        super.onSaveInstanceState(outState);
    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();

        int resourceId = resources.getIdentifier(
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                        "navigation_bar_height" : "navigation_bar_width",
                "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public void init() {
        playlistItemsAdapter = new PlaylistItemsAdapter(MainActivity.this);
        modeAdapter = new ModeAdapter(MainActivity.this);
    }

    public void restorePreviousState() {
        updateAdapter();
        setRealPositions();
    }

    private boolean isTablet() {
        return handsetFragment == null && tabletFragment != null;
    }

    private void configureInitialSettings() {
        SharedPreferences preferences = PreferencesManager.getPreferences();
        try {
            preferences.edit().putInt("version", getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionCode).apply();
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        PlaylistManager.getInstance().updateDatabase();
        Config.resources = getResources();
        Config.tag = getResources().getString(R.string.app_name);
    }

    public void openDropButton() {
        mainMenu.performIdentifierAction(R.id.track_button, 0);
    }

    @Override
    protected void onResume() {
//        ViewServer.get(this).setFocusedWindow(this);
        super.onResume();
        invalidateOptionsMenu();
        if (AudioTimeline.isPlaylistChanged()) {
            updateAdapter();
            changePlaylistWithoutTrackChange();
        }
    }

    public void changePlaylistWithoutTrackChange() {
        AudioTimeline.getQueue().clear();
        setRealPositions();
        AudioTimeline.clearPreviousList();
        if (AudioTimeline.getPlaylist().size() > 0) {
            PlaylistManager.getInstance().saveUnsavedPlaylist(AudioTimeline.getPlaylist());
        }
        AudioTimeline.setPlaylist(AudioTimeline.getPlaylist());
        AudioTimeline.setPlaylistChanged(false);
    }

    public void setRealPositions() {
        List<Track> playlist = AudioTimeline.getPlaylist();
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
                if (getSlidingMenu().isMenuShowing()) {
                    getSlidingMenu().showContent(true);
                    return;
                }
            }
        }
        if (fixed) return;
        if (PreferencesManager.getPreferences().getBoolean("exit_anyway", false)
                || !AudioTimeline.isStateActive()) {
            destroy();
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onDestroy() {
//        ViewServer.get(this).removeWindow(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Track currentTrack = AudioTimeline.getCurrentTrack();
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
                if (getSlidingMenu() != null) toggle();
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
                Intent intent = new Intent(MainActivity.this, ArtistViewerActivity.class);
                intent.putExtra(ArtistViewerActivity.ARTIST, currentTrack.getArtist());
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
                if (AudioTimeline.getCurrentTrack() != null && AudioTimeline.getCurrentTrack().isLocal()) {
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
                Intent intent = new Intent(MainActivity.this, SearchSherlockListActivity.class);
                intent.putExtra(Constants.TARGET, currentTrack.getNotation());
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.AUDIO_SEARCH_RESULT);
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
                if (currentTrack != null) {
                    addToVk(currentTrack);
                }
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
                final Track track = AudioTimeline.getCurrentTrack();
                if (!track.isLoved()) {
                    LastfmRequestManager.getInstance().love(track, new Callback<Object>() {
                        @Override
                        public void success(Object o, Response response) {
                            progressBar.setVisibility(View.GONE);
                            track.setLoved(true);
                            MainActivity.this.invalidateMenu();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    LastfmRequestManager.getInstance().unlove(track, new Callback<Object>() {
                        @Override
                        public void success(Object o, Response response) {
                            progressBar.setVisibility(View.GONE);
                            track.setLoved(false);
                            MainActivity.this.invalidateMenu();
                        }

                        @Override
                        public void failure(RetrofitError error) {
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
                if (musicPlaybackService == null) {
                    Toast.makeText(MainActivity.this, R.string.service_not_connected,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, EqualizerSherlockActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.donate_button: {
                Intent myIntent = new Intent(MainActivity.this, DonateSherlockActivity.class);
                startActivity(myIntent);
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
                                musicPlaybackService.setTimer(0);
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                musicPlaybackService.setTimer(sb.getCurrent() * 60);
                                SharedPreferences.Editor editor =
                                        PreferencesManager.getPreferences().edit();
                                editor.putInt(Constants.TIMER_DEFAULT, sb.getCurrent());
                                editor.commit();
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
                Intent intent = new Intent(MainActivity.this, PlaylistsSherlockListActivity.class);
                intent.putExtra(PlaylistsSherlockListActivity.AIM,
                        PlaylistsSherlockListActivity.Aim.SHOW_PLAYLISTS);
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
                    if (!landscapeTablet) showMenu();
                    modeListFragment.getAdapter().notifyChanges();
                }
            }
            return true;
            case R.id.menu_search: {
                SharedPreferences savePreferences = PreferencesManager.getSavePreferences();
                boolean visibility = !savePreferences.getBoolean(
                        Constants.SEARCH_PLAYLIST_VISIBILITY, false);
                savePreferences.edit().putBoolean(
                        Constants.SEARCH_PLAYLIST_VISIBILITY, visibility).commit();
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
            handsetFragment.updateSearchVisibility();
        }
    }

    private void showShareDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.share_dialog_layout);
        dialog.setTitle(R.string.share_track);
        final Track currentTrack = AudioTimeline.getCurrentTrack();
        ImageButton vkButton = (ImageButton) dialog.findViewById(R.id.vk_button);
        Button otherButton = (Button) dialog.findViewById(R.id.other_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        String template = PreferencesManager.getPreferences().getString(Constants.SHARE_FORMAT, getString(R.string.listening_now));
        final Album album = AudioTimeline.getAlbum();
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
                shareTrackVk(currentTrack, shareBody, imageUrl);
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

    private void shareTrackVk(final Track track, final String message, final String photo) {
        QueryManager.getInstance().postVkUserWall(message, photo, track, new GetResponseCallback() {
            @Override
            public void onDataReceived(ReadyResult result) {
                // No operations.
            }
        });
    }

    public void removeTrack(int position) {
        if (position < AudioTimeline.getCurrentIndex()) {
            AudioTimeline.setCurrentIndex(AudioTimeline.getCurrentIndex() - 1);
        } else if (position == AudioTimeline.getCurrentIndex()) {
            AudioTimeline.setStillLastPlaylist(true);
        }
        AudioTimeline.getPlaylist().remove(position);
        updateAdapter();
        changePlaylistWithoutTrackChange();
    }

    private void changePlaylist() {
        changePlaylist(0, true);
    }

    public void updateAdapter() {
        List<Track> playlist = AudioTimeline.getPlaylist();
        playlistItemsAdapter.setValues(playlist);
    }

    private void changePlaylist(int position, boolean play) {
        AudioTimeline.getQueue().clear();
        setRealPositions();
        AudioTimeline.clearPreviousList();
        if (musicPlaybackService != null) {
            musicPlaybackService.pause(true);
        }
        if (AudioTimeline.getPlaylist().size() > 0) {
            if (play) {
                getListView().performItemClick(getPlaylistItemsAdapter().getView(position, null, null), position, position);
            }
            PlaylistManager.getInstance().saveUnsavedPlaylist(AudioTimeline.getPlaylist());
        }
        if (!isTablet()) handsetFragment.changeViewPagerItem(0);
    }

    private void sortByArtist() {
        if (playlistItemsAdapter == null) return;
        List<Track> tracks = new ArrayList<Track>(playlistItemsAdapter.getValues());
        Collections.sort(tracks, new ArtistTrackComparator());
        AudioTimeline.setPlaylist(tracks);
        updateAdapter();
        AudioTimeline.setStillLastPlaylist(true);
        changePlaylistWithoutTrackChange();
    }

    private void findCurrentTrack() {
        int currentIndex = AudioTimeline.getCurrentIndex();
        if (currentIndex >= 0 && currentIndex < AudioTimeline.getPlaylistSize()) {
            getListView().setSelection(currentIndex);
        }
    }

    private void shufflePlaylist() {
        if (playlistItemsAdapter == null) return;
        List<Track> tracks = new ArrayList<Track>(playlistItemsAdapter.getValues());
        Collections.shuffle(tracks);
        AudioTimeline.setPlaylist(tracks);
        updateAdapter();
        AudioTimeline.setStillLastPlaylist(true);
        changePlaylistWithoutTrackChange();
    }

    private void fixateSearchResult() {
        if (playlistItemsAdapter == null) return;
        AudioTimeline.setPlaylist(new ArrayList<Track>(playlistItemsAdapter.getValues()));
        updateAdapter();
        clearSearch();
        AudioTimeline.clearPreviousList();
        changePlaylist();
    }

    private void clearSearch() {
        if (isTablet()) {
            tabletFragment.clearFilter();
        } else {
            handsetFragment.clearFilter();
        }
    }

    private void setStartActions(Intent data) {
        MainActivityStartEnum mainActivityStartEnum = (MainActivityStartEnum) data.getSerializableExtra(Constants.ACTION_ENUM);
        if (mainActivityStartEnum != null) {
            switch (mainActivityStartEnum) {
                case PLAY_TRACKS: {
                    if (playlistItemsAdapter.isEditMode()) playlistItemsAdapter.setEditMode(false);
                    int positionToPlay = data.getIntExtra(Constants.POSITION_TO_PLAY, 0);
                    if (!isTablet()) handsetFragment.changeViewPagerItem(0);
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
        musicPlaybackService.pause(true);
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
        List<Track> playlist = AudioTimeline.getPlaylist();
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
                Intent intent = new Intent(MainActivity.this, SearchSherlockListActivity.class);
                intent.putExtra(SearchSherlockListActivity.SEARCH_MODE,
                        SearchSherlockListActivity.SearchMode.AUDIO_SEARCH_RESULT_ADD_VK);
                intent.putExtra(Constants.TARGET, track.getNotation());
                startActivity(intent);
            }
        } else {
            VkRequestManager.getInstance().addToUserAudioFast(track.getNotation(), new VkSimpleCallback<VkResponse>() {
                @Override
                public void success(VkResponse data) {
                    Toast.makeText(LiqearApplication.getAppContext(),
                            R.string.added, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(VkError error) {

                }
            });
        }
    }

    private void openLyrics(Track currentTrack) {
        Intent intent = new Intent(MainActivity.this, TextSherlockActivity.class);
        intent.putExtra("artist", currentTrack.getArtist());
        intent.putExtra("title", currentTrack.getTitle());
        intent.putExtra(TextSherlockActivity.TEXT_AIM, TextSherlockActivity.Aim.LYRICS);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mainMenu = menu;
        if (isTablet()) {
            int menuLayout = R.menu.tablet_menu_no_track;
            if (AudioTimeline.hasCurrentTrack()) {
                menuLayout = R.menu.tablet_menu;
                if (AudioTimeline.getCurrentTrack().isLoved()) {
                    menuLayout = R.menu.tablet_menu_loved;
                }
            }
            inflater.inflate(menuLayout, menu);
        } else {
            switch (handsetFragment.getCurrentItem()) {
                case MainActivityAdapter.PLAY_TAB_INDEX: {
                    int menuLayout = R.menu.menu_play_tab_no_current_track;
                    if (AudioTimeline.hasCurrentTrack()) {
                        menuLayout = R.menu.menu_play_tab;
                        if (AudioTimeline.getCurrentTrack().isLoved()) {
                            menuLayout = R.menu.menu_play_tab_loved;
                        }
                    }
                    inflater.inflate(menuLayout, menu);
                }
                break;
                case MainActivityAdapter.PLAYLIST_TAB_INDEX:
                    inflater.inflate(R.menu.menu_playlist_tab, menu);
                    break;
                case MainActivityAdapter.MODE_TAB_INDEX:
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
        AudioTimeline.setQueue(new LinkedList<Integer>());
        ProgressDialog progress = null;
        try {
            progress = ProgressDialog.show(MainActivity.this, null, getString(R.string.wait), true);
        } catch (Exception e) {
            // No operations.
        }

        finalProgress = progress;
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                MusicPlaybackService.LocalBinder binder =
                        (MusicPlaybackService.LocalBinder) service;
                musicPlaybackService = binder.getService();
                if (isTablet()) {
                    tabletFragment.setServiceConnected();
                } else {
                    handsetFragment.setServiceConnected();
                }
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                if (AudioTimeline.isStateActive()) musicPlaybackService.showTrackInNotification();
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
        Intent intent = new Intent(LiqearApplication.getAppContext(), MusicPlaybackService.class);
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
                Intent intent = new Intent(MainActivity.this, PlaylistsSherlockListActivity.class);
                intent.putExtra(PlaylistsSherlockListActivity.AIM,
                        PlaylistsSherlockListActivity.Aim.ADD_TO_PLAYLIST);
                intent.putExtra(Constants.ARTIST, track.getArtist());
                intent.putExtra(Constants.TITLE, track.getTitle());
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                break;
            case 1:
                openLyrics(track);
                break;
            case 2:
                AudioTimeline.getQueue().add(info.position);
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
                            ArtistViewerActivity.class);
                    artistInfoIntent.putExtra(ArtistViewerActivity.ARTIST, track.getArtist());
                    startActivityForResult(artistInfoIntent, Constants.MAIN_REQUEST_CODE);
                }
                break;
            case 4: //Remove from playlist
                removeTrack(info.position);
                break;
            case 5: //Add to...
                final Track targetTrack = AudioTimeline.getTrack(info.position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.choose_target).setItems(R.array.add_targets,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        LastfmRequestManager.getInstance().love(targetTrack, new Callback<Object>() {
                                            @Override
                                            public void success(Object o, Response response) {
                                                progressBar.setVisibility(View.GONE);
                                                targetTrack.setLoved(true);
                                                MainActivity.this.invalidateMenu();
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
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
        ListView listView = null;
        if (isTablet()) {
            listView = tabletFragment.getPlaylistListView();
        } else {
            listView = handsetFragment.getPlaylistListView();
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
        Intent intent = new Intent(this, MusicPlaybackService.class);
        try {
            if (serviceConnection != null) {
                unbindService(serviceConnection);
            }
        } catch (IllegalArgumentException ignored) {
        }
        if (isTablet()) {
            tabletFragment.stopMusicService();
        } else {
            handsetFragment.stopMusicService();
        }
        stopService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (getSlidingMenu() != null) {
                getSlidingMenu().showContent();
            }
            if (musicPlaybackService == null) {
                activityResult = new ActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == Constants.MAIN_REQUEST_CODE) {
                setStartActions(data);
            } else if (requestCode == 7463) {
                long aid = data.getLongExtra("aid", -1);
                long oid = data.getLongExtra("oid", -1);
                if (aid == -1 || oid == -1) {
                    musicPlaybackService.nextUrl(data.getIntExtra("position", 0));
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
                if (AudioTimeline.getPlaylist().size() > 0) {
                    PlaylistManager.getInstance().saveUnsavedPlaylist(AudioTimeline.getPlaylist());
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
                    switch (handsetFragment.getCurrentItem()) {
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
        if (isTablet()) showContent();
        QueryManager.getInstance().getRadiomix(AuthorizationInfoManager.getLastfmName(),
                new GetResponseCallback() {
                    @Override
                    public void onDataReceived(ReadyResult result) {
                        int positionToPlay = 0;
                        List<Track> tracklist = (List<Track>) result.getObject();
                        if (!isTablet()) handsetFragment.changeViewPagerItem(0);
                        AudioTimeline.setPlaylist(tracklist);
                        updateAdapter();
                        changePlaylist(positionToPlay, true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void openLibrary() {
        progressBar.setVisibility(View.VISIBLE);
        if (isTablet()) showContent();
        QueryManager.getInstance().getLibraryTracks(AuthorizationInfoManager.getLastfmName(),
                new GetResponseCallback() {
                    @Override
                    public void onDataReceived(ReadyResult result) {
                        int positionToPlay = 0;
                        List<Track> tracklist = (List<Track>) result.getObject();
                        if (!isTablet()) handsetFragment.changeViewPagerItem(0);
                        AudioTimeline.setPlaylist(tracklist);
                        updateAdapter();
                        changePlaylist(positionToPlay, true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public PlaylistItemsAdapter getPlaylistItemsAdapter() {
        return playlistItemsAdapter;
    }

    public MusicPlaybackService getMusicPlaybackService() {
        return musicPlaybackService;
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

    public RightFragment getRightFragment() {
        return rightFragment;
    }

    public void playPause() {
        startService(new Intent(MainActivity.this, MusicPlaybackService.class)
                .setAction(MusicPlaybackService.ACTION_TOGGLE_PLAYBACK_NOTIFICATION));
    }

    public void invalidateMenu() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                supportInvalidateOptionsMenu();
            }
        });
    }

}
