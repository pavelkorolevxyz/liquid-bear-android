package com.pillowapps.liqear.models;

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
import com.pillowapps.liqear.network.LastfmCallback;
import com.pillowapps.liqear.network.LastfmSimpleCallback;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;
import java.util.Locale;

public class LastfmArtistModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();

    public void getArtistTopTracks(Artist artist, int limit, int page,
                                   final LastfmSimpleCallback<List<LastfmTrack>> callback) {
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

    public void getArtistAlbums(String artistName, final LastfmSimpleCallback<List<LastfmAlbum>> callback) {
        lastfmService.getArtistTopAlbums(
                artistName,
                new LastfmCallback<LastfmTopAlbumsRoot>() {
                    @Override
                    public void success(LastfmTopAlbumsRoot albumsRoot) {
                        callback.success(albumsRoot.getAlbums().getAlbums());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getPersonalArtistTop(String artist, String username, int limit, int page,
                                     final LastfmSimpleCallback<List<LastfmTrack>> callback) {
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
                                  final LastfmSimpleCallback<List<LastfmArtist>> callback) {
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
                              final LastfmSimpleCallback<LastfmArtist> callback) {
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
                             final LastfmSimpleCallback<List<LastfmArtist>> callback) {
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
}
