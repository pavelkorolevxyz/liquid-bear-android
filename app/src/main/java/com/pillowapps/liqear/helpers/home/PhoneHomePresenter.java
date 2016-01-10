package com.pillowapps.liqear.helpers.home;

import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Playlist;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;

import java.util.List;

public class PhoneHomePresenter extends HomePresenter {

    public PhoneHomePresenter(HomeView view) {
        this.view = view;
    }

    @Override
    public void init() {

    }

    @Override
    public void setMusicServiceConnected() {

    }

    @Override
    public void openRadiomix() {
        view.showLoading(true);
        new LastfmLibraryModel().getRadiomix(AuthorizationInfoManager.getLastfmName(),
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

    @Override
    public void openLibrary() {
        view.showLoading(true);
        new LastfmLibraryModel().getLibrary(AuthorizationInfoManager.getLastfmName(),
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

    @Override
    public void playTrack(int index) {
        Timeline.getInstance().setStartPlayingOnPrepared(true);
        view.playTrack(index);
    }

}
