package com.pillowapps.liqear.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.activities.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.adapters.ModeAdapter;
import com.pillowapps.liqear.adapters.PhoneFragmentAdapter;
import com.pillowapps.liqear.adapters.PlaylistItemsAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.audio.deprecated.AudioTimeline;
import com.pillowapps.liqear.audio.deprecated.MusicPlaybackService;
import com.pillowapps.liqear.components.ModeClickListener;
import com.pillowapps.liqear.components.SwipeDetector;
import com.pillowapps.liqear.components.ViewPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.network.ImageLoadingListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class PhoneFragment extends Fragment {
    private ServiceBroadcastReceiver receiver;
    private ViewPager pager;
    private View playlistTab;
    private View playbackTab;
    private View modeTab;
    private StickyGridHeadersGridView modeGridView;
    private DragSortListView playlistsListView;
    private TextView artistTextView;
    private TextView titleTextView;
    private ImageButton playPauseButton;
    private SeekBar seekBar;
    private TextView timeTextView;
    private TextView timeDurationTextView;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    private TextView timePlateTextView;
    private ImageView artistImageView;
    private ImageView albumImageView;
    private TextView albumTextView;
    private View blackView;
    private MainActivity mainActivity;
    private UnderlinePageIndicator indicator;
    private EditText searchPlaylistEditText;
    private ImageButton clearEditTextButton;
    private View tutorialLayout;
    private Animation tutorialBlinkAnimation;
    private ViewGroup backLayout;
    private ViewGroup bottomControlsLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.handset_fragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();
        initUi(v);
        initListeners();
        restorePreviousState();
        return v;
    }

    private void initViewPager(View v) {
        final List<ViewPage> pages = new ArrayList<>();
        playlistTab = View.inflate(mainActivity, R.layout.playlist_tab, null);
        playbackTab = View.inflate(mainActivity, R.layout.play_tab, null);
        modeTab = View.inflate(mainActivity, R.layout.mode_tab, null);
        pages.add(new ViewPage(mainActivity, playlistTab, R.string.playlist_tab));
        pages.add(new ViewPage(mainActivity, playbackTab, R.string.play_tab));
        pages.add(new ViewPage(mainActivity, modeTab, R.string.mode_tab));
        pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(pages.size());
        pager.setAdapter(new PhoneFragmentAdapter(pages));

        indicator = (UnderlinePageIndicator) v.findViewById(R.id.indicator);
        indicator.setSelectedColor(getResources().getColor(R.color.accent));
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int index) {
                mainActivity.invalidateOptionsMenu();
                if (index == PhoneFragmentAdapter.PLAYLIST_TAB_INDEX
                        || index == PhoneFragmentAdapter.MODE_TAB_INDEX) {
                    SharedPreferences startPreferences = PreferencesManager.getStartPreferences();
                    boolean tutorialEnabled = !startPreferences
                            .getBoolean(Constants.TUTORIAL_DISABLED, false);
                    if (tutorialEnabled && tutorialBlinkAnimation != null) {
                        tutorialBlinkAnimation.cancel();
                        tutorialLayout.setVisibility(View.GONE);
                    }
                    SharedPreferences.Editor editor = startPreferences.edit();
                    editor.putBoolean(Constants.TUTORIAL_DISABLED, true).apply();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initUi(View v) {
        initViewPager(v);
        changeViewPagerItem(PhoneFragmentAdapter.PLAY_TAB_INDEX);

        mainActivity.init();

        initModeTab();
        initPlaylistsTab();
        initPlaybackTab();

        boolean tutorialEnabled = !PreferencesManager.getStartPreferences()
                .getBoolean(Constants.TUTORIAL_DISABLED, false);
        if (tutorialEnabled) {
            showTutorial();
        }
    }

    private void initPlaylistsTab() {
        playlistsListView = (DragSortListView) playlistTab.findViewById(R.id.playlist_list_view_playlist_tab);
        playlistsListView.setAdapter(mainActivity.getPlaylistItemsAdapter());
        searchPlaylistEditText = (EditText) playlistTab.findViewById(R.id.search_edit_text_playlist_tab);
        searchPlaylistEditText.setVisibility(PreferencesManager.getSavePreferences()
                .getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false) ? View.VISIBLE : View.GONE);
        clearEditTextButton = (ImageButton) playlistTab.findViewById(R.id.clear_edit_text_button_playlist_tab);
    }

    private void initPlaybackTab() {
        artistTextView = (TextView) playbackTab.findViewById(R.id.artist_text_view_playback_tab);
        titleTextView = (TextView) playbackTab.findViewById(R.id.title_text_view_playback_tab);
        playPauseButton = (ImageButton) playbackTab.findViewById(R.id.play_pause_button_playback_tab);
        backLayout = (ViewGroup) playbackTab.findViewById(R.id.back_layout);
        bottomControlsLayout = (ViewGroup) playbackTab.findViewById(R.id.bottom_controls_layout);
        seekBar = (SeekBar) playbackTab.findViewById(R.id.seek_bar_playback_tab);
        timeTextView = (TextView) playbackTab.findViewById(R.id.time_text_view_playback_tab);
        timeDurationTextView = (TextView) playbackTab.findViewById(R.id.time_inverted_text_view_playback_tab);
        nextButton = (ImageButton) playbackTab.findViewById(R.id.next_button_playback_tab);
        prevButton = (ImageButton) playbackTab.findViewById(R.id.prev_button_playback_tab);
        shuffleButton = (ImageButton) playbackTab.findViewById(R.id.shuffle_button_playback_tab);
        repeatButton = (ImageButton) playbackTab.findViewById(R.id.repeat_button_playback_tab);
        artistImageView = (ImageView) playbackTab.findViewById(R.id.artist_image_view_headset);
        artistImageView.setImageResource(R.drawable.artist_placeholder);
        timePlateTextView = (TextView) playbackTab.findViewById(R.id.time_plate_text_view_playback_tab);
        albumImageView = (ImageView) playbackTab.findViewById(R.id.album_cover_image_view);
        albumTextView = (TextView) playbackTab.findViewById(R.id.album_title_text_view);
        blackView = playbackTab.findViewById(R.id.view);
    }

    private void initModeTab() {
        modeGridView = (StickyGridHeadersGridView) modeTab.findViewById(R.id.mode_gridview);

        modeGridView.setOnItemClickListener(new ModeClickListener(mainActivity));
        modeGridView.setOnItemLongClickListener(new ModeLongClickListener());
    }

    private void showTutorial() {
        ImageView swipeLeftImageView = (ImageView) playbackTab.findViewById(R.id.swipe_left_image_view);
        ImageView swipeRightImageView = (ImageView) playbackTab.findViewById(R.id.swipe_right_image_view);
        tutorialLayout = playbackTab.findViewById(R.id.tutorial_layout);
        tutorialLayout.setVisibility(View.VISIBLE);
        tutorialBlinkAnimation = AnimationUtils.loadAnimation(mainActivity, R.anim.blink_animation);
        swipeLeftImageView.startAnimation(tutorialBlinkAnimation);
        swipeRightImageView.startAnimation(tutorialBlinkAnimation);
    }

    private void initListeners() {
        playlistsListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                menu.setHeaderTitle(mainActivity.getPlaylistItemsAdapter().getItem(info.position).getTitle());
                String[] menuItems = getResources().getStringArray(R.array.playlist_item_menu);
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(android.view.Menu.NONE, i, i, menuItems[i]);
                }
            }
        });
        playlistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = mainActivity.getPlaylistItemsAdapter().getItem(position);
                if (mainActivity.getPlaylistItemsAdapter().isEditMode()) {
                    mainActivity.showRenameDialog(track, position);
                } else {
                    artistTextView.setText(track.getArtist());
                    titleTextView.setText(track.getTitle());
                    playPauseButton.setImageResource(R.drawable.pause_button_states);
                    mainActivity.getMusicPlaybackService().setPlayOnPrepared(true);
                    mainActivity.getMusicPlaybackService().play(track.getRealPosition());
                }
            }
        });
        playlistsListView.setRemoveListener(new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                mainActivity.removeTrack(which);
            }
        });
        playlistsListView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from == to) return;
                Timeline timeline = Timeline.getInstance();
                List<Track> playlist = timeline.getPlaylistTracks();
                int currentIndex = timeline.getIndex();
                if (from == currentIndex) {
                    timeline.setIndex(to);
                } else if (from < to && currentIndex < to && currentIndex > from) {
                    timeline.setIndex(currentIndex - 1);
                } else if (from > to && currentIndex < from && currentIndex > to) {
                    timeline.setIndex(currentIndex + 1);
                } else if (to == currentIndex && currentIndex > from) {
                    timeline.setIndex(currentIndex - 1);
                } else if (to == currentIndex && currentIndex < from) {
                    timeline.setIndex(currentIndex + 1);
                }
                Track item = playlist.get(from);
                playlist.remove(from);
                playlist.add(to, item);
                mainActivity.updateAdapter();
                mainActivity.changePlaylistWithoutTrackChange();
            }
        });

        clearEditTextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchPlaylistEditText.setText("");
            }
        });
        searchPlaylistEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearEditTextButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                PlaylistItemsAdapter playlistItemsAdapter = mainActivity.getPlaylistItemsAdapter();
                if (playlistItemsAdapter != null) {
                    playlistItemsAdapter.getFilter().filter(s);
                }
            }
        });


        shuffleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Timeline.getInstance().toggleShuffle();
                shuffleButton.setImageResource(Utils.getShuffleButtonImage());
                mainActivity.getMusicPlaybackService().updateWidgets();
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Timeline.getInstance().toggleRepeat();
                repeatButton.setImageResource(Utils.getRepeatButtonImage());
                mainActivity.getMusicPlaybackService().updateWidgets();
            }
        });

        artistTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AudioTimeline.hasCurrentTrack()) return;
                Intent artistInfoIntent = new Intent(mainActivity,
                        LastfmArtistViewerActivity.class);
                artistInfoIntent.putExtra(LastfmArtistViewerActivity.ARTIST,
                        AudioTimeline.getCurrentTrack().getArtist());
                mainActivity.startActivityForResult(artistInfoIntent,
                        Constants.MAIN_REQUEST_CODE);
            }
        });

        // Playback controlling.
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.playPause();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.getMusicPlaybackService().next();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.getMusicPlaybackService().prev();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
                mainActivity.getMusicPlaybackService().seekTo(seekBar.getProgress()
                        * mainActivity.getMusicPlaybackService().getDuration() / 100);
                timePlateTextView.setVisibility(View.GONE);
                mainActivity.getMusicPlaybackService().startPlayProgressUpdater();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                timePlateTextView.setVisibility(View.VISIBLE);
                timePlateTextView.setText(timeTextView.getText().toString());
                mainActivity.getMusicPlaybackService().stopPlayProgressUpdater();
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                int timeFromBeginning = seekBar.getProgress() *
                        mainActivity.getMusicPlaybackService().getDuration() / 100000;
                String time = Utils.secondsToString(timeFromBeginning);
                timePlateTextView.setText(time);
                int timeToEnd = mainActivity.getMusicPlaybackService().getDuration() / 1000 -
                        timeFromBeginning;
                timeTextView.setText("-" + Utils.secondsToString(timeToEnd));
            }
        });

        View.OnClickListener albumClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, LastfmAlbumViewerActivity.class);
                Album album = AudioTimeline.getAlbum();
                if (album == null) return;
                intent.putExtra(LastfmAlbumViewerActivity.ALBUM, album.getTitle());
                intent.putExtra(LastfmAlbumViewerActivity.ARTIST, album.getArtist());
                mainActivity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
        };
        albumImageView.setOnClickListener(albumClickListener);
        albumTextView.setOnClickListener(albumClickListener);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferencesManager.getPreferences();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.TIME_INVERTED,
                        !preferences.getBoolean(Constants.TIME_INVERTED, false));
                editor.apply();
                updateTime();
            }
        });
        SwipeDetector swipeDetector = new SwipeDetector(mainActivity);
        blackView.setOnTouchListener(swipeDetector);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    public void changeViewPagerItem(int currentItem) {
        pager.setCurrentItem(currentItem);
        indicator.setCurrentItem(currentItem);
    }

    public void updateSearchVisibility() {
        searchPlaylistEditText.setVisibility(PreferencesManager.getSavePreferences()
                .getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false) ? View.VISIBLE : View.GONE);
    }

    private void updateTime() {
        if (!mainActivity.getMusicPlaybackService().isPrepared()) return;
        seekBar.setProgress(mainActivity.getMusicPlaybackService().getCurrentPositionPercent());
        String text;
        int timeFromBeginning = mainActivity.getMusicPlaybackService().getCurrentPosition() / 1000;
        int duration = mainActivity.getMusicPlaybackService().getDuration();
        if (duration > 0) {
            if (PreferencesManager.getPreferences().getBoolean(Constants.TIME_INVERTED, false)) {
                int timeToEnd = duration / 1000 - timeFromBeginning;
                text = "-" + Utils.secondsToString(timeToEnd);
            } else {
                text = Utils.secondsToString(timeFromBeginning);
            }
            timeTextView.setText(text);
            timeDurationTextView.setText(String.format(" / %s", Utils.secondsToString(duration / 1000)));
        }
    }

    private void saveState() {
        saveTrackState();
        SharedPreferences.Editor editor = PreferencesManager.getPreferences().edit();
        if (mainActivity.getMusicPlaybackService() != null) {
            editor.putInt(Constants.CURRENT_POSITION,
                    mainActivity.getMusicPlaybackService().getCurrentPosition());
            editor.putInt(Constants.CURRENT_BUFFER,
                    mainActivity.getMusicPlaybackService().getCurrentBuffer());
            editor.putBoolean(Constants.SHUFFLE_MODE_ON,
                    AudioTimeline.getShuffleMode() == ShuffleMode.SHUFFLE);
            editor.putBoolean(Constants.REPEAT_MODE_ON,
                    AudioTimeline.getRepeatMode() == RepeatMode.REPEAT);
            editor.putInt(Constants.CURRENT_INDEX, AudioTimeline.getCurrentIndex());
        }
        editor.apply();
    }

    private void saveTrackState() {
        SharedPreferences.Editor editor = PreferencesManager.getPreferences().edit();
        final Track currentTrack = AudioTimeline.getCurrentTrack();
        if (AudioTimeline.getPlaylist() != null
                && AudioTimeline.getPlaylist().size() != 0
                && currentTrack != null) {
            editor.putString(Constants.ARTIST, currentTrack.getArtist());
            editor.putString(Constants.TITLE, currentTrack.getTitle());
            editor.putInt(Constants.DURATION, currentTrack.getDuration());
        }
        editor.putInt(Constants.CURRENT_INDEX, AudioTimeline.getCurrentIndex());
        editor.apply();
    }

    private void restorePreviousState() {
        shuffleButton.setImageResource(Utils.getShuffleButtonImage());
        repeatButton.setImageResource(Utils.getRepeatButtonImage());

        List<Track> tracks = PlaylistManager.getInstance().loadPlaylist();
        AudioTimeline.setPlaylist(tracks);
        if (AudioTimeline.getPlaylist().size() == 0) {
            return;
        }
        tracks = AudioTimeline.getPlaylist();
        SharedPreferences preferences = PreferencesManager.getPreferences();
        String artist = preferences.getString(Constants.ARTIST, "");
        String title = preferences.getString(Constants.TITLE, "");
        int currentIndex = preferences.getInt(Constants.CURRENT_INDEX, 0);
        int position = preferences.getInt(Constants.CURRENT_POSITION, 0);
        seekBar.setSecondaryProgress(preferences.getInt(Constants.CURRENT_BUFFER, 0));

        boolean currentFits = currentIndex < tracks.size();
        if (!currentFits) currentIndex = 0;
        Track currentTrack = tracks.get(currentIndex);
        boolean tracksEquals = currentFits
                && (artist + title).equalsIgnoreCase(currentTrack.getArtist()
                + currentTrack.getTitle());
        if (!tracksEquals) {
            artistImageView.setBackgroundResource(R.drawable.artist_placeholder);
            currentIndex = 0;
            artistTextView.setText(Html.fromHtml(currentTrack.getArtist()));
            titleTextView.setText(Html.fromHtml(currentTrack.getTitle()));
            position = 0;
        } else {
            artistTextView.setText(Html.fromHtml(artist));
            titleTextView.setText(Html.fromHtml(title));
        }
        AudioTimeline.setCurrentIndex(currentIndex);
        if (currentIndex > AudioTimeline.getPlaylist().size()) {
            artistImageView.setBackgroundResource(R.drawable.artist_placeholder);
            position = 0;
        }
        if (!PreferencesManager.getPreferences().getBoolean("continue_from_position", true)) {
            position = 0;
        }

        AudioTimeline.setCurrentPosition(position);
        mainActivity.restorePreviousState();
        if (!Utils.isOnline()) {
            artistImageView.setImageResource(R.drawable.artist_placeholder);
            albumImageView.setImageDrawable(null);
            albumTextView.setVisibility(View.GONE);
            return;
        }
        if (PreferencesManager.getPreferences()
                .getBoolean(Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
            new ImageModel().loadImage(AudioTimeline.getImageUrl(),
                    artistImageView, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted() {

                        }

                        @Override
                        public void onLoadingFailed(String message) {

                        }

                        @Override
                        public void onLoadingComplete(Bitmap bitmap) {
                            updatePaletteWithBitmap(bitmap);
                        }

                        @Override
                        public void onLoadingCancelled() {

                        }
                    });
        }

        Album album = AudioTimeline.getAlbum();
        if (album != null) {
            String imageUrl = album.getImageUrl();
            if (imageUrl == null || !PreferencesManager.getPreferences()
                    .getBoolean(Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
                albumImageView.setVisibility(View.GONE);
            } else {
                albumImageView.setVisibility(View.VISIBLE);
                new ImageModel().loadImage(imageUrl, albumImageView);
            }
            String albumTitle = album.getTitle();
            if (albumTitle == null) {
                albumTextView.setVisibility(View.GONE);
            } else {
                albumTextView.setVisibility(View.VISIBLE);
                albumTextView.setText(albumTitle);
            }
        }
    }

    public void setServiceConnected() {
        receiver = new ServiceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_SERVICE);
        mainActivity.registerReceiver(receiver, intentFilter);
        if (AudioTimeline.getCurrentTrack() != null) {
            playPauseButton.setImageResource(AudioTimeline.isPlaying() ?
                    R.drawable.pause_button_states : R.drawable.play_button_states);
            playPauseButton.setEnabled(true);
            mainActivity.getMusicPlaybackService().startPlayProgressUpdater();
            seekBar.setEnabled(true);
            updateTime();
            if (AudioTimeline.isStateActive())
                mainActivity.getMusicPlaybackService().showTrackInNotification();

        }
        boolean isPlaying = AudioTimeline.isPlaying();
        if (isPlaying) {
//            AudioTimeline.setStateActive(true);
            playPauseButton.setImageResource(R.drawable.pause_button_states);
        }
    }

    public int getCurrentItem() {
        return pager.getCurrentItem();
    }

    public ListView getPlaylistListView() {
        return playlistsListView;
    }

    public void stopMusicService() {
        try {
            mainActivity.unregisterReceiver(receiver);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shuffleButton.setImageResource(Utils.getShuffleButtonImage());
        repeatButton.setImageResource(Utils.getRepeatButtonImage());
        ModeAdapter adapter = mainActivity.getModeAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void clearFilter() {
        searchPlaylistEditText.setText("");
    }

    public class ModeLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            ModeItemsHelper.setEditMode(true);
            mainActivity.getModeAdapter().notifyChanges();
            return true;
        }
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int callbackType = intent.getIntExtra("callback-type", -1);
            switch (callbackType) {
                case MusicPlaybackService.TRACK_INFO_CALLBACK:
                    Track track = AudioTimeline.getCurrentTrack();
                    if (track == null) return;
                    if (PreferencesManager.getPreferences()
                            .getBoolean("scroll_to_current", false)) {
                        playlistsListView.requestFocusFromTouch();
                        playlistsListView.setSelection(AudioTimeline.getCurrentIndex());
                    }
                    AudioTimeline.setCurrentAlbum(null);
                    track.setCurrent(true);
                    artistTextView.setText(Html.fromHtml(track.getArtist()));
                    titleTextView.setText(Html.fromHtml(track.getTitle()));
                    List<Integer> listToUpdate = new ArrayList<>(
                            AudioTimeline.getPrevClickedItems());
                    listToUpdate.addAll(AudioTimeline.getQueue());
                    AudioTimeline.clearPrevClickedItems();
                    mainActivity.updateView(listToUpdate);
                    if (!Utils.isOnline()) {
                        artistImageView.setImageResource(R.drawable.artist_placeholder);
                        albumImageView.setImageDrawable(null);
                        albumTextView.setVisibility(View.GONE);
                    }
                    break;
                case MusicPlaybackService.PLAY_CALLBACK:
                    playPauseButton.setImageResource(R.drawable.pause_button_states);
                    break;
                case MusicPlaybackService.PLAY_WITHOUT_ICON_CALLBACK:
                    playPauseButton.setImageResource(R.drawable.play_button_states);
                    break;
                case MusicPlaybackService.PAUSE_CALLBACK:
                    playPauseButton.setImageResource(R.drawable.play_button_states);
                    break;
                case MusicPlaybackService.PRAPARED_CALLBACK:
                    updateTime();
                    break;
                case MusicPlaybackService.BUFFERIZATION_CALLBACK:
                    seekBar.setSecondaryProgress(intent.getIntExtra("track-buffering", 0));
                    break;
                case MusicPlaybackService.UPDATE_POSITION_CALLBACK:
                    updateTime();
                    break;
                case MusicPlaybackService.EXIT_CALLBACK:
                    mainActivity.destroy();
                    break;
                case MusicPlaybackService.SHOW_PROGRESS_BAR:
                    break;
                case MusicPlaybackService.ARTIST_INFO_CALLBACK:
                    if (PreferencesManager.getPreferences().getBoolean(
                            Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
                        String imageUrl = intent.getStringExtra(Constants.IMAGE_URL);
                        AudioTimeline.setImageUrl(imageUrl);
                        new ImageModel().loadImage(imageUrl, artistImageView, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted() {

                            }

                            @Override
                            public void onLoadingFailed(String message) {

                            }

                            @Override
                            public void onLoadingComplete(Bitmap bitmap) {
                                updatePaletteWithBitmap(bitmap);
                            }

                            @Override
                            public void onLoadingCancelled() {

                            }
                        });
                    }
                    break;
                case MusicPlaybackService.NO_URL_CALLBACK:
                    break;
                case MusicPlaybackService.INTERNET_STATE_CHANGE:
                    mainActivity.getModeAdapter().notifyDataSetChanged();
                    break;
                case MusicPlaybackService.ALBUM_INFO_CALLBACK:
                    Album album = AudioTimeline.getAlbum();
                    if (album != null && !album.equals(AudioTimeline.getPreviousAlbum())) {
                        String imageUrl = album.getImageUrl();
                        if (imageUrl == null || !PreferencesManager.getPreferences().getBoolean(
                                Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
                            albumImageView.setVisibility(View.GONE);
                        } else {
                            albumImageView.setVisibility(View.VISIBLE);
                            new ImageModel().loadImage(imageUrl, albumImageView);

                        }
                        String title = album.getTitle();
                        if (title == null) {
                            albumTextView.setVisibility(View.GONE);
                        } else {
                            albumTextView.setVisibility(View.VISIBLE);
                            albumTextView.setText(title);
                        }
                    } else if (album == null) {
                        albumImageView.setVisibility(View.GONE);
                        albumTextView.setVisibility(View.GONE);
                    }
                    mainActivity.invalidateMenu();
                    break;
                case MusicPlaybackService.TIME_CALLBACK: {
                    updateTime();
                }
                break;
                default:
                    break;
            }
        }

    }

    private void updatePaletteWithBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            backLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono_dark));
            bottomControlsLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono));
            return;
        }
        Palette.generateAsync(bitmap,
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch backSwatch =
                                palette.getDarkMutedSwatch();
                        if (backSwatch == null) {
                            backLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono_dark));
                            return;
                        }
                        backLayout.setBackgroundColor(
                                backSwatch.getRgb());

                        Palette.Swatch bottomSwatch =
                                palette.getDarkVibrantSwatch();
                        if (bottomSwatch == null) {
                            bottomControlsLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono));
                            return;
                        }
                        bottomControlsLayout.setBackgroundColor(
                                bottomSwatch.getRgb());
                    }
                });
    }
}