package com.pillowapps.liqear.helpers.home;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.callbacks.VkPassiveCallback;
import com.pillowapps.liqear.callbacks.VkSimpleCallback;
import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.vk.VkError;
import com.pillowapps.liqear.entities.vk.VkResponse;
import com.pillowapps.liqear.helpers.ArtistTrackComparator;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.LBPreferencesManager;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.Presenter;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.ShareModel;
import com.pillowapps.liqear.models.TrackModel;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkWallModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class HomePresenter extends Presenter<HomeView> {

    protected LastfmLibraryModel libraryModel;
    protected ShareModel shareModel;
    private VkWallModel vkWallModel;
    private VkAudioModel vkAudioModel;
    private Timeline timeline;


    @Inject
    public HomePresenter(LastfmLibraryModel libraryModel,
                         ShareModel shareModel,
                         VkWallModel vkWallModel,
                         VkAudioModel vkAudioModel,
                         Timeline timeline) {
        this.libraryModel = libraryModel;
        this.shareModel = shareModel;
        this.vkWallModel = vkWallModel;
        this.vkAudioModel = vkAudioModel;
        this.timeline = timeline;
    }

    public void setMusicServiceConnected() {

    }

    public void openRadiomix() {
        final HomeView view = view();

        libraryModel.getRadiomix(AuthorizationInfoManager.getLastfmName(),
                new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> tracks) {
                        int index = 0;
                        List<Track> trackList = Converter.convertLastfmTrackList(tracks);
                        timeline.setPlaylist(new Playlist(trackList));

                        view.changeViewPagerItem(0);
                        view.updateAdapter();
                        view.changePlaylist(index, true);
                        view.showLoading(false);
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
        libraryModel.getLibrary(AuthorizationInfoManager.getLastfmName(),
                new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> tracks) {
                        int index = 0;
                        List<Track> trackList = Converter.convertLastfmTrackList(tracks);
                        timeline.setPlaylist(new Playlist(trackList));

                        view.changeViewPagerItem(0);
                        view.updateAdapter();
                        view.changePlaylist(index, true);
                        view.showLoading(false);
                    }

                    @Override
                    public void failure(String errorMessage) {
                        view.showError(errorMessage);
                        view.showLoading(false);
                    }
                });
    }

    public void playTrack(int index) {
        timeline.setStartPlayingOnPrepared(true);
        final HomeView view = view();

        view.playTrack(index);
    }

    public void openArtistPhotos() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null || currentTrack.getArtist() == null) return;

        final HomeView view = view();

        if (!NetworkUtils.isOnline()) {
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

        if (!NetworkUtils.isOnline()) {
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


        String template = SharedPreferencesManager.getPreferences().getString(Constants.SHARE_FORMAT,
                LBApplication.getAppContext().getString(R.string.listening_now));

        String shareMessage = shareModel.createShareMessage(currentTrack, timeline.getCurrentAlbum(), template);

        String imageUrl = shareModel.getAlbumImageUrl(currentTrack, currentAlbum);

        view.showShareDialog(shareMessage, imageUrl, currentTrack);
    }

    public void shareTrackToVk(String shareMessage, String imageUrl, Track track) {
        final HomeView view = view();

        if (!NetworkUtils.isOnline()) {
            view.showNoInternetError();
            return;
        }

        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
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
        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            view.showVkAuthorizationError();
            return;
        }
        if (!NetworkUtils.isOnline()) {
            view.showNoInternetError();
            return;
        }

        view.openVkAudioSearchForNextUrl(currentTrack);
    }

    public void openLyrics() {
        Track currentTrack = timeline.getCurrentTrack();
        if (currentTrack == null) return;

        final HomeView view = view();

        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            view.showVkAuthorizationError();
            return;
        }
        if (!NetworkUtils.isOnline()) {
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

        if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
            view.showVkAuthorizationError();
            return;
        }
        if (!NetworkUtils.isOnline()) {
            view.showNoInternetError();
            return;
        }

        if (LBPreferencesManager.isVkAddSlow()) {
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

    public void setTimerInSeconds(int seconds) {
        // todo
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

        view.updateAdapter();
        view.clearSearch();
        view.changePlaylist(0, true);
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
        timeline.setPlaylist(new Playlist(tracks));

        HomeView view = view();
        view.updateAdapter();
        view.changePlaylist(0, true); //todo not changing playlist immediately
    }

    public void toggleModeListEditMode() {
        ModeItemsHelper.setEditMode(!ModeItemsHelper.isEditMode());
        HomeView view = view();

        view.updateModeListEditMode();
    }

    public void togglePlaylistSearchVisibility() {
        SharedPreferences savePreferences = SharedPreferencesManager.getSavePreferences();
        boolean visibility = !savePreferences.getBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, false);
        savePreferences.edit().putBoolean(Constants.SEARCH_PLAYLIST_VISIBILITY, visibility).apply(); // todo move shared preferences to interactor layer

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
}
