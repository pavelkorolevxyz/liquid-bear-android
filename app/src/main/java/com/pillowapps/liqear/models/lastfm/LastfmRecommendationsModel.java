package com.pillowapps.liqear.models.lastfm;

import android.content.Context;

import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.LastfmTracks;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LastfmRecommendationsModel {

    @Inject
    LastfmArtistModel lastfmArtistModel;

    public LastfmRecommendationsModel(Context context) {
        LBApplication.get(context).applicationComponent().inject(this);
    }

    public void getRecommendationsTracks(List<Artist> artists, final SimpleCallback<List<LastfmTrack>> callback) {
        if (artists == null || artists.size() == 0) {
            callback.success(new ArrayList<>(0));
            return;
        }

        List<Observable<LastfmTopTracksRoot>> observableList = new ArrayList<>(artists.size());
        for (Artist artist : artists) {
            Observable<LastfmTopTracksRoot> topTracksRootObservable = lastfmArtistModel
                    .getArtistTopTracks(artist, 5, 0);
            observableList.add(topTracksRootObservable);
        }
        Observable.zip(observableList, topTracksRoots -> {
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
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::success, throwable -> {
                    throwable.printStackTrace();
                    callback.failure(throwable.getMessage());
                });
    }

}
