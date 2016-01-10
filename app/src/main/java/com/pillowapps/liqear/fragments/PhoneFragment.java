package com.pillowapps.liqear.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.activities.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.adapters.ModeGridAdapter;
import com.pillowapps.liqear.adapters.PlaylistItemsAdapter;
import com.pillowapps.liqear.adapters.pagers.PhoneFragmentPagerAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.CompletionCallback;
import com.pillowapps.liqear.components.OnItemStartDragListener;
import com.pillowapps.liqear.components.OnRecyclerItemClickListener;
import com.pillowapps.liqear.components.OnTopToBottomSwipeListener;
import com.pillowapps.liqear.components.SwipeDetector;
import com.pillowapps.liqear.components.ViewPage;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
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
import com.pillowapps.liqear.entities.events.TrackAndAlbumInfoUpdatedEvent;
import com.pillowapps.liqear.entities.events.TrackInfoEvent;
import com.pillowapps.liqear.entities.events.UpdatePositionEvent;
import com.pillowapps.liqear.helpers.ButtonStateUtils;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.StateManager;
import com.pillowapps.liqear.helpers.TimeUtils;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.PlayingState;
import com.pillowapps.liqear.models.Tutorial;
import com.pillowapps.liqear.network.ImageLoadingCompletionListener;
import com.pillowapps.liqear.network.ImageLoadingListener;
import com.squareup.otto.Subscribe;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class PhoneFragment extends MainFragment {
    private ViewPager pager;
    private UnderlinePageIndicator indicator;
    private View playlistTab;
    private View playbackTab;
    private View modeTab;

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            return mainActivity.onOptionsItemSelected(menuItem);
        }
    };

    /**
     * Playlists tab
     **/

    private ListView playlistListView;
    private EditText searchPlaylistEditText;
    private Toolbar playlistToolbar;
    private TextView emptyPlaylistTextView;
    private ItemTouchHelper mItemTouchHelper;


    /**
     * Play tab
     */

    private Toolbar playbackToolbar;
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
    private FloatingActionButton loveFloatingActionButton;
    private View blackView;
    private View tutorialLayout;
    private Animation tutorialBlinkAnimation;
    private ViewGroup backLayout;
    private ViewGroup bottomControlsLayout;

    /**
     * Modes tab
     */

    private Toolbar modeToolbar;

    private Tutorial tutorial = new Tutorial();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        StateManager.savePlaylistState(mainActivity.getMusicService());
    }

    @Override
    public void onResume() {
        super.onResume();
        shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
        repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());
        ModeGridAdapter adapter = mainActivity.getModeAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initUi(View v) {
        initViewPager(v);
        changeViewPagerItem(PhoneFragmentPagerAdapter.PLAY_TAB_INDEX);

        mainActivity.init();

        initModeTab();
        initPlaylistsTab();
        initPlaybackTab();

        if (tutorial.isEnabled()) {
            showTutorial();
        }
    }

    private void initPlaylistsTab() {
        playlistListView = (DragSortListView) playlistTab.findViewById(R.id.playlist_list_view_playlist_tab);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                PopupMenu popup = new PopupMenu(mainActivity, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_main_playlist_track, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mainActivity.onContextItemSelected(item);
                        return true;
                    }
                });

                popup.show();
            }
        };
        OnItemStartDragListener onStartDragListener = new OnItemStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        };
        PlaylistItemsAdapter playlistItemsAdapter = new PlaylistItemsAdapter(getActivity());
        mainActivity.setPlaylistItemsAdapter(playlistItemsAdapter);
        playlistListView.setAdapter(playlistItemsAdapter);
        searchPlaylistEditText = (EditText) playlistTab.findViewById(R.id.search_edit_text_playlist_tab);
        searchPlaylistEditText.setVisibility(SharedPreferencesManager.getSavePreferences()
                .getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false) ? View.VISIBLE : View.GONE);
        emptyPlaylistTextView = (TextView) playlistTab.findViewById(R.id.empty);
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

        loveFloatingActionButton = (FloatingActionButton) playbackTab.findViewById(R.id.love_button);

        blackView = playbackTab.findViewById(R.id.view);
    }

    private void initModeTab() {
        StickyGridHeadersGridView modeGridView = (StickyGridHeadersGridView) modeTab.findViewById(R.id.mode_gridview);
//        modeGridView.setOnItemClickListener(new ModeClickListener(mainActivity));
        modeGridView.setOnItemLongClickListener(new ModeLongClickListener());
//        modeGridView.setAdapter(mainActivity.getModeAdapter());
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
        pager.setAdapter(new PhoneFragmentPagerAdapter(pages));

        playlistToolbar = (Toolbar) playlistTab.findViewById(R.id.toolbar);
        playbackToolbar = (Toolbar) playbackTab.findViewById(R.id.toolbar);
        modeToolbar = (Toolbar) modeTab.findViewById(R.id.toolbar);
        playlistToolbar.setTitle(R.string.playlist_tab);
        playlistToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), playlistToolbar.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
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
                if (index != PhoneFragmentPagerAdapter.PLAY_TAB_INDEX) {
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

    private void showTutorial() {
        ImageView swipeLeftImageView = (ImageView) playbackTab.findViewById(R.id.swipe_left_image_view);
        ImageView swipeRightImageView = (ImageView) playbackTab.findViewById(R.id.swipe_right_image_view);
        tutorialLayout = playbackTab.findViewById(R.id.tutorial_layout);
        tutorialLayout.setVisibility(View.VISIBLE);
        tutorialBlinkAnimation = AnimationUtils.loadAnimation(mainActivity, R.anim.blink_animation);
        swipeLeftImageView.startAnimation(tutorialBlinkAnimation);
        swipeRightImageView.startAnimation(tutorialBlinkAnimation);
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
        }
        playbackToolbar.inflateMenu(menuLayout);
        playbackToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        mainActivity.setMainMenu(playbackToolbar.getMenu());
    }

    private void initListeners() {
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
                shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
                mainActivity.getMusicService().updateWidgets();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Timeline.getInstance().toggleRepeat();
                repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());
                mainActivity.getMusicService().updateWidgets();
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
                mainActivity.getMusicService().next();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.getMusicService().prev();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                mainActivity.getMusicService().seekTo(seekBar.getProgress()
                        * mainActivity.getMusicService().getDuration() / 100);
                timePlateTextView.setVisibility(View.GONE);
                mainActivity.getMusicService().startPlayProgressUpdater();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                timePlateTextView.setVisibility(View.VISIBLE);
                timePlateTextView.setText(timeTextView.getText().toString());
                mainActivity.getMusicService().stopPlayProgressUpdater();
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                int timeFromBeginning = seekBar.getProgress() *
                        mainActivity.getMusicService().getDuration() / 100000;
                String time = TimeUtils.secondsToMinuteString(timeFromBeginning);
                timePlateTextView.setText(time);
                int timeToEnd = mainActivity.getMusicService().getDuration() / 1000 -
                        timeFromBeginning;
                timeTextView.setText(String.format("-%s", TimeUtils.secondsToMinuteString(timeToEnd)));
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
        SwipeDetector swipeDetector = new SwipeDetector(new OnTopToBottomSwipeListener() {
            @Override
            public void onTopToBottomSwipe() {
                openDropButton();
            }
        });
        blackView.setOnTouchListener(swipeDetector);

        loveFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLoveCurrentTrack();
            }
        });
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
        if (!mainActivity.getMusicService().isPrepared()) return;
        seekBar.setProgress(mainActivity.getMusicService().getCurrentPositionPercent());
        String text;
        int timeFromBeginning = mainActivity.getMusicService().getCurrentPosition() / 1000;
        int duration = mainActivity.getMusicService().getDuration();
        if (duration > 0) {
            if (SharedPreferencesManager.getPreferences().getBoolean(Constants.TIME_INVERTED, false)) {
                int timeToEnd = duration / 1000 - timeFromBeginning;
                text = String.format("-%s", TimeUtils.secondsToMinuteString(timeToEnd));
            } else {
                text = TimeUtils.secondsToMinuteString(timeFromBeginning);
            }
            timeTextView.setText(text);
            timeDurationTextView.setText(String.format(" / %s", TimeUtils.secondsToMinuteString(duration / 1000)));
        }
    }

    private void restorePreviousState() {
        shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
        repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());

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
                        if (!NetworkUtils.isOnline()) {
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
                                        public void onLoadingComplete(Bitmap bitmap) {
                                            updatePaletteWithBitmap(bitmap);
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
                mainActivity.getMusicService().showTrackInNotification();
            }
            playPauseButton.setEnabled(true);
            seekBar.setEnabled(true);
            updateTime();
        }
    }


    public int getCurrentItem() {
        return pager.getCurrentItem();
    }

    public void clearFilter() {
        searchPlaylistEditText.setText("");
    }

    public void updateEmptyTextView() {
        emptyPlaylistTextView.setVisibility(
                playlistListView.getAdapter() != null
                        && playlistListView.getAdapter().getCount() > 0
                        ? View.GONE : View.VISIBLE);
    }

    public void updateMainPlaylistTitle() {
        String title = Timeline.getInstance().getPlaylist().getTitle();
        if (title == null) {
            title = getString(R.string.playlist_tab);
        }
        playlistToolbar.setTitle(title);
    }

    public OnRecyclerItemClickListener getPlaylistItemClickListener() {
        return new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                playTrack(position);
            }
        };
    }

    public void playTrack(int position) {
        Track track = mainActivity.getPlaylistItemsAdapter().getItem(position);
        if (mainActivity.getPlaylistItemsAdapter().isEditMode()) {
            mainActivity.showRenameDialog(track, position);
        } else {
            artistTextView.setText(track.getArtist());
            titleTextView.setText(track.getTitle());
            playPauseButton.setImageResource(R.drawable.pause_button);
            Timeline.getInstance().setStartPlayingOnPrepared(true);
            mainActivity.getMusicService().play(track.getRealPosition());
        }
    }

    public void updateLoveButton() {
        loveFloatingActionButton.setImageResource(ButtonStateUtils.getLoveButtonImage());
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
        if (SharedPreferencesManager.getPreferences().getBoolean("scroll_to_current", false)) {
            playlistListView.requestFocusFromTouch();
            playlistListView.setSelection(Timeline.getInstance().getIndex());
        }
        Timeline.getInstance().setCurrentAlbum(null);
//        track.setCurrent(true);
        artistTextView.setText(Html.fromHtml(track.getArtist()));
        titleTextView.setText(Html.fromHtml(track.getTitle()));
        List<Integer> indexesToUpdate = new ArrayList<>(Timeline.getInstance().getPreviousTracksIndexes());
        indexesToUpdate.addAll(Timeline.getInstance().getQueueIndexes());
        mainActivity.updateView(indexesToUpdate);
        if (!NetworkUtils.isOnline()) {
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
            new ImageModel().loadImage(imageUrl, artistImageView, new ImageLoadingCompletionListener() {
                @Override
                public void onLoadingComplete(Bitmap bitmap) {
                    updatePaletteWithBitmap(bitmap);
                }
            });
        }
    }

    @Subscribe
    public void albumInfoEvent(TrackAndAlbumInfoUpdatedEvent event) {
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
        updateLoveButton();
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
        Palette.Builder builder = new Palette.Builder(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch backSwatch = palette.getDarkMutedSwatch();
                if (backSwatch == null) {
                    backLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono_dark));
                    return;
                }
                backLayout.setBackgroundColor(backSwatch.getRgb());

                Palette.Swatch bottomSwatch = palette.getDarkVibrantSwatch();
                if (bottomSwatch == null) {
                    bottomControlsLayout.setBackgroundColor(getResources().getColor(R.color.accent_light));
                    loveFloatingActionButton.setBackgroundTintList(
                            ColorStateList.valueOf(getResources().getColor(R.color.accent_light)));
                    return;
                }
                bottomControlsLayout.setBackgroundColor(bottomSwatch.getRgb());
                loveFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(bottomSwatch.getRgb()));
            }
        });
    }

    public ListView getPlaylistListView() {
        return playlistListView;
    }
}
