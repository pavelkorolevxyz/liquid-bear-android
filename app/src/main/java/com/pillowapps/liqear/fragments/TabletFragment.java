package com.pillowapps.liqear.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.activities.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.adapters.PlaylistItemsAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.CompletionCallback;
import com.pillowapps.liqear.components.OnTopToBottomSwipeListener;
import com.pillowapps.liqear.components.SwipeDetector;
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
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.StateManager;
import com.pillowapps.liqear.helpers.TimeUtils;
import com.pillowapps.liqear.models.PlayingState;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class TabletFragment extends MainFragment {
    public TextView artistTextView;
    public TextView titleTextView;
    public ImageButton playPauseButton;
    public SeekBar seekBar;
    public TextView timeTextView;
    public TextView timeDurationTextView;
    public ImageButton nextButton;
    public ImageButton prevButton;
    public ImageButton shuffleButton;
    public ImageButton repeatButton;
    private DragSortListView listView;
    private EditText searchPlaylistEditText;
    private ImageButton clearEditTextButton;
    private ImageView artistImageView;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(300))
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageView albumImageView;
    private TextView albumTextView;
    private View blackView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tablet_fragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();
        LBApplication.bus.register(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LBApplication.bus.unregister(this);
    }

    public void init() {
        initUi(getView());
        initListeners();
        restorePreviousState();
    }

    private void initUi(View v) {
        listView = (DragSortListView) v.findViewById(R.id.playlist_list_view_playlist_tab);
        listView.setAdapter(mainActivity.getPlaylistItemsAdapter());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchPlaylistEditText = (EditText) v.findViewById(R.id.search_edit_text_playlist_tab);
        searchPlaylistEditText.setVisibility(SharedPreferencesManager.getSavePreferences()
                .getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false)
                ? View.VISIBLE : View.GONE);
        clearEditTextButton = (ImageButton) v.findViewById(
                R.id.clear_edit_text_button_playlist_tab);

        PlaybackControlFragment playbackControlFragment =
                mainActivity.getPlaybackControlFragment();
        playPauseButton = playbackControlFragment.getPlayPauseButton();
        seekBar = playbackControlFragment.getSeekBar();
        timeTextView = playbackControlFragment.getTimeTextView();
        timeDurationTextView = playbackControlFragment.getTimeDurationTextView();
        nextButton = playbackControlFragment.getNextButton();
        prevButton = playbackControlFragment.getPrevButton();
        shuffleButton = playbackControlFragment.getShuffleButton();
        repeatButton = playbackControlFragment.getRepeatButton();
        artistTextView = (TextView) v.findViewById(R.id.artist_text_view_playback_tab);
        titleTextView = (TextView) v.findViewById(R.id.title_text_view_playback_tab);
        albumTextView = (TextView) v.findViewById(R.id.album_title_text_view);
        albumImageView = (ImageView) v.findViewById(R.id.album_cover_image_view);
        artistImageView = (ImageView) v.findViewById(R.id.artist_image_view);
        blackView = v.findViewById(R.id.view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StateManager.savePlaylistState(mainActivity.getMusicService());
    }

    private void initListeners() {
        artistTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Track currentTrack = Timeline.getInstance().getCurrentTrack();
                if (currentTrack == null) return;
                Intent artistInfoIntent = new Intent(mainActivity, LastfmArtistViewerActivity.class);
                artistInfoIntent.putExtra(LastfmArtistViewerActivity.ARTIST,
                        currentTrack.getArtist());
                mainActivity.startActivityForResult(artistInfoIntent,
                        Constants.MAIN_REQUEST_CODE);
            }
        });

        View.OnClickListener albumClickListener = new

                View.OnClickListener() {
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

//        recycler.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() { todo
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                if (v.getId() == R.id.playlist_list_view_playlist_tab) {
//                    AdapterView.AdapterContextMenuInfo info =
//                            (AdapterView.AdapterContextMenuInfo) menuInfo;
//                    menu.setHeaderTitle(((Track) recycler.getAdapter()
//                            .getItem(info.position)).getTitle());
//                    String[] menuItems = getResources().getStringArray(R.array.playlist_item_menu);
//                    for (int i = 0; i < menuItems.length; i++) {
//                        menu.add(android.view.Menu.NONE, i, i, menuItems[i]);
//                    }
//                }
//            }
//        });
//        recycler.setClickable(true);
//        recycler.setFocusable(true);
//        recycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                List<Track> playlist = Timeline.getInstance().getPlaylistTracks();
//                Track track = playlist.get(mainActivity.getPlaylistItemsAdapter()
//                        .getItem(position).getRealPosition());
//                if (mainActivity.getPlaylistItemsAdapter().isEditMode()) {
//                    mainActivity.showRenameDialog(track, position);
//                } else {
//                    artistTextView.setText(Html.fromHtml(track.getArtist()));
//                    titleTextView.setText(Html.fromHtml(track.getTitle()));
//                    playPauseButton.setImageResource(R.drawable.pause_button);
//                    Timeline.getInstance().setStartPlayingOnPrepared(true);
//                    mainActivity.getMusicService().play(track.getRealPosition());
//                }
//            }
//        });
//        recycler.setRemoveListener(new DragSortListView.RemoveListener() {
//            @Override
//            public void remove(int which) {
//                mainActivity.removeTrack(which);
//            }
//        });
//        recycler.setDropListener(new DragSortListView.DropListener() {
//            @Override
//            public void drop(int from, int to) {
//                if (from == to) return;
//                List<Track> playlist = Timeline.getInstance().getPlaylistTracks();
//                int currentIndex = Timeline.getInstance().getIndex();
//                int index = 0;
//                if (from == currentIndex) {
//                    index = to;
//                } else if (from < to && currentIndex < to && currentIndex > from) {
//                    index = currentIndex - 1;
//                } else if (from > to && currentIndex < from && currentIndex > to) {
//                    index = currentIndex + 1;
//                } else if (to == currentIndex && currentIndex > from) {
//                    index = currentIndex - 1;
//                } else if (to == currentIndex && currentIndex < from) {
//                    index = currentIndex + 1;
//                }
//                Timeline.getInstance().setIndex(index);
//                Track item = playlist.get(from);
//                playlist.remove(from);
//                playlist.add(to, item);
//                mainActivity.updateAdapter();
//                mainActivity.changePlaylistWithoutTrackChange();
//            }
//        });
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
                PlaylistItemsAdapter playlistItemsAdapter =
                        mainActivity.getPlaylistItemsAdapter();
                if (playlistItemsAdapter != null) {
                    playlistItemsAdapter.getFilter().filter(s);
                }
            }
        });
        SwipeDetector swipeDetector = new SwipeDetector(new OnTopToBottomSwipeListener() {
            @Override
            public void onTopToBottomSwipe() {
                openDropButton();
            }
        });
        blackView.setOnTouchListener(swipeDetector);
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
            timeDurationTextView.setText(TimeUtils.secondsToMinuteString(duration / 1000));
        }
    }

    public void setServiceConnected() {
        boolean isPlaying = Timeline.getInstance().getPlayingState() == PlayingState.PLAYING;
        if (Timeline.getInstance().getCurrentTrack() != null) {
            playPauseButton.setImageResource(isPlaying ?
                    R.drawable.pause_button : R.drawable.play_button);
            playPauseButton.setEnabled(true);
            mainActivity.getMusicService().startPlayProgressUpdater();
            seekBar.setEnabled(true);
            updateTime();
            if (isPlaying)
                mainActivity.getMusicService().showTrackInNotification();

        }
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.pause_button);
        }
    }

    public ListView getPlaylistListView() {
        return listView;
    }

    public void clearFilter() {
        searchPlaylistEditText.setText("");
    }

    public void updateSearchVisibility() {
        boolean visible = SharedPreferencesManager.getSavePreferences().getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false);
        if (visible) {
            searchPlaylistEditText.requestFocus();
        }
        searchPlaylistEditText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void restorePreviousState() {
        shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
        repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());

        StateManager.restorePlaylistState(new CompletionCallback() {
            @Override
            public void onCompleted() {
                final Playlist playlist = Timeline.getInstance().getPlaylist();
                if (playlist == null || playlist.getTracks().size() == 0) return;

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
                        boolean tracksEquals = currentFits && (artist + title)
                                .equalsIgnoreCase(currentTrack.getArtist()
                                        + currentTrack.getTitle());
                        if (!tracksEquals) {
                            currentIndex = 0;
                            artistTextView.setText(Html.fromHtml(currentTrack.getArtist()));
                            titleTextView.setText(Html.fromHtml(currentTrack.getTitle()));
                            position = 0;
                        } else {
                            artistTextView.setText(Html.fromHtml(artist));
                            titleTextView.setText(Html.fromHtml(title));
                        }
                        Timeline.getInstance().setIndex(currentIndex);
                        if (currentIndex > Timeline.getInstance().getPlaylistTracks().size()) {
                            position = 0;
                        }
                        if (!SharedPreferencesManager.getPreferences().getBoolean("continue_from_position", true)) {
                            position = 0;
                        }

                        Timeline.getInstance().setTimePosition(position);
                        mainActivity.restorePreviousState();
                    }
                });
            }
        });
    }


    @Subscribe
    public void trackInfoEvent(TrackInfoEvent event) {
        Track track = Timeline.getInstance().getCurrentTrack();
        if (track == null) return;
        if (SharedPreferencesManager.getPreferences()
                .getBoolean("scroll_to_current", false)) {
            listView.requestFocusFromTouch();
//            recycler.setSelection(Timeline.getInstance().getIndex());
        }
        Timeline.getInstance().setCurrentAlbum(null);
//        track.setCurrent(true);
        artistTextView.setText(Html.fromHtml(track.getArtist()));
        titleTextView.setText(Html.fromHtml(track.getTitle()));
        List<Integer> playlistItemsIndexesToUpdate = new ArrayList<>(
                Timeline.getInstance().getPreviousTracksIndexes());
        playlistItemsIndexesToUpdate.addAll(Timeline.getInstance().getQueueIndexes());
        Timeline.getInstance().clearPreviousIndexes();
        mainActivity.updateView(playlistItemsIndexesToUpdate);
        if (!NetworkUtils.isOnline()) {
            artistImageView.setImageResource(R.drawable.artist_placeholder);
            albumImageView.setImageDrawable(null);
            albumTextView.setVisibility(View.INVISIBLE);
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
            if (SharedPreferencesManager.getPreferences().getBoolean(
                    Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)) {
                String imageUrl = event.getImageUrl();
                Timeline.getInstance().setCurrentArtistImageUrl(imageUrl);
                imageLoader.displayImage(imageUrl, artistImageView, options);
            }
        }
    }

    @Subscribe
    public void albumInfoEvent(TrackAndAlbumInfoUpdatedEvent event) {
        Album album = event.getAlbum();
        if (album != null && !album.equals(Timeline.getInstance().getPreviousAlbum())) {
            String imageUrl = album.getImageUrl();
            if (imageUrl == null || !SharedPreferencesManager.getPreferences()
                    .getBoolean(Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true)
                    ) {
                albumImageView.setVisibility(View.INVISIBLE);
            } else {
                albumImageView.setVisibility(View.VISIBLE);
                imageLoader.displayImage(imageUrl, albumImageView, options);
            }
            String title = album.getTitle();
            if (title == null) {
                albumTextView.setVisibility(View.INVISIBLE);
            } else {
                albumTextView.setVisibility(View.VISIBLE);
                albumTextView.setText(title);
            }
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
        //todo
//        if (bitmap == null) {
//            backLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono_dark));
//            bottomControlsLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono));
//            return;
//        }
//        Palette.generateAsync(bitmap,
//                new Palette.PaletteAsyncListener() {
//                    @Override
//                    public void onGenerated(Palette palette) {
//                        Palette.Swatch backSwatch =
//                                palette.getDarkMutedSwatch();
//                        if (backSwatch == null) {
//                            backLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono_dark));
//                            return;
//                        }
//                        backLayout.setBackgroundColor(
//                                backSwatch.getRgb());
//
//                        Palette.Swatch bottomSwatch =
//                                palette.getDarkVibrantSwatch();
//                        if (bottomSwatch == null) {
//                            bottomControlsLayout.setBackgroundColor(getResources().getColor(R.color.accent_mono));
//                            return;
//                        }
//                        bottomControlsLayout.setBackgroundColor(
//                                bottomSwatch.getRgb());
//                    }
//                });
    }

    public void updateLoveButton() {
        //todo
    }

    public void playTrack(int position) {
        //todo
    }
}
