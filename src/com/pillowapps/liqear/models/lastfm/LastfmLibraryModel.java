package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.LastfmTrackArtistStruct;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmWeeklyTrackChartRoot;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.LastfmSimpleCallback;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import timber.log.Timber;

public class LastfmLibraryModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();

    public void getRadiomix(final String user, final LastfmSimpleCallback<List<LastfmTrack>> callback) {
        Observable<LastfmWeeklyTrackChartRoot> observable = new LastfmUserModel().getWeeklyTracksChart(user);
        Observable<LastfmWeeklyTrackChartRoot> observable2 = new LastfmUserModel().getWeeklyTracksChart(user);
        Observable.zip(observable, observable2, new Func2<LastfmWeeklyTrackChartRoot, LastfmWeeklyTrackChartRoot, List<LastfmTrackArtistStruct>>() {
            @Override
            public List<LastfmTrackArtistStruct> call(LastfmWeeklyTrackChartRoot lastfmWeeklyTrackChartRoot, LastfmWeeklyTrackChartRoot lastfmWeeklyTrackChartRoot2) {
                List<LastfmTrackArtistStruct> tracks = new ArrayList<>();
                List<LastfmTrackArtistStruct> tracks1 = lastfmWeeklyTrackChartRoot.getTracks().getTracks();
                Timber.d("result count = " + tracks1.size());
                tracks.addAll(tracks1);
                List<LastfmTrackArtistStruct> tracks2 = lastfmWeeklyTrackChartRoot2.getTracks().getTracks();
                Timber.d("result count = " + tracks2.size());
                tracks.addAll(tracks2);
                Timber.d("result count = " + tracks.size());
                return tracks;
            }
        }).subscribe(new Action1<List<LastfmTrackArtistStruct>>() {
            @Override
            public void call(List<LastfmTrackArtistStruct> tracks) {
                Timber.d("subscribe called " + tracks.toString());
                List<LastfmTrack> lastfmTracks = Converter.convertLastfmTracksArtistStruct(tracks);
                callback.success(lastfmTracks);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                callback.failure(throwable.getMessage());
            }
        });
    }
}
