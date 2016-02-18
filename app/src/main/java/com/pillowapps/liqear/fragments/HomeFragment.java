package com.pillowapps.liqear.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.michaelnovakjr.numberpicker.NumberPicker;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.HomeActivity;
import com.pillowapps.liqear.activities.ImagePagerActivity;
import com.pillowapps.liqear.activities.TextActivity;
import com.pillowapps.liqear.activities.modes.PlaylistsActivity;
import com.pillowapps.liqear.activities.modes.VkAudioSearchActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmAlbumViewerActivity;
import com.pillowapps.liqear.activities.modes.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.activities.preferences.EqualizerActivity;
import com.pillowapps.liqear.activities.preferences.PreferencesActivity;
import com.pillowapps.liqear.adapters.PlaylistItemsAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.fragments.base.BaseFragment;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.MusicServiceManager;
import com.pillowapps.liqear.helpers.NetworkManager;
import com.pillowapps.liqear.helpers.PreferencesModel;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.helpers.SavesManager;
import com.pillowapps.liqear.helpers.StateManager;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.helpers.home.HomePresenter;
import com.pillowapps.liqear.helpers.home.HomeView;
import com.pillowapps.liqear.models.ImageModel;
import com.pillowapps.liqear.models.PlaylistModel;
import com.pillowapps.liqear.models.ShareModel;
import com.pillowapps.liqear.models.TutorialModel;
import com.pillowapps.liqear.models.VideoModel;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkWallModel;

import java.util.LinkedList;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

public abstract class HomeFragment extends BaseFragment implements HomeView {

    protected HomeActivity activity;

    protected ProgressBar mainProgressBar;

    protected PlaylistItemsAdapter playlistItemsAdapter;

    protected Menu mainMenu;

    @Inject
    HomePresenter presenter;

    @Inject
    StateManager stateManager;

    @Inject
    VkAudioModel vkAudioModel;
    @Inject
    LastfmTrackModel lastfmTrackModel;
    @Inject
    PlaylistModel playlistModel;

    @Inject
    MusicServiceManager musicServiceManager;

    @Inject
    ImageModel imageModel;

    @Inject
    AuthorizationInfoManager authorizationInfoManager;
    @Inject
    NetworkManager networkManager;

    @Inject
    ModeItemsHelper modeItemsHelper;

    @Inject
    SavesManager savesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(getContext()).applicationComponent().plus(new HomeFragmentModule()).inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        activity = (HomeActivity) getActivity();

        playlistItemsAdapter = new PlaylistItemsAdapter(getContext());

        presenter.bindView(this);

        musicServiceManager.startServiceAsync(activity, () -> {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            presenter.restorePlayingState();
            musicServiceManager.restore();
        });

        LBApplication.BUS.register(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        LBApplication.BUS.unregister(this);
        stateManager.savePlaylistState(musicServiceManager.getService());
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.photo_artist_button: {
                presenter.openArtistPhotos();
                break;
            }
            case R.id.show_artist_button: {
                presenter.openArtistViewer();
                break;
            }
            case R.id.share_track_button: {
                presenter.shareTrack();
                break;
            }
            case R.id.next_url_button: {
                presenter.nextUrl();
                break;
            }
            case R.id.lyrics_button: {
                presenter.openLyrics();
                break;
            }
            case R.id.youtube_video_button: {
                presenter.openVideo();
                break;
            }
            case R.id.add_to_vk_button: {
                presenter.addToVkAudio();
                break;
            }
            case R.id.settings: {
                presenter.openPreferences();
                break;
            }
            case R.id.equalizer: {
                presenter.openEqualizer();
                break;
            }
            case R.id.timer_button: {
                presenter.openTimer();
                break;
            }
            case R.id.exit_button: {
                presenter.exit();
                break;
            }
            case R.id.playlists_button: {
                presenter.openPlaylistsScreen();
                break;
            }
            case R.id.clean_titles: {
                presenter.clearTitles();
                break;
            }
            case R.id.shuffle_button: {
                presenter.shufflePlaylist();
                break;
            }
            case R.id.playlist_edit_mode_button: {
                presenter.togglePlaylistEditMode();
                break;
            }
            case R.id.fixate_search_result_button: {
                presenter.fixateSearchResult(new Playlist(playlistItemsAdapter.getValues()));
                break;
            }
            case R.id.find_current_button: {
                presenter.findCurrentTrack();
                break;
            }
            case R.id.sort_by_artist_button: {
                presenter.sortByArtist(playlistItemsAdapter.getValues());
                break;
            }
            case R.id.edit_modes_button: {
                presenter.toggleModeListEditMode();
                break;
            }
            case R.id.menu_search: {
                presenter.togglePlaylistSearchVisibility();
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Track track = playlistItemsAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.add_to_playlist_track_menu_item:
                presenter.addTrackToPlaylist(track);
                break;
            case R.id.lyrics_track_menu_item:
                presenter.openLyrics(track);
                break;
            case R.id.add_to_queue_track_menu_item:
                presenter.queueTrack(track.getRealPosition());
                break;
            case R.id.show_artist_track_menu_item:
                presenter.openArtistViewer(track.getArtist());
                break;
            case R.id.remove_from_list_track_menu_item:
                presenter.removeTrackFromPlaylist(track.getRealPosition());
                break;
            case R.id.add_to_track_menu_item:
                presenter.addTo(track);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == Constants.MAIN_REQUEST_CODE) {
            int positionToPlay = data.getIntExtra(Constants.POSITION_TO_PLAY, 0);
            presenter.playNewPlaylist(positionToPlay, data.getParcelableExtra("playlist"));
        } else if (requestCode == Constants.VK_ADD_REQUEST_CODE) {
            int position = data.getIntExtra("position", 0);
            String url = data.getStringExtra("url");
            presenter.changeCurrentTrackUrl(position, url);
        }
    }

    @Override
    public void openAddTrackToPlaylistScreen(Track track) {
        Intent intent = PlaylistsActivity.startIntent(getContext(), PlaylistsActivity.Intention.ADD_TO_PLAYLIST);
        intent.putExtra(PlaylistsActivity.TRACK, track);
        startActivity(intent);
    }

    @Override
    public void showAddToDialog(Track track) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.choose_target)
                .setItems(R.array.add_targets, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    presenter.love(track);
                                    break;
                                case 1:
                                    presenter.addToVkAudio(track);
                                    break;
                            }
                        }
                ).show();
    }

    @Override
    public void exit() {
        musicServiceManager.stopService(getContext());
        activity.finish();
    }

    @Override
    public void showLoading(boolean loading) {
        mainProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateAdapter() {
        playlistItemsAdapter.notifyDataSetChanged();
    }

    @Override
    public void changePlaylist(int index, Playlist playlist, LinkedList<Integer> queueIndexes) {
        playlistItemsAdapter.setValues(playlist.getTracks());
        playlistItemsAdapter.setCurrentIndex(index);
        playlistItemsAdapter.setQueue(queueIndexes);
        playlistItemsAdapter.notifyDataSetChanged();
        updateToolbars();
    }

    protected void updateToolbars() {
        // No op.
    }

    public void restoreState() {
        presenter.restoreState();
    }

    @Override
    public void restoreServiceState() {
        musicServiceManager.restore();
    }

    @Override
    public void showError(String errorMessage) {
        ErrorNotifier.showError(activity, errorMessage);
    }

    @Override
    public abstract void updateEmptyPlaylistTextView();

    public void openRadiomix() {
        presenter.openRadiomix();
    }

    public void openLibrary() {
        presenter.openLibrary();
    }

    public void openDropButton() {
        if (mainMenu == null) {
            return;
        }
        mainMenu.performIdentifierAction(R.id.track_button, 0);
    }

    public void toggleLoveCurrentTrack() {
        if (!authorizationInfoManager.isAuthorizedOnLastfm()) {
            toast(R.string.last_fm_not_authorized);
            return;
        }
        if (!networkManager.isOnline()) {
            toast(R.string.no_internet);
            return;
        }
        showLoading(true);
        presenter.toggleLoveForCurrentTrack();
    }

    public void updateLoveButton(boolean loved) {
        // No operations.
    }

    @Override
    public void showNoInternetError() {
        toast(R.string.no_internet);
    }

    @Override
    public void openArtistPhotosScreen(String artist) {
        Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.ARTIST, artist);
        startActivity(intent);
    }

    @Override
    public void openArtistViewer(String artist) {
        Intent intent = new Intent(getActivity(), LastfmArtistViewerActivity.class);
        intent.putExtra(LastfmArtistViewerActivity.ARTIST, artist);
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    @Override
    public void showShareDialog(String shareMessage, String imageUrl, Track track) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.share_track)
                .create();

        View shareDialogView = View.inflate(getContext(), R.layout.share_dialog_layout, null);

        shareDialogView.findViewById(R.id.vk_button).setOnClickListener(v -> {
            presenter.shareTrackToVk(shareMessage, imageUrl, track);
            dialog.dismiss();
        });

        shareDialogView.findViewById(R.id.other_button).setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_track)));
            dialog.dismiss();
        });

        shareDialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.setView(shareDialogView);

        dialog.show();
    }

    @Override
    public void showVkAuthorizationError() {
        toast(R.string.vk_not_authorized);
    }

    @Override
    public void showTrackIsLocalError() {
        toast(R.string.track_local);
    }

    @Override
    public void openVkAudioSearchForNextUrl(Track currentTrack) {
        Intent intent = VkAudioSearchActivity.getStartIntent(getContext(), VkAudioSearchActivity.CHOOSE_URL_PURPOSE);
        intent.putExtra(Constants.TARGET, TrackUtils.getNotation(currentTrack));
        startActivityForResult(intent, Constants.VK_ADD_REQUEST_CODE);
    }

    @Override
    public void openLyricsScreen(Track track) {
        Intent intent = TextActivity.startIntent(getContext());
        intent.putExtra(TextActivity.INTENTION, TextActivity.Aim.LYRICS);
        intent.putExtra("artist", track.getArtist());
        intent.putExtra("title", track.getTitle());
        startActivity(intent);
    }

    @Override
    public void openTrackVideo(Track track) {
        musicServiceManager.pause();
        new VideoModel().openVideo(getActivity(), track);
    }

    @Override
    public void openAddToVkScreen(Track track) {
        Intent intent = VkAudioSearchActivity.getStartIntent(getContext(), VkAudioSearchActivity.ADD_TO_VK_PURPOSE);
        intent.putExtra(Constants.TARGET, TrackUtils.getNotation(track));
        startActivity(intent);
    }

    @Override
    public void showToastAdded() {
        toast(R.string.added);
    }

    @Override
    public void openPreferences() {
        startActivity(PreferencesActivity.getStartIntent(getContext()));
    }

    @Override
    public void openEqualizer() {
        startActivity(EqualizerActivity.startIntent(getContext()));
    }

    @Override
    public void showTimerDialog() {
        View layout = View.inflate(getContext(), R.layout.seekbar_layout, null);
        final NumberPicker sb = (NumberPicker) layout.findViewById(R.id.minutes_picker);
        sb.setRange(1, 1440);
        int timerDefault = savesManager.getTimerDefault();
        sb.setCurrent(timerDefault);

        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.timer)
                .customView(layout, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
            presenter.setTimerInSeconds(sb.getCurrent());
            musicServiceManager.setTimer(sb.getCurrent() * 60);
            savesManager.saveTimerDefault(sb.getCurrent());
        });
        dialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(v -> {
            musicServiceManager.setTimer(0);
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public abstract void clearSearch();

    @Override
    public abstract void setMainPlaylistSelection(int currentIndex);

    @Override
    public abstract void updateModeListEditMode();

    @Override
    public abstract void updateSearchVisibility(boolean visibility);

    @Override
    public void openPlaylistsScreen() {
        startActivityForResult(PlaylistsActivity.startIntent(getContext(), PlaylistsActivity.Intention.SHOW_PLAYLISTS), Constants.MAIN_REQUEST_CODE);
    }

    @Override
    public void changeCurrentTrackUrl(int newPosition, String url) {
        musicServiceManager.changeCurrentTrackUrl(newPosition, url);
    }

    @Override
    public void togglePlaylistEditMode() {
        playlistItemsAdapter.toggleEditMode();
    }

    @Override
    public void updateWidgets() {
        musicServiceManager.updateWidgets();
    }

    @Override
    public void setTimer(int minutes) {
        musicServiceManager.setTimer(minutes);
    }

    @Override
    public void openAlbumScreen(@NonNull Album album) {
        Intent intent = new Intent(activity, LastfmAlbumViewerActivity.class);
        intent.putExtra(LastfmAlbumViewerActivity.ALBUM, album.getTitle());
        intent.putExtra(LastfmAlbumViewerActivity.ARTIST, album.getArtist());
        startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
    }

    @Subcomponent(modules = HomeFragmentModule.class)
    public interface HomeFragmentComponent {
        void inject(@NonNull HomeFragment itemsFragment);
    }

    @Module
    public static class HomeFragmentModule {

        @Provides
        @NonNull
        public HomePresenter provideHomePresenter(@NonNull StateManager stateManager,
                                                  @NonNull LastfmLibraryModel libraryModel,
                                                  @NonNull ShareModel shareModel,
                                                  @NonNull VkWallModel vkWallModel,
                                                  @NonNull VkAudioModel vkAudioModel,
                                                  @NonNull PreferencesModel preferencesModel,
                                                  @NonNull PlaylistModel playlistModel,
                                                  @NonNull Timeline timeline,
                                                  @NonNull TutorialModel tutorial,
                                                  @NonNull AuthorizationInfoManager authorizationInfoManager,
                                                  @NonNull NetworkManager networkManager,
                                                  @NonNull ModeItemsHelper modeItemsHelper,
                                                  @NonNull PreferencesScreenManager preferencesManager,
                                                  @NonNull LastfmTrackModel lastfmTrackModel) {
            return new HomePresenter(stateManager, libraryModel, shareModel, vkWallModel, vkAudioModel, preferencesModel, playlistModel, timeline, tutorial,
                    authorizationInfoManager, networkManager, modeItemsHelper, preferencesManager, lastfmTrackModel);
        }
    }
}
