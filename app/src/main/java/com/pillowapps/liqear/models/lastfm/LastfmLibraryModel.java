package com.pillowapps.liqear.models.lastfm;

import android.content.Context;

import com.pillowapps.liqear.activities.modes.viewers.LastfmUserViewerActivity;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.LastfmTrackArtistStruct;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmLovedTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmWeeklyTrackChartRoot;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LastfmLibraryModel {

    private LastfmUserModel lastfmUserModel;
    private Context context;

    @Inject
    public LastfmLibraryModel(Context context, LastfmUserModel lastfmUserModel) {
        this.context = context;
        this.lastfmUserModel = lastfmUserModel;
    }

    public void getRadiomix(final String user, final SimpleCallback<List<LastfmTrack>> callback) {
        Observable<LastfmWeeklyTrackChartRoot> weeklyObservable = lastfmUserModel.getWeeklyTracksChart(user);
        Observable<LastfmTopTracksRoot> topTracksObservable = lastfmUserModel.getTopTracks(user,
                Constants.PERIODS_ARRAY[0],
                SharedPreferencesManager.getModePreferences(context).getInt(Constants.TOP_IN_RADIOMIX, 100),
                1);
        Observable<LastfmLovedTracksRoot> lovedObservable = lastfmUserModel.getLovedTracks(user,
                SharedPreferencesManager.getModePreferences(context).getInt(Constants.TOP_IN_RADIOMIX, 100),
                1);
        Observable.zip(weeklyObservable, topTracksObservable, lovedObservable,
                (weeklyTrackChartRoot, topTracksRoot, lovedTracksRoot) -> {
                    HashSet<LastfmTrack> tracks = new LinkedHashSet<>();
                    List<LastfmTrackArtistStruct> weeklyTracks = weeklyTrackChartRoot.getTracks().getTracks();
                    tracks.addAll(Converter.convertLastfmTracksArtistStruct(weeklyTracks));
                    List<LastfmTrack> topTracks = topTracksRoot.getTracks().getTracks();
                    tracks.addAll(topTracks);
                    List<LastfmTrack> lovedTracks = topTracksRoot.getTracks().getTracks();
                    tracks.addAll(lovedTracks);
                    ArrayList<LastfmTrack> resultList = new ArrayList<>(tracks);
                    Collections.shuffle(resultList);
                    return resultList;
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<LastfmTrack>>() {
                    @Override
                    public void call(List<LastfmTrack> tracks) {
                        callback.success(tracks);
                    }
                }, throwable -> {
                    callback.failure(throwable.getMessage());
                });
    }

    public void getLibrary(final String user, final SimpleCallback<List<LastfmTrack>> callback) {
        Observable<LastfmTopTracksRoot> topTracksObservable = lastfmUserModel.getTopTracks(user,
                Constants.PERIODS_ARRAY[0],
                SharedPreferencesManager.getModePreferences(context).getInt(Constants.TOP_IN_RADIOMIX, 100),
                1);
        Observable<LastfmLovedTracksRoot> lovedObservable = lastfmUserModel.getLovedTracks(user,
                SharedPreferencesManager.getModePreferences(context).getInt(Constants.TOP_IN_RADIOMIX, 100),
                1);
        Observable.zip(topTracksObservable, lovedObservable, (topTracksRoot, lovedTracksRoot) -> {
            HashSet<LastfmTrack> tracks = new LinkedHashSet<>();
            List<LastfmTrack> topTracks = topTracksRoot.getTracks().getTracks();
            tracks.addAll(topTracks);
            List<LastfmTrack> lovedTracks = topTracksRoot.getTracks().getTracks();
            tracks.addAll(lovedTracks);
            ArrayList<LastfmTrack> resultList = new ArrayList<>(tracks);
            Collections.shuffle(resultList);
            return resultList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<LastfmTrack>>() {
                    @Override
                    public void call(List<LastfmTrack> tracks) {
                        callback.success(tracks);
                    }
                }, throwable -> {
                    callback.failure(throwable.getMessage());
                });
    }
}
