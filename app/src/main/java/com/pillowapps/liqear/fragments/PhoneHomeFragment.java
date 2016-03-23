package com.pillowapps.liqear.fragments;

import android.content.Context;
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
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.pillowapps.liqear.adapters.pagers.PhoneFragmentPagerAdapter;
import com.pillowapps.liqear.entities.Page;
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
import com.pillowapps.liqear.entities.events.ProgressEvent;
import com.pillowapps.liqear.entities.events.TimeEvent;
import com.pillowapps.liqear.entities.events.TrackAndAlbumInfoUpdatedEvent;
import com.pillowapps.liqear.entities.events.TrackInfoEvent;
import com.pillowapps.liqear.entities.events.UpdatePositionEvent;
import com.pillowapps.liqear.helpers.TimeUtils;
import com.pillowapps.liqear.listeners.OnSwipeListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class PhoneHomeFragment extends HomeFragment {

    private ViewPager pager;
    private View playlistTab;
    private View playbackTab;
//    private View modeTab;

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = this::onOptionsItemSelected;

    /**
     * Playlists tab
     **/
    private EditText searchPlaylistEditText;
    //    private Toolbar playlistToolbar;
    private TextView emptyPlaylistTextView;

    /**
     * Play tab
     */
//    private Toolbar playbackToolbar;
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
    private ImageButton playlistsButton;
    private ImageButton playbackButton;
    private TextView timePlateTextView;
    private ImageView artistImageView;
    private ImageView albumImageView;
    private TextView albumTextView;
    private FloatingActionButton loveFloatingActionButton;
    private View blackView;
    private ViewGroup backLayout;
    private ViewGroup bottomControlsLayout;
    private LinearLayout colorsLayout;

    private DragSortListView playlistListView;
    private Toolbar toolbar;
    private PhoneFragmentPagerAdapter pagerAdapter;
    private ViewGroup bottomControlsLayoutPlaylists;

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

        initToolbar(v);
        initViewPager(v);

        initPlaylistsTab();
        initPlaybackTab();
    }

    private void initToolbar(View v) {
        toolbar = (Toolbar) v.findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_play_tab);
        activity.setToolbar(toolbar);

//        ((Toolbar) v.findViewById(R.id.toolbar)).inflateMenu(R.menu.menu_play_tab);
//        tabs = (SmartTabLayout) v.findViewById(R.id.tabs);
//        tabs.setCustomTabView((container, position, adapter) -> {
//            LayoutInflater inflater = LayoutInflater.from(container.getContext());
//            ImageView icon = (ImageView) inflater.inflate(R.layout.custom_tab_icon1, container, false);
//            icon.setImageResource(pagerAdapter.getImageRes(position));
//            return icon;
//        });
    }

    private void initViewPager(View v) {
        final List<Page> pages = new ArrayList<>();
        Context context = getContext();
        playlistTab = View.inflate(context, R.layout.playlist_tab, null);
        playbackTab = View.inflate(context, R.layout.play_tab, null);
        pages.add(new Page(playbackTab, R.drawable.ic_play));
        pages.add(new Page(playlistTab, R.drawable.ic_playlist));
        pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(pages.size());
        pagerAdapter = new PhoneFragmentPagerAdapter(pages);
        pager.setAdapter(pagerAdapter);
//        tabs.setViewPager(pager);

        updateToolbars();

        changeViewPagerItem(PhoneFragmentPagerAdapter.PLAY_TAB_INDEX);
    }

    private void initPlaylistsTab() {
        playlistListView = (DragSortListView) playlistTab.findViewById(R.id.playlist_list_view_playlist_tab);

        playlistListView.setOnItemClickListener((parent, view, position, id) -> {
            int realPosition = playlistItemsAdapter.getItem(position).getRealPosition();
            playlistItemsAdapter.setCurrentIndex(realPosition);
            musicServiceManager.play(realPosition, true);
        });

        playlistListView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(playlistItemsAdapter.getItem(info.position).getTitle());
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.menu_main_playlist_track, menu);
        });

        playlistListView.setAdapter(playlistItemsAdapter);
        searchPlaylistEditText = (EditText) playlistTab.findViewById(R.id.search_edit_text_playlist_tab);
        searchPlaylistEditText.setVisibility(savesManager.isSearchVisible() ? View.VISIBLE : View.GONE);
        emptyPlaylistTextView = (TextView) playlistTab.findViewById(R.id.empty);
        playbackButton = (ImageButton) playlistTab.findViewById(R.id.playback_button);
        bottomControlsLayoutPlaylists = (ViewGroup) playlistTab.findViewById(R.id.bottom_controls_layout);

        Toolbar playlistBottomToolbar = (Toolbar) playlistTab.findViewById(R.id.playlist_bottom_toolbar);
        playlistBottomToolbar.inflateMenu(R.menu.menu_playlist_tab);
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
        playlistsButton = (ImageButton) playbackTab.findViewById(R.id.playlists_button);

        loveFloatingActionButton = (FloatingActionButton) playbackTab.findViewById(R.id.love_button);

        if (!authorizationInfoManager.isAuthorizedOnLastfm()) {
            loveFloatingActionButton.setVisibility(View.GONE);
        }

        blackView = playbackTab.findViewById(R.id.view);

        colorsLayout = (LinearLayout) playbackTab.findViewById(R.id.colors_layout);
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

        shuffleButton.setOnClickListener(v -> presenter.toggleShuffle());

        repeatButton.setOnClickListener(v -> presenter.toggleRepeat());

        artistTextView.setOnClickListener(v -> presenter.openArtistViewer());


        // Playback controlling.
        playPauseButton.setOnClickListener(v -> musicServiceManager.playPause());

        nextButton.setOnClickListener(v -> musicServiceManager.next());

        prevButton.setOnClickListener(v -> musicServiceManager.prev());

        playlistsButton.setOnClickListener(v -> changeViewPagerItem(PhoneFragmentPagerAdapter.PLAYLIST_TAB_INDEX));

        playbackButton.setOnClickListener(v -> changeViewPagerItem(PhoneFragmentPagerAdapter.PLAY_TAB_INDEX));


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
                if (!fromUser) {
                    return;
                }
                int timeFromBeginning = seekBar.getProgress() *
                        musicServiceManager.getDuration() / 100000;
                String time = TimeUtils.secondsToMinuteString(timeFromBeginning);
                timePlateTextView.setText(time);
                int timeToEnd = musicServiceManager.getDuration() / 1000 -
                        timeFromBeginning;
                timeTextView.setText(String.format("-%s", TimeUtils.secondsToMinuteString(timeToEnd)));
            }
        });

        View.OnClickListener albumClickListener = view -> presenter.openAlbumScreen();
        albumImageView.setOnClickListener(albumClickListener);
        albumTextView.setOnClickListener(albumClickListener);
        timeTextView.setOnClickListener(view -> {
            savesManager.toggleTimeInversion();
            updateTime();
        });
        OnSwipeListener swipeDetector = new OnSwipeListener(this::openDropButton);
        blackView.setOnTouchListener(swipeDetector);

        loveFloatingActionButton.setOnClickListener(v -> toggleLoveCurrentTrack());
    }

    public void changeViewPagerItem(int currentItem) {
        pager.setCurrentItem(currentItem);
    }

    @Override
    public void updateMainPlaylistTitle(@Nullable String playlistTitle) {
        String title = getString(R.string.app_name);
        toolbar.setTitle(title);
        toolbar.setSubtitle(playlistTitle);
    }

    @Override
    protected void updateToolbars() {
//        playlistToolbar.getMenu().clear();
//        modeToolbar.getMenu().clear();
//        modeToolbar.inflateMenu(R.menu.menu_mode_tab);
//        playlistToolbar.inflateMenu(R.menu.menu_playlist_tab);
//        playlistToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
//        modeToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    @Override
    public void updatePlayingState(boolean isPlaying) {
        Timber.d("updatePlayingState " + isPlaying);
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.pause_button);
        } else {
            playPauseButton.setImageResource(R.drawable.play_button);
        }
    }

    @Override
    public void updateLoveButton(int imageRes) {
        loveFloatingActionButton.setImageResource(imageRes);
    }

    private void updateTime() {
        seekBar.setProgress(musicServiceManager.getCurrentPositionPercent());
        String text;
        int timeFromBeginning = musicServiceManager.getCurrentPosition() / 1000;
        int duration = musicServiceManager.getDuration();
        if (duration > 0) {
            if (savesManager.isTimeInverted()) {
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
    public void changePlaylist(int index, Playlist playlist, LinkedList<Integer> queueIndexes) {
        super.changePlaylist(index, playlist, queueIndexes);
        updateMainPlaylistTitle(playlist.getTitle());
    }

    @Override
    public void playTrack(int index, boolean autoplay) {
        Track track = playlistItemsAdapter.getItem(index);

        artistTextView.setText(track.getArtist());
        titleTextView.setText(track.getTitle());
        playPauseButton.setImageResource(R.drawable.pause_button);

        musicServiceManager.play(track.getRealPosition(), autoplay);
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
            if (getContext() == null) {
                return;
            }
            int backColor = palette.getDarkMutedColor(ContextCompat.getColor(getContext(), R.color.accent_mono_dark));
            backLayout.setBackgroundColor(backColor);

            int bottomColor = palette.getMutedColor(ContextCompat.getColor(getContext(), R.color.accent_light));
            int firstBottomColor = palette.getDarkVibrantColor(bottomColor);
            bottomControlsLayout.setBackgroundColor(firstBottomColor);
            bottomControlsLayoutPlaylists.setBackgroundColor(firstBottomColor);

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
    public void updateRepeatButtonState(int imageRes) {
        repeatButton.setImageResource(imageRes);
    }

    @Override
    public void updateShuffleButtonState(int imageRes) {
        shuffleButton.setImageResource(imageRes);
    }

    @Override
    public void updateArtistPhotoAndColors(String artistImageUrl) {
        imageModel.loadImage(artistImageUrl, artistImageView, this::updatePaletteWithBitmap);
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
//        modeAdapter.notifyDataSetChanged(); todo
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
        presenter.updateLove();
        updateAlbum();
    }

    @Subscribe
    public void timeEvent(TimeEvent event) {
        updateTime();
    }

    @Subscribe
    public void trackInfoEvent(TrackInfoEvent event) {
        Track currentTrack = event.getTrack();
        titleTextView.setText(currentTrack.getTitle());
        artistTextView.setText(currentTrack.getArtist());
        playlistItemsAdapter.setCurrentIndex(event.getCurrentIndex());
        playlistItemsAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void updatePositionEvent(UpdatePositionEvent event) {
        updateTime();
    }

    @Subscribe
    public void exitEvent(ExitEvent event) {
        exit();
    }

    @Subscribe
    public void progressEvent(ProgressEvent event) {
        mainProgressBar.setVisibility(event.isShow() ? View.VISIBLE : View.GONE);
    }
}
