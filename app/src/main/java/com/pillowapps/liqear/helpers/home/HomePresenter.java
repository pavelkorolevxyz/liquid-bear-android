package com.pillowapps.liqear.helpers.home;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.Presenter;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;

import java.util.List;

import javax.inject.Inject;

public class HomePresenter extends Presenter<HomeView> {

    protected LastfmLibraryModel libraryModel;

    @Inject
    public HomePresenter(LastfmLibraryModel libraryModel) {
        this.libraryModel = libraryModel;
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

}
