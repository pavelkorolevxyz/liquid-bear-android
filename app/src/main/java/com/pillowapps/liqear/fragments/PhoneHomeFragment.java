package com.pillowapps.liqear.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.adapters.ModeGridAdapter;
import com.pillowapps.liqear.adapters.pagers.PhoneFragmentPagerAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.ViewPage;
import com.pillowapps.liqear.entities.events.BufferizationEvent;
import com.pillowapps.liqear.entities.events.NetworkStateChangeEvent;
import com.pillowapps.liqear.entities.events.PlayWithoutIconEvent;
import com.pillowapps.liqear.entities.events.PreparedEvent;
import com.pillowapps.liqear.entities.events.TimeEvent;
import com.pillowapps.liqear.entities.events.UpdatePositionEvent;
import com.pillowapps.liqear.helpers.ButtonStateUtils;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.TimeUtils;
import com.pillowapps.liqear.listeners.OnModeClickListener;
import com.pillowapps.liqear.listeners.OnSwipeListener;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.TutorialModel;
import com.squareup.otto.Subscribe;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class PhoneHomeFragment extends HomeFragment {

    private ViewPager pager;
    private UnderlinePageIndicator indicator;
    private View playlistTab;
    private View playbackTab;
    private View modeTab;

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = this::onOptionsItemSelected;

    /**
     * Playlists tab
     **/
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

    private TutorialModel tutorial = new TutorialModel();
    private ModeGridAdapter modeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LBApplication.get(getContext()).applicationComponent().inject(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.handset_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
        initListeners();
        restoreState();
    }

    private void restoreState() {
        shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
        repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());

        stateManager.restorePlaylistState(() -> {
            final Playlist playlist = Timeline.getInstance().getPlaylist();
            if (playlist == null || playlist.getTracks().size() == 0) return;
            updateMainPlaylistTitle();

            getActivity().runOnUiThread(() -> {
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
                updateAdapter();
                Timeline.getInstance().updateRealTrackPositions();

                if (!NetworkUtils.isOnline()) {
                    artistImageView.setImageResource(R.drawable.artist_placeholder);
                    albumImageView.setImageDrawable(null);
                    albumTextView.setVisibility(View.GONE);
                    return;
                }
                if (SharedPreferencesManager.getPreferences()
                        .getBoolean(Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
                    new ImageModel().loadImage(Timeline.getInstance().getCurrentArtistImageUrl(),
                            artistImageView, bitmap -> {
//                                                updatePaletteWithBitmap(bitmap); todo
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
                updateAdapter();
            });
        });
    }

    private void initUi(View v) {
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        initViewPager(v);
        changeViewPagerItem(PhoneFragmentPagerAdapter.PLAY_TAB_INDEX);

        initModeTab();
        initPlaylistsTab();
        initPlaybackTab();

        if (tutorial.isEnabled()) {
            showTutorial();
        }
    }

    private void initViewPager(View v) {
        final List<ViewPage> pages = new ArrayList<>();
        Context context = getContext();
        playlistTab = View.inflate(context, R.layout.playlist_tab, null);
        playbackTab = View.inflate(context, R.layout.play_tab, null);
        modeTab = View.inflate(context, R.layout.mode_tab, null);
        pages.add(new ViewPage(playlistTab, R.string.playlist_tab));
        pages.add(new ViewPage(playbackTab, R.string.play_tab));
        pages.add(new ViewPage(modeTab, R.string.mode_tab));
        pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(pages.size());
        pager.setAdapter(new PhoneFragmentPagerAdapter(pages));

        playlistToolbar = (Toolbar) playlistTab.findViewById(R.id.toolbar);
        playbackToolbar = (Toolbar) playbackTab.findViewById(R.id.toolbar);
        modeToolbar = (Toolbar) modeTab.findViewById(R.id.toolbar);
        playlistToolbar.setTitle(R.string.playlist_tab);
        playlistToolbar.setOnClickListener(v1 -> Toast.makeText(getActivity(), playlistToolbar.getTitle(), Toast.LENGTH_SHORT).show());
        playbackToolbar.setTitle(R.string.app_name);
        modeToolbar.setTitle(R.string.mode_tab);
        updateToolbars();

        indicator = (UnderlinePageIndicator) v.findViewById(R.id.indicator);
        indicator.setSelectedColor(ContextCompat.getColor(activity, R.color.accent));
        indicator.setOnClickListener(null);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setHasOptionsMenu(true);
                if (position != PhoneFragmentPagerAdapter.PLAY_TAB_INDEX) {
                    if (tutorial.isEnabled() && tutorialBlinkAnimation != null) {
                        tutorialBlinkAnimation.cancel();
                        tutorialLayout.setVisibility(View.GONE);
                        tutorial.end();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPlaylistsTab() {
        ListView playlistListView = (DragSortListView) playlistTab.findViewById(R.id.playlist_list_view_playlist_tab);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

//        View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                PopupMenu popup = new PopupMenu(getContext(), v);
//                //Inflating the Popup using xml file
//                popup.getMenuInflater()
//                        .inflate(R.menu.menu_main_playlist_track, popup.getMenu());
//
//                //registering popup with OnMenuItemClickListener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        onContextItemSelected(item);
//                        return true;
//                    }
//                });
//
//                popup.show();
//            }
//        };
//        OnItemStartDragListener onStartDragListener = new OnItemStartDragListener() {
//            @Override
//            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
//                mItemTouchHelper.startDrag(viewHolder);
//            }
//        };
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
        modeAdapter = new ModeGridAdapter(activity);

        StickyGridHeadersGridView modeGridView = (StickyGridHeadersGridView) modeTab.findViewById(R.id.mode_gridview);
        modeGridView.setOnItemClickListener(new OnModeClickListener(this));
        modeGridView.setOnItemLongClickListener(new ModeLongClickListener());
        modeGridView.setAdapter(modeAdapter);
    }

    private void initListeners() {
        searchPlaylistEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (playlistItemsAdapter != null) {
                    playlistItemsAdapter.getFilter().filter(s);
                }
            }
        });

        shuffleButton.setOnClickListener(v -> {
            Timeline.getInstance().toggleShuffle();
            shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
            musicServiceManager.updateWidgets();
        });

        repeatButton.setOnClickListener(v -> {
            Timeline.getInstance().toggleRepeat();
            repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());
            musicServiceManager.updateWidgets();
        });

        artistTextView.setOnClickListener(v -> {
            Track currentTrack = Timeline.getInstance().getCurrentTrack();
            if (currentTrack == null) return;
            Intent artistInfoIntent = new Intent(activity, LastfmArtistViewerActivity.class);
            artistInfoIntent.putExtra(LastfmArtistViewerActivity.ARTIST, currentTrack.getArtist());
            startActivityForResult(artistInfoIntent, Constants.MAIN_REQUEST_CODE);
        });


        // Playback controlling.
        playPauseButton.setOnClickListener(v -> musicServiceManager.playPause());

        nextButton.setOnClickListener(v -> musicServiceManager.next());

        prevButton.setOnClickListener(v -> musicServiceManager.prev());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicServiceManager.seekTo(seekBar.getProgress()
                        * musicServiceManager.getDuration() / 100);
                timePlateTextView.setVisibility(View.GONE);
                musicServiceManager.startPlayProgressUpdater();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                timePlateTextView.setVisibility(View.VISIBLE);
                timePlateTextView.setText(timeTextView.getText().toString());
                musicServiceManager.stopPlayProgressUpdater();
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                int timeFromBeginning = seekBar.getProgress() *
                        musicServiceManager.getDuration() / 100000;
                String time = TimeUtils.secondsToMinuteString(timeFromBeginning);
                timePlateTextView.setText(time);
                int timeToEnd = musicServiceManager.getDuration() / 1000 -
                        timeFromBeginning;
                timeTextView.setText(String.format("-%s", TimeUtils.secondsToMinuteString(timeToEnd)));
            }
        });

        View.OnClickListener albumClickListener = view -> {
            Intent intent = new Intent(activity, LastfmAlbumViewerActivity.class);
            Album album = Timeline.getInstance().getCurrentAlbum();
            if (album == null) return;
            intent.putExtra(LastfmAlbumViewerActivity.ALBUM, album.getTitle());
            intent.putExtra(LastfmAlbumViewerActivity.ARTIST, album.getArtist());
            startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
        };
        albumImageView.setOnClickListener(albumClickListener);
        albumTextView.setOnClickListener(albumClickListener);
        timeTextView.setOnClickListener(view -> {
            SharedPreferences preferences = SharedPreferencesManager.getPreferences();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.TIME_INVERTED,
                    !preferences.getBoolean(Constants.TIME_INVERTED, false));
            editor.apply();
            updateTime();
        });
        OnSwipeListener swipeDetector = new OnSwipeListener(this::openDropButton);
        blackView.setOnTouchListener(swipeDetector);

        loveFloatingActionButton.setOnClickListener(v -> toggleLoveCurrentTrack());
    }

    public void changeViewPagerItem(int currentItem) {
        pager.setCurrentItem(currentItem);
        indicator.setCurrentItem(currentItem);
    }

    public void updateMainPlaylistTitle() {
        String title = Timeline.getInstance().getPlaylist().getTitle();
        if (title == null) {
            title = getString(R.string.playlist_tab);
        }
        playlistToolbar.setTitle(title);
    }

    private void showTutorial() {
        ImageView swipeLeftImageView = (ImageView) playbackTab.findViewById(R.id.swipe_left_image_view);
        ImageView swipeRightImageView = (ImageView) playbackTab.findViewById(R.id.swipe_right_image_view);
        tutorialLayout = playbackTab.findViewById(R.id.tutorial_layout);
        tutorialLayout.setVisibility(View.VISIBLE);
        tutorialBlinkAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_animation);
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
        mainMenu = playbackToolbar.getMenu();
    }

    private void updateTime() {
        if (musicServiceManager.isPrepared()) return;
        seekBar.setProgress(musicServiceManager.getCurrentPositionPercent());
        String text;
        int timeFromBeginning = musicServiceManager.getCurrentPosition() / 1000;
        int duration = musicServiceManager.getDuration();
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

    @Override
    public void updateLoveButton() {
        loveFloatingActionButton.setImageResource(ButtonStateUtils.getLoveButtonImage());
    }

    @Override
    public void changePlaylist(int index, boolean autoPlay) {
        super.changePlaylist(index, autoPlay);

        updateMainPlaylistTitle();
        changeViewPagerItem(0);
    }

    @Override
    public void playTrack(int index) {
        Track track = playlistItemsAdapter.getItem(index);

        artistTextView.setText(track.getArtist());
        titleTextView.setText(track.getTitle());
        playPauseButton.setImageResource(R.drawable.pause_button);

        musicServiceManager.play(track.getRealPosition());
    }

    @Override
    public void updateEmptyPlaylistTextView() {
        boolean isEmpty = playlistItemsAdapter == null || playlistItemsAdapter.getCount() == 0;
        emptyPlaylistTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    public void networkStateEvent(NetworkStateChangeEvent event) {
        modeAdapter.notifyDataSetChanged();
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
    public void timeEvent(TimeEvent event) {
        updateTime();
    }

    @Subscribe
    public void updatePositionEvent(UpdatePositionEvent event) {
        updateTime();
    }

    public class ModeLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            ModeItemsHelper.setEditMode(true);
//            mainActivity.getModeAdapter().notifyChanges();
            return true;
        }
    }
}
