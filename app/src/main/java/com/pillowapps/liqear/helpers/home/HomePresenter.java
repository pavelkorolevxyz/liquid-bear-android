package com.pillowapps.liqear.helpers.home;

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
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.LBPreferencesManager;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.Presenter;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.models.ShareModel;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.pillowapps.liqear.models.vk.VkWallModel;

import java.util.List;

import javax.inject.Inject;

public class HomePresenter extends Presenter<HomeView> {

    protected LastfmLibraryModel libraryModel;
    protected ShareModel shareModel;
    private VkWallModel vkWallModel;
    private VkAudioModel vkAudioModel;

    @Inject
    public HomePresenter(LastfmLibraryModel libraryModel,
                         ShareModel shareModel,
                         VkWallModel vkWallModel,
                         VkAudioModel vkAudioModel) {
        this.libraryModel = libraryModel;
        this.shareModel = shareModel;
        this.vkWallModel = vkWallModel;
        this.vkAudioModel = vkAudioModel;
    }

    public void setMusicServiceConnected() {

    }

    public void openRadiomix() {
        final HomeView view = view();

        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

        libraryModel.getRadiomix(AuthorizationInfoManager.getLastfmName(),
                new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> tracks) {
                        int index = 0;
                        List<Track> trackList = Converter.convertLastfmTrackList(tracks);
                        Timeline.getInstance().setPlaylist(new Playlist(trackList));

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

        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

        view.showLoading(true);
        libraryModel.getLibrary(AuthorizationInfoManager.getLastfmName(),
                new SimpleCallback<List<LastfmTrack>>() {
                    @Override
                    public void success(List<LastfmTrack> tracks) {
                        int index = 0;
                        List<Track> trackList = Converter.convertLastfmTrackList(tracks);
                        Timeline.getInstance().setPlaylist(new Playlist(trackList));

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
        Timeline.getInstance().setStartPlayingOnPrepared(true);
        final HomeView view = view();

        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }
        view.playTrack(index);
    }

    public void openArtistPhotos() {
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null || currentTrack.getArtist() == null) return;

        final HomeView view = view();
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

        if (!NetworkUtils.isOnline()) {
            view.showNoInternetError();
            return;
        }

        view.openArtistPhotosScreen(currentTrack.getArtist());
        // todo according to new Lastfm frontend
    }

    public void openArtistViewer() {
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null || currentTrack.getArtist() == null) return;

        final HomeView view = view();
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

        if (!NetworkUtils.isOnline()) {
            view.showNoInternetError();
            return;
        }
        view.openArtistViewer(currentTrack.getArtist());
    }

    public void shareTrack(String template) {
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        Album currentAlbum = Timeline.getInstance().getCurrentAlbum();

        if (currentTrack == null) {
            return;
        }

        final HomeView view = view();
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

        String shareMessage = shareModel.createShareMessage(currentTrack, template);

        String imageUrl = shareModel.getAlbumImageUrl(currentTrack, currentAlbum);

        view.showShareDialog(shareMessage, imageUrl, currentTrack);
    }

    public void shareTrackToVk(String shareMessage, String imageUrl, Track track) {
        final HomeView view = view();
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

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
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;


        final HomeView view = view();
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }


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
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;

        final HomeView view = view();
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

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
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;

        final HomeView view = view();
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

        view.openTrackVideo(currentTrack);
    }

    public void addToVkAudio() {
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        if (currentTrack == null) return;

        final HomeView view = view();
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }

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
}
