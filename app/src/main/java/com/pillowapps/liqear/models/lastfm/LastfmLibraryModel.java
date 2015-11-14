package com.pillowapps.liqear.models.lastfm;

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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class LastfmLibraryModel {

    public void getRadiomix(final String user, final SimpleCallback<List<LastfmTrack>> callback) {
        Observable<LastfmWeeklyTrackChartRoot> weeklyObservable = new LastfmUserModel().getWeeklyTracksChart(user);
        Observable<LastfmTopTracksRoot> topTracksObservable = new LastfmUserModel().getTopTracks(user,
                Constants.PERIODS_ARRAY[0],
                SharedPreferencesManager.getModePreferences().getInt(Constants.TOP_IN_RADIOMIX, 100),
                1);
        Observable<LastfmLovedTracksRoot> lovedObservable = new LastfmUserModel().getLovedTracks(user,
                SharedPreferencesManager.getModePreferences().getInt(Constants.TOP_IN_RADIOMIX, 100),
                1);
        Observable.zip(weeklyObservable, topTracksObservable, lovedObservable,
                new Func3<LastfmWeeklyTrackChartRoot, LastfmTopTracksRoot, LastfmLovedTracksRoot, List<LastfmTrack>>() {
                    @Override
                    public List<LastfmTrack> call(LastfmWeeklyTrackChartRoot weeklyTrackChartRoot,
                                                  LastfmTopTracksRoot topTracksRoot,
                                                  LastfmLovedTracksRoot lovedTracksRoot) {
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
                        callback.failure(throwable.getMessage());
                    }
                });
    }

    public void getLibrary(final String user, final SimpleCallback<List<LastfmTrack>> callback) {
        Observable<LastfmTopTracksRoot> topTracksObservable = new LastfmUserModel().getTopTracks(user,
                Constants.PERIODS_ARRAY[0],
                SharedPreferencesManager.getModePreferences().getInt(Constants.TOP_IN_RADIOMIX, 100),
                1);
        Observable<LastfmLovedTracksRoot> lovedObservable = new LastfmUserModel().getLovedTracks(user,
                SharedPreferencesManager.getModePreferences().getInt(Constants.TOP_IN_RADIOMIX, 100),
                1);
        Observable.zip(topTracksObservable, lovedObservable,
                new Func2<LastfmTopTracksRoot, LastfmLovedTracksRoot, List<LastfmTrack>>() {
                    @Override
                    public List<LastfmTrack> call(LastfmTopTracksRoot topTracksRoot,
                                                  LastfmLovedTracksRoot lovedTracksRoot) {
                        HashSet<LastfmTrack> tracks = new LinkedHashSet<>();
                        List<LastfmTrack> topTracks = topTracksRoot.getTracks().getTracks();
                        tracks.addAll(topTracks);
                        List<LastfmTrack> lovedTracks = topTracksRoot.getTracks().getTracks();
                        tracks.addAll(lovedTracks);
                        ArrayList<LastfmTrack> resultList = new ArrayList<>(tracks);
                        Collections.shuffle(resultList);
                        return resultList;
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
                        callback.failure(throwable.getMessage());
                    }
                });
    }
}
