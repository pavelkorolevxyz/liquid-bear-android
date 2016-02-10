package com.pillowapps.liqear.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.modes.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.adapters.ModeGridAdapter;
import com.pillowapps.liqear.adapters.pagers.PhoneFragmentPagerAdapter;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.ViewPage;
import com.pillowapps.liqear.entities.events.ArtistInfoEvent;
import com.pillowapps.liqear.entities.events.BufferizationEvent;
import com.pillowapps.liqear.entities.events.NetworkStateChangeEvent;
import com.pillowapps.liqear.entities.events.PauseEvent;
import com.pillowapps.liqear.entities.events.PlayEvent;
import com.pillowapps.liqear.entities.events.PlayWithoutIconEvent;
import com.pillowapps.liqear.entities.events.PreparedEvent;
import com.pillowapps.liqear.entities.events.TimeEvent;
import com.pillowapps.liqear.entities.events.TrackAndAlbumInfoUpdatedEvent;
import com.pillowapps.liqear.entities.events.TrackInfoEvent;
import com.pillowapps.liqear.entities.events.UpdatePositionEvent;
import com.pillowapps.liqear.helpers.ButtonStateUtils;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.TimeUtils;
import com.pillowapps.liqear.listeners.OnModeClickListener;
import com.pillowapps.liqear.listeners.OnSwipeListener;
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
    private LinearLayout colorsLayout;

    /**
     * Modes tab
     */
    private Toolbar modeToolbar;

    private ModeGridAdapter modeAdapter;
    private DragSortListView playlistListView;

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

    private void updateArtistPhoto() {
        presenter.updateArtistPhoto();
    }

    public void updateAlbum() {
        presenter.updateAlbum();
    }

    private void initUi(View v) {
        mainProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        initViewPager(v);

        initModeTab();
        initPlaylistsTab();
        initPlaybackTab();

        presenter.showTutorial();
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
                // No op.
            }

            @Override
            public void onPageSelected(int position) {
                setHasOptionsMenu(true);
                if (position != PhoneFragmentPagerAdapter.PLAY_TAB_INDEX) {
                    presenter.hideTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // No op.
            }
        });
        changeViewPagerItem(PhoneFragmentPagerAdapter.PLAY_TAB_INDEX);
    }

    private void initPlaylistsTab() {
        playlistListView = (DragSortListView) playlistTab.findViewById(R.id.playlist_list_view_playlist_tab);

        playlistListView.setOnItemClickListener((parent, view, position, id) -> {
            musicServiceManager.play(playlistItemsAdapter.getItem(position).getRealPosition());
        });
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

        colorsLayout = (LinearLayout) playbackTab.findViewById(R.id.colors_layout);
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
            presenter.toggleShuffle();
        });

        repeatButton.setOnClickListener(v -> {
            presenter.toggleRepeat();
        });

        artistTextView.setOnClickListener(v -> {
            presenter.openArtistViewer();
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
                musicServiceManager.startUpdaters();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                timePlateTextView.setVisibility(View.VISIBLE);
                timePlateTextView.setText(timeTextView.getText().toString());
                musicServiceManager.stopUpdaters();
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
            Album album = timeline.getCurrentAlbum();
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
        String title = timeline.getPlaylist().getTitle();
        if (title == null) {
            title = getString(R.string.playlist_tab);
        }
        playlistToolbar.setTitle(title);
    }

    @Override
    public void showTutorial() {
        ImageView swipeLeftImageView = (ImageView) playbackTab.findViewById(R.id.swipe_left_image_view);
        ImageView swipeRightImageView = (ImageView) playbackTab.findViewById(R.id.swipe_right_image_view);
        tutorialLayout = playbackTab.findViewById(R.id.tutorial_layout);
        tutorialLayout.setVisibility(View.VISIBLE);
        tutorialBlinkAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_animation);
        swipeLeftImageView.startAnimation(tutorialBlinkAnimation);
        swipeRightImageView.startAnimation(tutorialBlinkAnimation);
    }

    @Override
    protected void updateToolbars() {
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
        if (timeline.getCurrentTrack() != null) {
            menuLayout = R.menu.menu_play_tab;
        }
        playbackToolbar.inflateMenu(menuLayout);
        playbackToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        mainMenu = playbackToolbar.getMenu();
    }

    private void updateTime() {
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
        loveFloatingActionButton.setImageResource(ButtonStateUtils.getLoveButtonImage(timeline.getCurrentTrack()));
    }

    @Override
    public void changePlaylist(int index, boolean autoPlay) {
        super.changePlaylist(index, autoPlay);

        updateMainPlaylistTitle();
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

    private void updatePaletteWithBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            backLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent_mono_dark));
            bottomControlsLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent_light));
            return;
        }
        new Palette.Builder(bitmap).generate(palette -> {

            // all palette colors for investigation
//            int darkMutedColor = palette.getDarkMutedColor(Color.TRANSPARENT);
//            int darkVibrantColor = palette.getDarkVibrantColor(Color.TRANSPARENT);
//            int lightMutedColor = palette.getLightMutedColor(Color.TRANSPARENT);
//            int lightVibrantColor = palette.getLightVibrantColor(Color.TRANSPARENT);
//            int mutedColor = palette.getMutedColor(Color.TRANSPARENT);
//            int vibrantColor = palette.getVibrantColor(Color.TRANSPARENT);
//            colorsLayout.getChildAt(0).setBackgroundColor(darkMutedColor);
//            colorsLayout.getChildAt(1).setBackgroundColor(darkVibrantColor);
//            colorsLayout.getChildAt(2).setBackgroundColor(lightMutedColor);
//            colorsLayout.getChildAt(3).setBackgroundColor(lightVibrantColor);
//            colorsLayout.getChildAt(4).setBackgroundColor(mutedColor);
//            colorsLayout.getChildAt(5).setBackgroundColor(vibrantColor);

            int backColor = palette.getDarkMutedColor(ContextCompat.getColor(getContext(), R.color.accent_mono_dark));
            backLayout.setBackgroundColor(backColor);

            int bottomColor = palette.getMutedColor(ContextCompat.getColor(getContext(), R.color.accent_light));
            int firstBottomColor = palette.getDarkVibrantColor(bottomColor);
            bottomControlsLayout.setBackgroundColor(firstBottomColor);

            loveFloatingActionButton.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{firstBottomColor}));
        });
    }

    @Override
    public void clearSearch() {
        searchPlaylistEditText.setText("");
    }

    @Override
    public void setMainPlaylistSelection(int currentIndex) {
        playlistListView.setSelection(currentIndex);
    }

    @Override
    public void updateModeListEditMode() {
        modeAdapter.notifyChanges();
    }

    @Override
    public void updateSearchVisibility(boolean visible) {
        if (visible) {
            searchPlaylistEditText.requestFocus();
        }
        searchPlaylistEditText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showArtistPlaceholder() {
        artistImageView.setBackgroundResource(R.drawable.artist_placeholder);
    }

    @Override
    public void updateTrackTitle(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void updateTrackArtist(String artist) {
        artistTextView.setText(artist);
    }

    @Override
    public void updateRepeatButtonState() {
        repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage(timeline.getRepeatMode()));
    }

    @Override
    public void updateShuffleButtonState() {
        shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage(timeline.getShuffleMode()));
    }

    @Override
    public void hideTutorial() {
        if (tutorialBlinkAnimation != null) {
            tutorialBlinkAnimation.cancel();
        }
        tutorialLayout.setVisibility(View.GONE);
    }

    @Override
    public void updateArtistPhotoAndColors() {
        imageModel.loadImage(timeline.getCurrentArtistImageUrl(), artistImageView, this::updatePaletteWithBitmap);
    }

    @Override
    public void hideAlbumImage() {
        albumImageView.setImageDrawable(null);
        albumImageView.setVisibility(View.GONE);
    }

    @Override
    public void hideAlbumTitle() {
        albumTextView.setVisibility(View.GONE);
    }

    @Override
    public void showAlbumImage(String imageUrl) {
        albumImageView.setVisibility(View.VISIBLE);
        imageModel.loadImage(imageUrl, albumImageView);
    }

    @Override
    public void showAlbumTitle(String albumTitle) {
        albumTextView.setVisibility(View.VISIBLE);
        albumTextView.setText(albumTitle);
    }

    @Subscribe
    public void pauseEvent(PauseEvent event) {
        playPauseButton.setImageResource(R.drawable.play_button);
    }

    @Subscribe
    public void playEvent(PlayEvent event) {
        playPauseButton.setImageResource(R.drawable.pause_button);
    }

    @Subscribe
    public void networkStateEvent(NetworkStateChangeEvent event) {
        modeAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void artistInfoEvent(ArtistInfoEvent event) {
        updateArtistPhoto();
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
    public void trackAndAlbumInfoUpdatedEvent(TrackAndAlbumInfoUpdatedEvent event) {
        loveFloatingActionButton.setImageResource(ButtonStateUtils.getLoveButtonImage(timeline.getCurrentTrack()));
        updateAlbum();
    }

    @Subscribe
    public void timeEvent(TimeEvent event) {
        updateTime();
    }

    @Subscribe
    public void trackInfoEvent(TrackInfoEvent event) {
        Track currentTrack = timeline.getCurrentTrack();
        titleTextView.setText(currentTrack.getTitle());
        artistTextView.setText(currentTrack.getArtist());
        playlistItemsAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void updatePositionEvent(UpdatePositionEvent event) {
        updateTime();
    }

    public class ModeLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            ModeItemsHelper.setEditMode(true);
            modeAdapter.notifyChanges();
            return true;
        }
    }
}
