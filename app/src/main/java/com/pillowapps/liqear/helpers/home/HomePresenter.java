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
import com.pillowapps.liqear.helpers.NetworkManager;
import com.pillowapps.liqear.helpers.PlaylistUtils;
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
import timber.log.Timber;

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
        this.preferencesManager = preferencesManager;
        this.lastfmTrackModel = lastfmTrackModel;
    }

    public void openRadiomix() {
        final HomeView view = view();
        if (view == null) {
            return;
        }

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
        if (view == null) {
            return;
        }

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
        final HomeView view = view();
        if (view == null) {
            return;
        }

        view.playTrack(index, autoplay);
    }

    public void openArtistViewer() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null || currentTrack.getArtist() == null) {
            return;
        }
        openArtistViewer(currentTrack.getArtist());
    }

    public void openArtistViewer(@NonNull String artist) {
        final HomeView view = view();
        if (view == null) {
            return;
        }

        if (!networkManager.isOnline()) {
            view.showNoInternetError();
            return;
        }
        view.openArtistViewer(artist);
    }

    public void shareTrack() {
        Track currentTrack = timeline.getCurrentTrack();
        Album currentAlbum = timeline.getCurrentAlbum();

        if (currentTrack == null) {
            return;
        }

        final HomeView view = view();
        if (view == null) {
            return;
        }


        String template = preferencesModel.getShareTemplate();

        String shareMessage = shareModel.createShareMessage(currentTrack, timeline.getCurrentAlbum(), template);
        String imageUrl = shareModel.getAlbumImageUrl(currentTrack, currentAlbum);

        view.showShareDialog(shareMessage, imageUrl, currentTrack);
    }

    public void shareTrackToVk(String shareMessage, String imageUrl, Track track) {
        final HomeView view = view();
        if (view == null) {
            return;
        }

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
        if (view == null) {
            return;
        }

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
        if (currentTrack == null) {
            return;
        }
        openLyrics(currentTrack);
    }

    public void openLyrics(Track track) {
        final HomeView view = view();
        if (view == null) {
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

        view.openLyricsScreen(track);
    }

    public void openVideo() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) return;

        final HomeView view = view();
        if (view == null) {
            return;
        }
        view.openTrackVideo(currentTrack);
    }

    public void addToVkAudio() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) {
            return;
        }
        addToVkAudio(currentTrack);
    }

    public void addToVkAudio(Track track) {
        final HomeView view = view();
        if (view == null) {
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

        if (preferencesManager.isVkAddSlow()) {
            view.openAddToVkScreen(track);
        } else {
            vkAudioModel.addToUserAudioFast(TrackUtils.getNotation(track), new VkSimpleCallback<VkResponse>() {
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

    public void openEqualizer() {
        final HomeView view = view();
        if (view == null) {
            return;
        }
        view.openEqualizer();
    }

    public void openTimer() {
        final HomeView view = view();
        if (view == null) {
            return;
        }
        view.showTimerDialog();
    }

    public void setTimerInSeconds(int minutes) {
        final HomeView view = view();
        if (view == null) {
            return;
        }
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
        if (view == null) {
            return;
        }
        view.updateAdapter();
    }

    public void fixateSearchResult(@NonNull Playlist playlist) {
        timeline.setPlaylist(playlist);
        timeline.clearPreviousIndexes();

        final HomeView view = view();
        if (view == null) {
            return;
        }
        view.clearSearch();
        playTrack(0, true);
    }

    public void findCurrentTrack() {
        int currentIndex = timeline.getIndex();
        if (currentIndex >= 0 && currentIndex < timeline.getPlaylistTracks().size()) {
            final HomeView view = view();
            if (view == null) {
                return;
            }
            view.setMainPlaylistSelection(currentIndex);
        }
    }

    public void sortByArtist(List<Track> currentTrackList) {
        List<Track> tracks = new ArrayList<>(currentTrackList);
        Collections.sort(tracks, new ArtistTrackComparator());
        updateMainPlaylist(0, new Playlist(tracks));
    }

    private void updateMainPlaylist(int index, @NonNull Playlist playlist) {
        timeline.clearQueue();
        timeline.clearPreviousIndexes();
        timeline.setPlaylist(playlist);
        timeline.updateRealTrackPositions();

        playlistModel.saveMainPlaylist(timeline.getPlaylist())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(aLong -> {
                        },
                        throwable -> {
                            Timber.e(throwable, "Save main playlist error");
                        });

        setMainPlaylist(index, playlist);
    }

    private void setMainPlaylist(int index, @NonNull Playlist playlist) {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.changePlaylist(index, playlist, timeline.getQueueIndexes());
        view.updateEmptyPlaylistTextView();
    }

    public void togglePlaylistSearchVisibility() {
        boolean visibility = stateManager.toggleSearchVisibility();
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.updateSearchVisibility(visibility);
    }

    public void exit() {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.exit();
    }

    public void openPlaylistsScreen() {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.openPlaylistsScreen();
    }

    public void togglePlaylistEditMode() {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.togglePlaylistEditMode();
    }

    public void playNewPlaylist(int positionToPlay, Playlist playlist) {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.changeViewPagerItem(PhoneFragmentPagerAdapter.PLAYLIST_TAB_INDEX);
        updateMainPlaylist(positionToPlay, playlist);
        playTrack(positionToPlay, true);
    }

    public void changeCurrentTrackUrl(int newPosition, String url) {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.changeCurrentTrackUrl(newPosition, url);
    }

    public void restoreState() {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.updateRepeatButtonState(ButtonStateUtils.getRepeatButtonImage(timeline.getRepeatMode()));
        view.updateShuffleButtonState(ButtonStateUtils.getShuffleButtonImage(timeline.getShuffleMode()));
        view.updateArtistPhotoAndColors(timeline.getCurrentArtistImageUrl());
        view.updatePlayingState(timeline.isPlaying());
        view.showLoading(true);
        stateManager.getMainPlaylist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(playlist -> {
                    view.showLoading(false);
                    view.updateMainPlaylistTitle(playlist.getTitle());

                    if (PlaylistUtils.sizeOf(playlist) == 0) {
                        return;
                    }

                    RestoreData restoreData = stateManager.getRestoreData();
                    int restoredIndex = restoreData.getCurrentIndex();
                    int restoredPosition = restoreData.getPosition();

                    if (!preferencesManager.isContinueFromLastPositionEnabled()) {
                        restoredPosition = 0;
                    }
                    timeline.setIndex(restoredIndex);
                    timeline.setPosition(restoredPosition);

                    Track currentTrack = PlaylistUtils.getTrack(playlist, restoredIndex);
                    if (currentTrack == null) {
                        return;
                    }
                    view.updateTrackArtist(currentTrack.getArtist());
                    view.updateTrackTitle(currentTrack.getTitle());
                    view.updateAlbum();

                    updateMainPlaylist(restoredIndex, playlist);

                    view.restoreServiceState();
                }, throwable -> {
                    Timber.e(throwable, "Restore main playlist error");
                });
    }

    public void updateArtistPhoto() {
        HomeView view = view();
        if (view == null) {
            return;
        }
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
        if (view == null) {
            return;
        }

        Album album = timeline.getCurrentAlbum();
        Timber.d("Current album = " + album + ", and networkonline = " + networkManager.isOnline());
        if (!networkManager.isOnline() || album == null) {
            view.hideAlbumImage();
            view.hideAlbumTitle();
            return;
        }

        String imageUrl = album.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty() || !preferencesManager.isDownloadImagesEnabled()) {
            view.hideAlbumImage();
        } else {
            view.showAlbumImage(imageUrl);
        }
        String albumTitle = album.getTitle();
        if (albumTitle == null || albumTitle.isEmpty()) {
            view.hideAlbumTitle();
        } else {
            view.showAlbumTitle(albumTitle);
        }
    }

    public void toggleRepeat() {
        timeline.toggleRepeat();
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.updateRepeatButtonState(ButtonStateUtils.getRepeatButtonImage(timeline.getRepeatMode()));
        view.updateWidgets();
    }

    public void toggleShuffle() {
        timeline.toggleShuffle();
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.updateShuffleButtonState(ButtonStateUtils.getShuffleButtonImage(timeline.getShuffleMode()));
        view.updateWidgets();
    }

    public void restorePlayingState() {
        setMainPlaylist(timeline.getIndex(), timeline.getPlaylist());
    }

    public void toggleLoveForCurrentTrack() {
        final Track track = timeline.getCurrentTrack();
        if (track == null) {
            return;
        }

        if (track.isLoved()) {
            unlove(track);
        } else {
            love(track);
        }
    }

    private void unlove(final Track track) {
        HomeView view = view();
        if (view == null) {
            return;
        }
        lastfmTrackModel.unlove(track, new SimpleCallback<Object>() {
            @Override
            public void success(Object o) {
                track.setLoved(false);
                view.showLoading(false);
                view.updateLoveButton(ButtonStateUtils.getLoveButtonImage(track));
            }

            @Override
            public void failure(String error) {
                view.showLoading(false);
            }
        });
    }

    public void love(Track track) {
        HomeView view = view();
        if (view == null) {
            return;
        }
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
    }

    public void openAlbumScreen() {
        HomeView view = view();
        if (view == null) {
            return;
        }
        Album album = timeline.getCurrentAlbum();
        if (album == null) {
            return;
        }
        view.openAlbumScreen(album);
    }

    public void updateLove() {
        HomeView view = view();
        if (view == null) {
            return;
        }
        Track currentTrack = timeline.getCurrentTrack();
        view.updateLoveButton(ButtonStateUtils.getLoveButtonImage(currentTrack));
    }

    public void addTrackToPlaylist(Track track) {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.openAddTrackToPlaylistScreen(track);
    }

    public void queueTrack(int index) {
        timeline.queueTrack(index);
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.updateAdapter();
    }

    public void removeTrackFromPlaylist(int index) {
        timeline.removeTrack(index);
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.updateAdapter();
    }

    public void addTo(Track track) {
        HomeView view = view();
        if (view == null) {
            return;
        }
        view.showAddToDialog(track);
    }
}
