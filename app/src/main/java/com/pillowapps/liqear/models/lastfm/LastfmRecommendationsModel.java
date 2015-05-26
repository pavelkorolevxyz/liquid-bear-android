package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.LastfmTracks;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.callbacks.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class LastfmRecommendationsModel {

    public void getRecommendationsTracks(List<Artist> artists, final SimpleCallback<List<LastfmTrack>> callback) {
        if (artists == null || artists.size() == 0) callback.success(new ArrayList<LastfmTrack>(0));

        List<Observable<LastfmTopTracksRoot>> observableList = new ArrayList<>(artists.size());
        LastfmArtistModel lastfmArtistModel = new LastfmArtistModel();
        for (Artist artist : artists) {
            Observable<LastfmTopTracksRoot> topTracksRootObservable = lastfmArtistModel
                    .getArtistTopTracks(artist, 5, 0);
            observableList.add(topTracksRootObservable);
        }
        Observable.zip(observableList, new FuncN<List<LastfmTrack>>() {
            @Override
            public List<LastfmTrack> call(Object... topTracksRoots) {
                List<LastfmTrack> tracks = new ArrayList<>();
                for (Object arg : topTracksRoots) {
                    if (arg == null) continue;
                    if (arg instanceof LastfmTopTracksRoot) {
                        LastfmTopTracksRoot topTracks = (LastfmTopTracksRoot) arg;
                        LastfmTracks topTracksObject = topTracks.getTracks();
                        if (topTracksObject == null) continue;
                        List<LastfmTrack> lastfmTracks = topTracksObject.getTracks();
                        if (lastfmTracks == null) continue;
                        tracks.addAll(lastfmTracks);
                    }
                }
                return tracks;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<LastfmTrack>>() {
                    @Override
                    public void call(List<LastfmTrack> tracks) {
                        callback.success(tracks);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        callback.failure(throwable.getMessage());
                    }
                });
    }

}
