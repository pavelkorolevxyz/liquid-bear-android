package com.pillowapps.liqear.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.activities.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.adapters.ModeAdapter;
import com.pillowapps.liqear.adapters.PhoneFragmentAdapter;
import com.pillowapps.liqear.adapters.PlaylistItemsAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.CompletionCallback;
import com.pillowapps.liqear.components.ModeClickListener;
import com.pillowapps.liqear.components.SwipeDetector;
import com.pillowapps.liqear.components.ViewPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.events.AlbumInfoEvent;
import com.pillowapps.liqear.entities.events.ArtistInfoEvent;
import com.pillowapps.liqear.entities.events.BufferizationEvent;
import com.pillowapps.liqear.entities.events.ExitEvent;
import com.pillowapps.liqear.entities.events.NetworkStateChangeEvent;
import com.pillowapps.liqear.entities.events.PauseEvent;
import com.pillowapps.liqear.entities.events.PlayEvent;
import com.pillowapps.liqear.entities.events.PlayWithoutIconEvent;
import com.pillowapps.liqear.entities.events.PreparedEvent;
import com.pillowapps.liqear.entities.events.ShowProgressEvent;
import com.pillowapps.liqear.entities.events.TimeEvent;
import com.pillowapps.liqear.entities.events.TrackInfoEvent;
import com.pillowapps.liqear.entities.events.UpdatePositionEvent;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.StateManager;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.PlayingState;
import com.pillowapps.liqear.models.Tutorial;
import com.pillowapps.liqear.network.ImageLoadingListener;
import com.squareup.otto.Subscribe;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class PhoneFragment extends Fragment {
    private ViewPager pager;
    private View playlistTab;
    private View playbackTab;
    private View modeTab;
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
    private View tutorialLayout;
    private Animation tutorialBlinkAnimation;
    private ViewGroup backLayout;
    private ViewGroup bottomControlsLayout;

    private Tutorial tutorial = new Tutorial();
    private Toolbar modeToolbar;
    private Toolbar playbackToolbar;
    private Toolbar playlistToolbar;

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            return mainActivity.onOptionsItemSelected(menuItem);
        }
    };
    private TextView emptyTextView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.handset_fragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();
        initUi(v);
        initListeners();
        restorePreviousState();

        LBApplication.bus.register(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LBApplication.bus.unregister(this);
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

        playlistToolbar = (Toolbar) playlistTab.findViewById(R.id.toolbar);
        playbackToolbar = (Toolbar) playbackTab.findViewById(R.id.toolbar);
        modeToolbar = (Toolbar) modeTab.findViewById(R.id.toolbar);
        playlistToolbar.setTitle(R.string.playlist_tab);
        playbackToolbar.setTitle(R.string.app_name);
        modeToolbar.setTitle(R.string.mode_tab);
        updateToolbars();

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
                if (index != PhoneFragmentAdapter.PLAY_TAB_INDEX) {
                    if (tutorial.isEnabled() && tutorialBlinkAnimation != null) {
                        tutorialBlinkAnimation.cancel();
                        tutorialLayout.setVisibility(View.GONE);
                        tutorial.end();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void updateToolbars() {
        playlistToolbar.getMenu().clear();
        modeToolbar.getMenu().clear();
        modeToolbar.inflateMenu(R.menu.menu_mode_tab);
        playlistToolbar.inflateMenu(R.menu.menu_playlist_tab);
        playlistToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        modeToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        updatePlaybackToolbar();
    }

    public void updatePlaybackToolbar() {
        playbackToolbar.getMenu().clear();
        int menuLayout = R.menu.menu_play_tab_no_current_track;
        if (Timeline.getInstance().getCurrentTrack() != null) {
            menuLayout = R.menu.menu_play_tab;
            if (Timeline.getInstance().getCurrentTrack().isLoved()) {
                menuLayout = R.menu.menu_play_tab_loved;
            }
        }
        playbackToolbar.inflateMenu(menuLayout);
        playbackToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        mainActivity.setMainMenu(playbackToolbar.getMenu());
    }

    private void initUi(View v) {
        initViewPager(v);
        changeViewPagerItem(PhoneFragmentAdapter.PLAY_TAB_INDEX);

        mainActivity.init();

        initModeTab();
        initPlaylistsTab();
        initPlaybackTab();

        if (tutorial.isEnabled()) {
            showTutorial();
        }
    }

    private void initPlaylistsTab() {
        playlistsListView = (DragSortListView) playlistTab.findViewById(R.id.playlist_list_view_playlist_tab);
        playlistsListView.setAdapter(mainActivity.getPlaylistItemsAdapter());
        searchPlaylistEditText = (EditText) playlistTab.findViewById(R.id.search_edit_text_playlist_tab);
        searchPlaylistEditText.setVisibility(SharedPreferencesManager.getSavePreferences()
                .getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false) ? View.VISIBLE : View.GONE);
        emptyTextView = (TextView) playlistTab.findViewById(R.id.empty);
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
        StickyGridHeadersGridView modeGridView = (StickyGridHeadersGridView) modeTab.findViewById(R.id.mode_gridview);
        modeGridView.setOnItemClickListener(new ModeClickListener(mainActivity));
        modeGridView.setOnItemLongClickListener(new ModeLongClickListener());
        modeGridView.setAdapter(mainActivity.getModeAdapter());
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
                    playPauseButton.setImageResource(R.drawable.pause_button);
                    Timeline.getInstance().setStartPlayingOnPrepared(true);
                    mainActivity.getMusicPlaybackService().play(track.getRealPosition());
                }
            }
        });
        playlistsListView.setRemoveListener(new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                mainActivity.removeTrack(which);
                updateEmptyTextView();
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

        searchPlaylistEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                Track currentTrack = Timeline.getInstance().getCurrentTrack();
                if (currentTrack == null) return;
                Intent artistInfoIntent = new Intent(mainActivity, LastfmArtistViewerActivity.class);
                artistInfoIntent.putExtra(LastfmArtistViewerActivity.ARTIST, currentTrack.getArtist());
                mainActivity.startActivityForResult(artistInfoIntent, Constants.MAIN_REQUEST_CODE);
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
                Album album = Timeline.getInstance().getCurrentAlbum();
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
                SharedPreferences preferences = SharedPreferencesManager.getPreferences();
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
    public void onDestroy() {
        super.onDestroy();
        StateManager.savePlaylistState(mainActivity.getMusicService());
    }

    public void changeViewPagerItem(int currentItem) {
        pager.setCurrentItem(currentItem);
        indicator.setCurrentItem(currentItem);
    }

    public void updateSearchVisibility() {

        boolean visible = SharedPreferencesManager.getSavePreferences().getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false);
        if (visible) {
            searchPlaylistEditText.requestFocus();
        }
        searchPlaylistEditText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void updateTime() {
        if (!mainActivity.getMusicPlaybackService().isPrepared()) return;
        seekBar.setProgress(mainActivity.getMusicPlaybackService().getCurrentPositionPercent());
        String text;
        int timeFromBeginning = mainActivity.getMusicPlaybackService().getCurrentPosition() / 1000;
        int duration = mainActivity.getMusicPlaybackService().getDuration();
        if (duration > 0) {
            if (SharedPreferencesManager.getPreferences().getBoolean(Constants.TIME_INVERTED, false)) {
                int timeToEnd = duration / 1000 - timeFromBeginning;
                text = "-" + Utils.secondsToString(timeToEnd);
            } else {
                text = Utils.secondsToString(timeFromBeginning);
            }
            timeTextView.setText(text);
            timeDurationTextView.setText(String.format(" / %s", Utils.secondsToString(duration / 1000)));
        }
    }

    private void restorePreviousState() {
        shuffleButton.setImageResource(Utils.getShuffleButtonImage());
        repeatButton.setImageResource(Utils.getRepeatButtonImage());

        StateManager.restorePlaylistState(new CompletionCallback() {
            @Override
            public void onCompleted() {
                final Playlist playlist = Timeline.getInstance().getPlaylist();
                if (playlist == null || playlist.getTracks().size() == 0) return;
                updateMainPlaylistTitle();

                mainActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        List<Track> tracks = playlist.getTracks();

                        SharedPreferences preferences = SharedPreferencesManager.getPreferences();
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
                        Timeline.getInstance().setIndex(currentIndex);
                        if (currentIndex > tracks.size()) {
                            artistImageView.setBackgroundResource(R.drawable.artist_placeholder);
                            position = 0;
                        }
                        if (!SharedPreferencesManager.getPreferences().getBoolean("continue_from_position", true)) {
                            position = 0;
                        }

                        Timeline.getInstance().setTimePosition(position);
                        mainActivity.restorePreviousState();
                        if (!Utils.isOnline()) {
                            artistImageView.setImageResource(R.drawable.artist_placeholder);
                            albumImageView.setImageDrawable(null);
                            albumTextView.setVisibility(View.GONE);
                            return;
                        }
                        if (SharedPreferencesManager.getPreferences()
                                .getBoolean(Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
                            new ImageModel().loadImage(Timeline.getInstance().getCurrentArtistImageUrl(),
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

                        Album album = Timeline.getInstance().getCurrentAlbum();
                        if (album != null) {
                            String imageUrl = album.getImageUrl();
                            if (imageUrl == null || !SharedPreferencesManager.getPreferences()
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
                });
            }
        });
    }

    public void setServiceConnected() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_SERVICE);
        boolean playing = Timeline.getInstance().getPlayingState() == PlayingState.PLAYING;
        if (Timeline.getInstance().getCurrentTrack() != null) {
            playPauseButton.setImageResource(playing
                    ? R.drawable.pause_button
                    : R.drawable.play_button);
            if (playing) {
                mainActivity.getMusicPlaybackService().showTrackInNotification();
            }
            playPauseButton.setEnabled(true);
//            mainActivity.getMusicPlaybackService().startPlayProgressUpdater();
            seekBar.setEnabled(true);
            updateTime();
        }
    }

    public int getCurrentItem() {
        return pager.getCurrentItem();
    }

    public ListView getPlaylistListView() {
        return playlistsListView;
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

    public void updateEmptyTextView() {
        emptyTextView.setVisibility(
                playlistsListView.getAdapter() != null
                        && playlistsListView.getAdapter().getCount() > 0
                        ? View.GONE : View.VISIBLE);
    }

    public void updateMainPlaylistTitle() {
        String title = Timeline.getInstance().getPlaylist().getTitle();
        if (title == null) {
            title = getString(R.string.playlist_tab);
        }
        playlistToolbar.setTitle(title);
    }

    public class ModeLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            ModeItemsHelper.setEditMode(true);
            mainActivity.getModeAdapter().notifyChanges();
            return true;
        }
    }

    @Subscribe
    public void trackInfoEvent(TrackInfoEvent event) {
        Track track = Timeline.getInstance().getCurrentTrack();
        if (track == null) return;
        if (SharedPreferencesManager.getPreferences()
                .getBoolean("scroll_to_current", false)) {
            playlistsListView.requestFocusFromTouch();
            playlistsListView.setSelection(Timeline.getInstance().getIndex());
        }
        Timeline.getInstance().setCurrentAlbum(null);
//        track.setCurrent(true);
        artistTextView.setText(Html.fromHtml(track.getArtist()));
        titleTextView.setText(Html.fromHtml(track.getTitle()));
        List<Integer> indexesToUpdate = new ArrayList<>(Timeline.getInstance().getPreviousTracksIndexes());
        indexesToUpdate.addAll(Timeline.getInstance().getQueueIndexes());
        mainActivity.updateView(indexesToUpdate);
        if (!Utils.isOnline()) {
            artistImageView.setImageResource(R.drawable.artist_placeholder);
            albumImageView.setImageDrawable(null);
            albumTextView.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void playEvent(PlayEvent event) {
        playPauseButton.setImageResource(R.drawable.pause_button);
    }

    @Subscribe
    public void pauseEvent(PauseEvent event) {
        playPauseButton.setImageResource(R.drawable.play_button);
    }

    @Subscribe
    public void artistInfoEvent(ArtistInfoEvent event) {
        if (SharedPreferencesManager.getPreferences().getBoolean(
                Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
            String imageUrl = event.getImageUrl();
            Timeline.getInstance().setCurrentArtistImageUrl(imageUrl);
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
    }

    @Subscribe
    public void albumInfoEvent(AlbumInfoEvent event) {
        Album album = event.getAlbum();
        if (album != null && !album.equals(Timeline.getInstance().getPreviousAlbum())) {
            String imageUrl = album.getImageUrl();
            if (imageUrl == null || !SharedPreferencesManager.getPreferences().getBoolean(
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
    }

    @Subscribe
    public void exitEvent(ExitEvent event) {
        mainActivity.destroy();
    }

    @Subscribe
    public void networkStateEvent(NetworkStateChangeEvent event) {
        mainActivity.getModeAdapter().notifyDataSetChanged();
    }

    @Subscribe
    public void preparedEvent(PreparedEvent event) {
        updateTime();
    }

    @Subscribe
    public void bufferizationEvent(BufferizationEvent event) {
        seekBar.setSecondaryProgress(event.getBuffered());
    }

    @Subscribe
    public void playWithoutIconEvent(PlayWithoutIconEvent event) {
        playPauseButton.setImageResource(R.drawable.play_button);
    }

    @Subscribe
    public void showProgressEvent(ShowProgressEvent event) {
    }

    @Subscribe
    public void timeEvent(TimeEvent event) {
        updateTime();
    }

    @Subscribe
    public void updatePositionEvent(UpdatePositionEvent event) {
        updateTime();
    }

    private void updatePaletteWithBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            backLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono_dark));
            bottomControlsLayout.setBackgroundColor(getResources().getColor(R.color.accent_light));
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
                            bottomControlsLayout.setBackgroundColor(getResources().getColor(R.color.accent_light));
                            return;
                        }
                        bottomControlsLayout.setBackgroundColor(
                                bottomSwatch.getRgb());
                    }
                });
    }
}
