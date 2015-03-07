package com.pillowapps.liqear.models;

import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.LastfmApiHelper;
import com.pillowapps.liqear.helpers.LastfmCallbackUtils;
import com.pillowapps.liqear.network.callbacks.LastfmSimpleCallback;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.Map;
import java.util.TreeMap;

public class LastfmTrackModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();
    private LastfmApiHelper apiHelper = new LastfmApiHelper();

    public void love(Track track, final LastfmSimpleCallback<Object> callback) {
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

    public void unlove(Track track, final LastfmSimpleCallback<Object> callback) {
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
                         final LastfmSimpleCallback<Object> callback) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();
        Map<String, String> params = new TreeMap<>();
        params.put("artist", artist);
        params.put("track", title);
        params.put("album", album);
        params.put("sk", sessionKey);
        params.put("method", "track.unlove");
        lastfmService.scrobble(artist,
                title,
                album,
                timestamp,
                apiHelper.generateApiSig(params),
                sessionKey,
                LastfmCallbackUtils.createTransitiveCallback(callback));
    }

    public void nowplaying(Track track, final LastfmSimpleCallback<Object> callback) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();
        Map<String, String> params = new TreeMap<>();
        String artist = track.getArtist();
        String title = track.getArtist();
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
}
