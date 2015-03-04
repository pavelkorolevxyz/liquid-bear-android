package com.pillowapps.liqear.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activity.AlbumViewerActivity;
import com.pillowapps.liqear.activity.ArtistViewerActivity;
import com.pillowapps.liqear.activity.MainActivity;
import com.pillowapps.liqear.audio.AudioTimeline;
import com.pillowapps.liqear.audio.MusicPlaybackService;
import com.pillowapps.liqear.audio.RepeatMode;
import com.pillowapps.liqear.audio.ShuffleMode;
import com.pillowapps.liqear.components.ActivitySwipeDetector;
import com.pillowapps.liqear.components.ModeListAdapter;
import com.pillowapps.liqear.global.Config;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PlaylistItemsAdapter;
import com.pillowapps.liqear.helpers.PlaylistManager;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Track;

import java.util.ArrayList;
import java.util.List;

public class TabletFragment extends Fragment {
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
    private MainActivity mainActivity;
    private DragSortListView listView;
    private ServiceBroadcastReceiver receiver;
    private EditText searchPlaylistEditText;
    private ImageButton clearEditTextButton;
    private ImageView artistImageView;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisc()
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
        View v = inflater.inflate(R.layout.tablet_fragment_layout, null);
        mainActivity = (MainActivity) getActivity();
        initUi(v);
        initListeners();
        restorePreviousState();
        return v;
    }

    private void initUi(View v) {
        listView = (DragSortListView) v.findViewById(R.id.playlist_list_view_playlist_tab);
        listView.setAdapter(mainActivity.getPlaylistItemsAdapter());
        searchPlaylistEditText = (EditText) v.findViewById(R.id.search_edit_text_playlist_tab);
        searchPlaylistEditText.setVisibility(PreferencesManager.getSavePreferences()
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
    public void onSaveInstanceState(Bundle outState) {
        saveState();
        super.onSaveInstanceState(outState);
    }

    private void initListeners() {
        artistTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AudioTimeline.hasCurrentTrack()) return;
                Intent artistInfoIntent = new Intent(mainActivity, ArtistViewerActivity.class);
                artistInfoIntent.putExtra(ArtistViewerActivity.ARTIST,
                        AudioTimeline.getCurrentTrack().getArtist());
                mainActivity.startActivityForResult(artistInfoIntent,
                        Constants.MAIN_REQUEST_CODE);
            }
        });

        View.OnClickListener albumClickListener = new

                View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mainActivity, AlbumViewerActivity.class);
                        Album album = AudioTimeline.getAlbum();
                        if (album == null) return;
                        intent.putExtra(AlbumViewerActivity.ALBUM, album.getTitle());
                        intent.putExtra(AlbumViewerActivity.ARTIST, album.getArtist());
                        mainActivity.startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
                    }
                };
        albumImageView.setOnClickListener(albumClickListener);
        albumTextView.setOnClickListener(albumClickListener);

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.playlist_list_view_playlist_tab) {
                    AdapterView.AdapterContextMenuInfo info =
                            (AdapterView.AdapterContextMenuInfo) menuInfo;
                    menu.setHeaderTitle(((Track) listView.getAdapter()
                            .getItem(info.position)).getTitle());
                    String[] menuItems = getResources().getStringArray(R.array.playlist_item_menu);
                    for (int i = 0; i < menuItems.length; i++) {
                        menu.add(android.view.Menu.NONE, i, i, menuItems[i]);
                    }
                }
            }
        });
        listView.setClickable(true);
        listView.setFocusable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Track> playlist = AudioTimeline.getPlaylist();
                Track track = playlist.get(mainActivity.getPlaylistItemsAdapter()
                        .getItem(position).getRealPosition());
                if (mainActivity.getPlaylistItemsAdapter().isEditMode()) {
                    mainActivity.showRenameDialog(track, position);
                } else {
                    artistTextView.setText(Html.fromHtml(track.getArtist()));
                    titleTextView.setText(Html.fromHtml(track.getTitle()));
                    playPauseButton.setImageResource(R.drawable.pause_button_states);
                    mainActivity.getMusicPlaybackService().setPlayOnPrepared(true);
                    mainActivity.getMusicPlaybackService().play(track.getRealPosition());
                }
            }
        });
        listView.setRemoveListener(new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                mainActivity.removeTrack(which);
            }
        });
        listView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from == to) return;
                List<Track> playlist = AudioTimeline.getPlaylist();
                int currentIndex = AudioTimeline.getCurrentIndex();
                if (from == currentIndex) {
                    AudioTimeline.setCurrentIndex(to);
                } else if (from < to && currentIndex < to && currentIndex > from) {
                    AudioTimeline.setCurrentIndex(currentIndex - 1);
                } else if (from > to && currentIndex < from && currentIndex > to) {
                    AudioTimeline.setCurrentIndex(currentIndex + 1);
                } else if (to == currentIndex && currentIndex > from) {
                    AudioTimeline.setCurrentIndex(currentIndex - 1);
                } else if (to == currentIndex && currentIndex < from) {
                    AudioTimeline.setCurrentIndex(currentIndex + 1);
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
                PlaylistItemsAdapter playlistItemsAdapter =
                        mainActivity.getPlaylistItemsAdapter();
                if (playlistItemsAdapter != null) {
                    playlistItemsAdapter.getFilter().filter(s);
                }
            }
        });
        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(mainActivity);
        blackView.setOnTouchListener(activitySwipeDetector);
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
            timeDurationTextView.setText(Utils.secondsToString(duration / 1000));
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
        editor.commit();
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
        editor.commit();
    }

    public void setServiceConnected() {
        receiver = new ServiceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.ACTION_SERVICE);
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
            playPauseButton.setImageResource(R.drawable.pause_button_states);
        }
    }

    public ListView getPlaylistListView() {
        return listView;
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
        ModeListAdapter adapter = mainActivity.getModeListFragment().getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void clearFilter() {
        searchPlaylistEditText.setText("");
    }

    public void updateSearchVisibility() {
        searchPlaylistEditText.setVisibility(PreferencesManager.getSavePreferences()
                .getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false) ?
                View.VISIBLE : View.GONE);
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
        AudioTimeline.setCurrentIndex(currentIndex);
        if (currentIndex > AudioTimeline.getPlaylist().size()) {
            position = 0;
        }
        if (!PreferencesManager.getPreferences().getBoolean("continue_from_position", true)) {
            position = 0;
        }

        AudioTimeline.setCurrentPosition(position);
        mainActivity.restorePreviousState();
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
                        listView.requestFocusFromTouch();
                        listView.setSelection(AudioTimeline.getCurrentIndex());
                    }
                    AudioTimeline.setCurrentAlbum(null);
                    track.setCurrent(true);
                    artistTextView.setText(Html.fromHtml(track.getArtist()));
                    titleTextView.setText(Html.fromHtml(track.getTitle()));
                    List<Integer> listToUpdate = new ArrayList<Integer>(
                            AudioTimeline.getPrevClickedItems());
                    listToUpdate.addAll(AudioTimeline.getQueue());
                    AudioTimeline.clearPrevClickedItems();
                    mainActivity.updateView(listToUpdate);
                    if (!Utils.isOnline()) {
                        artistImageView.setImageResource(R.drawable.artist_placeholder);
                        albumImageView.setImageDrawable(null);
                        albumTextView.setVisibility(View.INVISIBLE);
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
                        imageLoader.displayImage(imageUrl, artistImageView, options);
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
                        if (imageUrl == null || !PreferencesManager.getPreferences()
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
}
