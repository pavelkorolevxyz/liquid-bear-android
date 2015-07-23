package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.callbacks.LastfmCallback;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTrackRoot;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.LastfmApiHelper;
import com.pillowapps.liqear.helpers.LastfmCallbackUtils;
import com.pillowapps.liqear.helpers.Utils;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class LastfmTrackModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();
    private LastfmApiHelper apiHelper = new LastfmApiHelper();

    public void love(Track track, final SimpleCallback<Object> callback) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();
        String artist = track.getArtist();
        String title = track.getTitle();

        Map<String, String> params = new TreeMap<>();
        params.put("artist", artist);
        params.put("track", title);
        params.put("sk", sessionKey);
        params.put("method", "track.love");

        lastfmService.love(artist,
                title,
                apiHelper.generateApiSig(params),
                sessionKey,
                LastfmCallbackUtils.createTransitiveCallback(callback));
    }

    public void unlove(Track track, final SimpleCallback<Object> callback) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();
        String artist = track.getArtist();
        String title = track.getTitle();

        Map<String, String> params = new TreeMap<>();
        params.put("artist", artist);
        params.put("track", title);
        params.put("sk", sessionKey);
        params.put("method", "track.unlove");

        lastfmService.unlove(artist,
                title,
                apiHelper.generateApiSig(params),
                sessionKey,
                LastfmCallbackUtils.createTransitiveCallback(callback));
    }

    public void scrobble(String artist, String title, String album, String timestamp,
                         final SimpleCallback<Object> callback) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();
        Map<String, String> params = new TreeMap<>();
        params.put("artist", artist);
        params.put("track", title);
        params.put("timestamp", timestamp);
        params.put("sk", sessionKey);
        params.put("method", "track.scrobble");
        String apiSig = apiHelper.generateApiSig(params);
        if (album != null) {
            params.put("album", album);
            lastfmService.scrobble(artist,
                    title,
                    album,
                    timestamp,
                    apiSig,
                    sessionKey,
                    LastfmCallbackUtils.createTransitiveCallback(callback));
        } else {
            lastfmService.scrobble(artist,
                    title,
                    timestamp,
                    apiSig,
                    sessionKey,
                    LastfmCallbackUtils.createTransitiveCallback(callback));
        }
    }

    public Observable<LastfmResponse> scrobble(String artist, String title, String album,
                                               String timestamp) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();
        Map<String, String> params = new TreeMap<>();
        params.put("artist", artist);
        params.put("track", title);
        params.put("timestamp", timestamp);
        params.put("sk", sessionKey);
        params.put("method", "track.scrobble");
        if (album != null) {
            params.put("album", album);
            return lastfmService.scrobble(artist,
                    title,
                    album,
                    timestamp,
                    apiHelper.generateApiSig(params),
                    sessionKey);
        } else {
            return lastfmService.scrobble(artist,
                    title,
                    timestamp,
                    apiHelper.generateApiSig(params),
                    sessionKey);
        }
    }

    public void nowplaying(Track track, final SimpleCallback<Object> callback) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();
        Map<String, String> params = new TreeMap<>();
        String artist = track.getArtist();
        String title = track.getTitle();
        String album = track.getAlbum();
        params.put("artist", artist);
        params.put("track", title);
        params.put("sk", sessionKey);
        params.put("method", "track.updateNowPlaying");
        if (album != null) {
            params.put("album", album);
            lastfmService.nowplaying(artist,
                    title,
                    album,
                    apiHelper.generateApiSig(params),
                    sessionKey,
                    LastfmCallbackUtils.createTransitiveCallback(callback));
        } else {
            lastfmService.nowplaying(artist,
                    title,
                    apiHelper.generateApiSig(params),
                    sessionKey,
                    LastfmCallbackUtils.createTransitiveCallback(callback));
        }
    }

    public void getTrackInfo(Track track, String username, final SimpleCallback<LastfmTrack> callback) {
        lastfmService.getTrackInfo(track.getArtist(), track.getTitle(), username,
                new LastfmCallback<LastfmTrackRoot>() {
                    @Override
                    public void success(LastfmTrackRoot data) {
                        callback.success(data.getTrack());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                });
    }

    public void scrobbleBunchOfTracks(List<Track> tracks) {
        if (tracks == null || tracks.size() == 0) return;

        // todo divide tracks by 50 in one request

        List<Observable<LastfmResponse>> observableList = new ArrayList<>(tracks.size());
        for (Track track : tracks) {
            Observable<LastfmResponse> topTracksRootObservable = scrobble(track.getArtist(),
                    track.getTitle(), track.getAlbum(), Utils.getCurrentTime());
            observableList.add(topTracksRootObservable);
        }
        Observable.zip(observableList, new FuncN<Object>() {
            @Override
            public Object call(Object... response) {
                return response;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
