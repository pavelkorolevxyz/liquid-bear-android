package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.callbacks.LastfmCallback;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmSimilarArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopAlbumsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTracksRoot;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.StringUtils;
import com.pillowapps.liqear.network.Parser;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.service.LastfmApiService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import inaka.com.tinytask.DoThis;
import inaka.com.tinytask.TinyTask;
import rx.Observable;

public class LastfmArtistModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();

    public void getArtistTopTracks(Artist artist, int limit, int page,
                                   final SimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getArtistTopTracks(
                artist.getName(),
                limit,
                page,
                new LastfmCallback<LastfmTopTracksRoot>() {
                    @Override
                    public void success(LastfmTopTracksRoot topTracksRoot) {
                        callback.success(topTracksRoot.getTracks().getTracks());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getArtistAlbums(String artistName, final SimpleCallback<List<LastfmAlbum>> callback) {
        lastfmService.getArtistTopAlbums(
                artistName,
                new LastfmCallback<LastfmTopAlbumsRoot>() {
                    @Override
                    public void success(LastfmTopAlbumsRoot albumsRoot) {
                        callback.success(Converter.convertTopAlbums(albumsRoot.getAlbums().getAlbums()));
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getPersonalArtistTop(String artist, String username, int limit, int page,
                                     final SimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getPersonalArtistTopTracks(
                artist,
                username,
                limit,
                page,
                new LastfmCallback<LastfmTracksRoot>() {
                    @Override
                    public void success(LastfmTracksRoot tracksRoot) {
                        callback.success(tracksRoot.getTracks().getTracks());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getSimilarArtists(String artistName, int limit, int page,
                                  final SimpleCallback<List<LastfmArtist>> callback) {
        lastfmService.getSimilarArtists(
                artistName,
                limit,
                page,
                new LastfmCallback<LastfmSimilarArtistsRoot>() {
                    @Override
                    public void success(LastfmSimilarArtistsRoot root) {
                        callback.success(root.getArtists().getArtists());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getArtistInfo(String artist, String username,
                              final SimpleCallback<LastfmArtist> callback) {
        String lang = Locale.getDefault().getLanguage();
        lastfmService.getArtistInfo(artist, username, lang, new LastfmCallback<LastfmArtistRoot>() {
            @Override
            public void success(LastfmArtistRoot root) {
                callback.success(root.getArtist());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void searchArtist(String query, int limit, int page,
                             final SimpleCallback<List<LastfmArtist>> callback) {
        lastfmService.searchArtist(query, limit, page,
                new LastfmCallback<LastfmArtistSearchResultsRoot>() {
                    @Override
                    public void success(LastfmArtistSearchResultsRoot root) {
                        callback.success(root.getResults().getArtists().getArtists());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                });
    }

    public Observable<LastfmTopTracksRoot> getArtistTopTracks(Artist artist, int limit, int page) {
        return lastfmService.getArtistTopTracks(
                artist.getName(),
                limit,
                page
        );
    }

    @SuppressWarnings("unchecked")
    public void getArtistImages(String artistName, int page, final SimpleCallback<List<String>> callback) {
        final String url = String.format("http://www.lastfm.ru/music/%s/+images?page=%d",
                StringUtils.encode(artistName), page);
        final OkHttpClient client = new OkHttpClient();
        TinyTask.perform(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException("Loading images failed");
            return new Parser().parseGetArtistImages(response);
        }).whenDone(new DoThis<List<String>>() {
            @Override
            public void ifOK(List<String> imagesList) {
                callback.success(imagesList);
            }

            @Override
            public void ifNotOK(Exception e) {
                callback.failure(e.getMessage());
            }
        }).go();
    }
}
