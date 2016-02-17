package com.pillowapps.liqear.helpers.home;

import android.support.annotation.NonNull;

import com.pillowapps.liqear.adapters.pagers.PhoneFragmentPagerAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.callbacks.VkPassiveCallback;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.RestoreData;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.helpers.ArtistTrackComparator;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.ButtonStateUtils;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.NetworkManager;
import com.pillowapps.liqear.helpers.PreferencesModel;
import com.pillowapps.liqear.helpers.PreferencesScreenManager;
import com.pillowapps.liqear.helpers.Presenter;
import com.pillowapps.liqear.helpers.StateManager;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.PlaylistModel;
import com.pillowapps.liqear.models.ShareModel;
import com.pillowapps.liqear.models.TrackModel;
import com.pillowapps.liqear.models.TutorialModel;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkWallModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomePresenter extends Presenter<HomeView> {

    protected LastfmLibraryModel libraryModel;
    protected ShareModel shareModel;
    private VkWallModel vkWallModel;
    private VkAudioModel vkAudioModel;
    private Timeline timeline;
    private PreferencesModel preferencesModel;
    private PlaylistModel playlistModel;
    private StateManager stateManager;
    private TutorialModel tutorial;
    private AuthorizationInfoManager authorizationInfoManager;
    private NetworkManager networkManager;
    private ModeItemsHelper modeItemsHelper;
    private PreferencesScreenManager preferencesManager;
    private LastfmTrackModel lastfmTrackModel;


    @Inject
    public HomePresenter(StateManager stateManager, LastfmLibraryModel libraryModel,
                         ShareModel shareModel,
                         VkWallModel vkWallModel,
                         VkAudioModel vkAudioModel,
                         PreferencesModel preferencesModel,
                         PlaylistModel playlistModel,
                         Timeline timeline,
                         TutorialModel tutorial,
                         AuthorizationInfoManager authorizationInfoManager,
                         NetworkManager networkManager,
                         ModeItemsHelper modeItemsHelper,
                         PreferencesScreenManager preferencesManager, LastfmTrackModel lastfmTrackModel) {
        this.stateManager = stateManager;
        this.libraryModel = libraryModel;
        this.shareModel = shareModel;
        this.vkWallModel = vkWallModel;
        this.vkAudioModel = vkAudioModel;
        this.preferencesModel = preferencesModel;
        this.playlistModel = playlistModel;
        this.timeline = timeline;
        this.tutorial = tutorial;
        this.authorizationInfoManager = authorizationInfoManager;
        this.networkManager = networkManager;
        this.modeItemsHelper = modeItemsHelper;
        this.preferencesManager = preferencesManager;
        this.lastfmTrackModel = lastfmTrackModel;
    }

    public void openRadiomix() {
        final HomeView view = view();

        libraryModel.getRadiomix(authorizationInfoManager.getLastfmName(), new SimpleCallback<List<LastfmTrack>>() {
            @Override
            public void success(List<LastfmTrack> tracks) {
                List<Track> trackList = Converter.convertLastfmTrackList(tracks);
                view.showLoading(false);
                playNewPlaylist(0, new Playlist("Radiomix", trackList));
            }

            @Override
            public void failure(String errorMessage) {
                view.showError(errorMessage);
                view.showLoading(false);
            }
        });
    }

    public void openLibrary() {
        final HomeView view = view();

        view.showLoading(true);
        libraryModel.getLibrary(authorizationInfoManager.getLastfmName(), new SimpleCallback<List<LastfmTrack>>() {
            @Override
            public void success(List<LastfmTrack> tracks) {
                List<Track> trackList = Converter.convertLastfmTrackList(tracks);

                view.showLoading(false);
                playNewPlaylist(0, new Playlist("Library", trackList));
            }

            @Override
            public void failure(String errorMessage) {
                view.showError(errorMessage);
                view.showLoading(false);
            }
        });
    }

    public void playTrack(int index, boolean autoplay) {
        timeline.setAutoplay(autoplay);
        final HomeView view = view();

        if (autoplay) {
            view.playTrack(index);
        }
    }

    public void openArtistPhotos() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null || currentTrack.getArtist() == null) return;

        final HomeView view = view();

        if (!networkManager.isOnline()) {
            view.showNoInternetError();
            return;
        }

        view.openArtistPhotosScreen(currentTrack.getArtist());
        // todo according to new Lastfm frontend
    }

    public void openArtistViewer() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null || currentTrack.getArtist() == null) return;

        final HomeView view = view();

        if (!networkManager.isOnline()) {
            view.showNoInternetError();
            return;
        }
        view.openArtistViewer(currentTrack.getArtist());
    }

    public void shareTrack() {
        Track currentTrack = timeline.getCurrentTrack();
        Album currentAlbum = timeline.getCurrentAlbum();

        if (currentTrack == null) {
            return;
        }

        final HomeView view = view();


        String template = preferencesModel.getShareTemplate();

        String shareMessage = shareModel.createShareMessage(currentTrack, timeline.getCurrentAlbum(), template);
        String imageUrl = shareModel.getAlbumImageUrl(currentTrack, currentAlbum);

        view.showShareDialog(shareMessage, imageUrl, currentTrack);
    }

    public void shareTrackToVk(String shareMessage, String imageUrl, Track track) {
        final HomeView view = view();

        if (!networkManager.isOnline()) {
            view.showNoInternetError();
            return;
        }

        if (!authorizationInfoManager.isAuthorizedOnVk()) {
            view.showVkAuthorizationError();
            return;
        }

        vkWallModel.postMessage(shareMessage, imageUrl, track, new VkPassiveCallback());
    }

    public void nextUrl() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) return;


        final HomeView view = view();

        if (currentTrack.isLocal()) {
            view.showTrackIsLocalError();
            return;
        }
        if (!authorizationInfoManager.isAuthorizedOnVk()) {
            view.showVkAuthorizationError();
            return;
        }
        if (!networkManager.isOnline()) {
            view.showNoInternetError();
            return;
        }

        view.openVkAudioSearchForNextUrl(currentTrack);
    }

    public void openLyrics() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) return;

        final HomeView view = view();

        if (!authorizationInfoManager.isAuthorizedOnVk()) {
            view.showVkAuthorizationError();
            return;
        }
        if (!networkManager.isOnline()) {
            view.showNoInternetError();
            return;
        }

        view.openLyricsScreen(currentTrack);
    }

    public void openVideo() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) return;

        final HomeView view = view();
        view.openTrackVideo(currentTrack);
    }

    public void addToVkAudio() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) return;

        final HomeView view = view();

        if (!authorizationInfoManager.isAuthorizedOnVk()) {
            view.showVkAuthorizationError();
            return;
        }
        if (!networkManager.isOnline()) {
            view.showNoInternetError();
            return;
        }

        if (preferencesManager.isVkAddSlow()) {
            view.openAddToVkScreen(currentTrack);
        } else {
            vkAudioModel.addToUserAudioFast(TrackUtils.getNotation(currentTrack), new VkSimpleCallback<VkResponse>() {
                @Override
                public void success(VkResponse data) {
                    view.showToastAdded();
                }

                @Override
                public void failure(VkError error) {
                    view.showError(error.getErrorMessage());
                }
            });
        }
    }

    public void openPreferences() {
        final HomeView view = view();
        view.openPreferences();
    }

    public void openEqualizer() {
        final HomeView view = view();
        view.openEqualizer();
    }

    public void openTimer() {
        final HomeView view = view();
        view.showTimerDialog();
    }

    public void setTimerInSeconds(int minutes) {
        final HomeView view = view();
        view.setTimer(minutes);
    }

    public void shufflePlaylist() {
        List<Track> playlistTracks = timeline.getPlaylistTracks();
        Collections.shuffle(playlistTracks);
    }

    public void clearTitles() {
        List<Track> playlistTracks = timeline.getPlaylistTracks();
        new TrackModel().clearTitles(playlistTracks);

        final HomeView view = view();
        view.updateAdapter();
    }

    public void fixateSearchResult(@NonNull Playlist playlist) {
        timeline.setPlaylist(playlist);
        timeline.clearPreviousIndexes();

        final HomeView view = view();
        view.clearSearch();
        updateMainPlaylist(0, true, playlist);
    }

    public void findCurrentTrack() {
        int currentIndex = timeline.getIndex();
        if (currentIndex >= 0 && currentIndex < timeline.getPlaylistTracks().size()) {
            final HomeView view = view();
            view.setMainPlaylistSelection(currentIndex);
        }
    }

    public void sortByArtist(List<Track> currentTrackList) {
        List<Track> tracks = new ArrayList<>(currentTrackList);
        Collections.sort(tracks, new ArtistTrackComparator());
        updateMainPlaylist(0, false, new Playlist(tracks));
    }

    private void updateMainPlaylist(int indexToPlay, boolean autoPlay, @NonNull Playlist playlist) {
        timeline.clearQueue();
        timeline.clearPreviousIndexes();
        timeline.setPlaylist(playlist);
        timeline.updateRealTrackPositions();
        playlistModel.saveMainPlaylist(timeline.getPlaylist())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();

        HomeView view = view();
        view.changePlaylist(indexToPlay, playlist);
        view.updateEmptyPlaylistTextView();

        if (timeline.getPlaylistTracks().size() > 0) {
            playTrack(indexToPlay, autoPlay);
        }

    }

    public void toggleModeListEditMode() {
        modeItemsHelper.setEditMode(!modeItemsHelper.isEditMode());
        HomeView view = view();
        view.updateModeListEditMode();
    }

    public void togglePlaylistSearchVisibility() {
        boolean visibility = stateManager.toggleSearchVisibility();
        HomeView view = view();
        view.updateSearchVisibility(visibility);
    }

    public void exit() {
        HomeView view = view();
        view.exit();
    }

    public void openPlaylistsScreen() {
        HomeView view = view();
        view.openPlaylistsScreen();
    }

    public void togglePlaylistEditMode() {
        HomeView view = view();
        view.togglePlaylistEditMode();
    }

    public void playNewPlaylist(int positionToPlay, Playlist playlist) {
        HomeView view = view();
        view.changeViewPagerItem(PhoneFragmentPagerAdapter.PLAYLIST_TAB_INDEX);
        updateMainPlaylist(positionToPlay, true, playlist);
    }

    public void changeCurrentTrackUrl(int newPosition, String url) {
        HomeView view = view();
        view.changeCurrentTrackUrl(newPosition, url);
    }

    public void restoreState() {
        HomeView view = view();
        view.updateRepeatButtonState(ButtonStateUtils.getRepeatButtonImage(timeline.getRepeatMode()));
        view.updateShuffleButtonState(ButtonStateUtils.getShuffleButtonImage(timeline.getShuffleMode()));
        view.updateArtistPhotoAndColors(timeline.getCurrentArtistImageUrl());
        view.updatePlayingState(timeline.isPlaying());
        stateManager.getMainPlaylist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(playlist -> {
                    view.updateMainPlaylistTitle(playlist.getTitle());

                    List<Track> tracks = playlist.getTracks();

                    RestoreData restoreData = stateManager.getRestoreData();
                    String artist = restoreData.getArtist();
                    String title = restoreData.getTitle();
                    int currentIndex = restoreData.getCurrentIndex();
                    int position = restoreData.getPosition();

                    boolean currentFits = currentIndex < tracks.size();
                    if (!currentFits) currentIndex = 0;
                    Track currentTrack = tracks.get(currentIndex);
                    boolean tracksEquals = currentFits
                            && (artist + title).equalsIgnoreCase(currentTrack.getArtist()
                            + currentTrack.getTitle());
                    if (!tracksEquals) {
                        view.showArtistPlaceholder();
                        currentIndex = 0;
                        view.updateTrackArtist(currentTrack.getArtist());
                        view.updateTrackTitle(currentTrack.getTitle());
                        position = 0;
                    } else {
                        view.updateTrackArtist(artist);
                        view.updateTrackTitle(title);
                    }
                    timeline.setIndex(currentIndex);
                    if (currentIndex > tracks.size()) {
                        view.showArtistPlaceholder();
                        position = 0;
                    }
                    if (!preferencesManager.isContinueFromLastPositionEnabled()) {
                        position = 0;
                    }
                    timeline.setPosition(position);
                    updateMainPlaylist(currentIndex, false, playlist);
                    view.updateAlbum();
                    view.restoreServiceState();
                });
    }

    public void showTutorial() {
        HomeView view = view();
        if (tutorial.isEnabled()) {
            view.showTutorial();
        }
    }

    public void hideTutorial() {
        HomeView view = view();
        if (tutorial.isEnabled()) {
            tutorial.disableTutorial();
            view.hideTutorial();
        }
    }

    public void updateArtistPhoto() {
        HomeView view = view();
        if (!networkManager.isOnline()) {
            view.showArtistPlaceholder();
            return;
        }
        if (preferencesManager.isDownloadImagesEnabled()) {
            view.updateArtistPhotoAndColors(timeline.getCurrentArtistImageUrl());
        }
    }

    public void updateAlbum() {
        HomeView view = view();

        Album album = timeline.getCurrentAlbum();
        if (!networkManager.isOnline() || album == null) {
            view.hideAlbumImage();
            view.hideAlbumTitle();
            return;
        }

        String imageUrl = album.getImageUrl();
        if (imageUrl == null || !preferencesManager.isDownloadImagesEnabled()) {
            view.hideAlbumImage();
        } else {
            view.showAlbumImage(imageUrl);
        }
        String albumTitle = album.getTitle();
        if (albumTitle == null) {
            view.hideAlbumTitle();
        } else {
            view.showAlbumTitle(albumTitle);
        }
    }

    public void toggleRepeat() {
        timeline.toggleRepeat();
        HomeView view = view();
        view.updateRepeatButtonState(ButtonStateUtils.getRepeatButtonImage(timeline.getRepeatMode()));
        view.updateWidgets();
    }

    public void toggleShuffle() {
        timeline.toggleShuffle();
        HomeView view = view();
        view.updateShuffleButtonState(ButtonStateUtils.getShuffleButtonImage(timeline.getShuffleMode()));
        view.updateWidgets();
    }

    public void restorePlayingState() {
        updateMainPlaylist(timeline.getIndex(), false, timeline.getPlaylist());
    }

    public void toggleLoveForCurrentTrack() {
        HomeView view = view();

        final Track track = timeline.getCurrentTrack();
        if (track == null) {
            return;
        }

        if (!track.isLoved()) {
            lastfmTrackModel.love(track, new SimpleCallback<Object>() {
                @Override
                public void success(Object data) {
                    track.setLoved(true);
                    view.updateLoveButton(ButtonStateUtils.getLoveButtonImage(track));
                    view.showLoading(false);
                }

                @Override
                public void failure(String errorMessage) {
                    view.showLoading(false);

                }
            });
        } else {
            lastfmTrackModel.unlove(track, new SimpleCallback<Object>() {
                @Override
                public void success(Object o) {
                    view.showLoading(false);
                    track.setLoved(false);
                    view.updateLoveButton(ButtonStateUtils.getLoveButtonImage(track));
                }

                @Override
                public void failure(String error) {
                    view.showLoading(false);
                }
            });
        }
    }

    public void openAlbumScreen() {
        HomeView view = view();
        Album album = timeline.getCurrentAlbum();
        if (album == null) {
            return;
        }
        view.openAlbumScreen(album);
    }

    public void updateLove() {
        HomeView view = view();
        Track currentTrack = timeline.getCurrentTrack();
        view.updateLoveButton(ButtonStateUtils.getLoveButtonImage(currentTrack));
    }

    public void updatePlaybackToolbar() {
        HomeView view = view();
        Track currentTrack = timeline.getCurrentTrack();
        view.updatePlaybackTabMenu(ToolbarUtils.getPlaybackToolbarMenuRes(currentTrack));
    }
}
