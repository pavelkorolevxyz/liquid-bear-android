package com.pillowapps.liqear.network;

import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.StringUtils;
import com.pillowapps.liqear.models.Album;
import com.pillowapps.liqear.models.Artist;
import com.pillowapps.liqear.models.Track;
import com.pillowapps.liqear.models.lastfm.LastfmAlbum;
import com.pillowapps.liqear.models.lastfm.LastfmArtist;
import com.pillowapps.liqear.models.lastfm.LastfmSession;
import com.pillowapps.liqear.models.lastfm.LastfmTag;
import com.pillowapps.liqear.models.lastfm.LastfmTrack;
import com.pillowapps.liqear.models.lastfm.LastfmUser;
import com.pillowapps.liqear.models.lastfm.roots.LastfmAlbumRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmAlbumSearchResultsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmArtistRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmArtistSearchResultsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmArtistsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmFriendsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmLovedTracksRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmNeighboursRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmRecentTracksRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmSessionRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmSimilarArtistsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTagSearchResultsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTopAlbumsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTopArtistsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTracksRoot;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LastfmRequestManager {

    public static LastfmRequestManager instance;
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();

    private LastfmRequestManager() {

    }

    public static LastfmRequestManager getInstance() {
        if (instance == null) {
            instance = new LastfmRequestManager();
        }
        return instance;
    }

    private String generateApiSig(Map<String, String> params) {
        StringBuilder b = new StringBuilder();
        params.put("api_key", ServiceHelper.LASTFM_API_KEY);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            b.append(entry.getKey());
            b.append(entry.getValue());
        }
        b.append(ServiceHelper.LASTFM_API_SECRET);
        return StringUtils.md5(b.toString());
    }

    public void getAlbumInfo(Album album, final Callback<LastfmAlbum> callback) {
        lastfmService.getAlbumInfo(
                album.getArtist(),
                album.getTitle(),
                new Callback<LastfmAlbumRoot>() {
                    @Override
                    public void success(LastfmAlbumRoot lastfmAlbumRoot, Response response) {
                        callback.success(lastfmAlbumRoot.getAlbum(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getArtistTopTracks(Artist artist, int limit, int page,
                                   final Callback<List<LastfmTrack>> callback) {
        lastfmService.getArtistTopTracks(
                artist.getName(),
                limit,
                page,
                new Callback<LastfmTopTracksRoot>() {
                    @Override
                    public void success(LastfmTopTracksRoot topTracksRoot, Response response) {
                        callback.success(topTracksRoot.getTracks().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getArtistAlbums(String artistName, final Callback<List<LastfmAlbum>> callback) {
        lastfmService.getArtistTopAlbums(
                artistName,
                new Callback<LastfmTopAlbumsRoot>() {
                    @Override
                    public void success(LastfmTopAlbumsRoot albumsRoot, Response response) {
                        callback.success(albumsRoot.getAlbums().getAlbums(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getPersonalArtistTop(String artist, String username, int limit, int page,
                                     final Callback<List<LastfmTrack>> callback) {
        lastfmService.getPersonalArtistTopTracks(
                artist,
                username,
                limit,
                page,
                new Callback<LastfmTracksRoot>() {
                    @Override
                    public void success(LastfmTracksRoot tracksRoot, Response response) {
                        callback.success(tracksRoot.getTracks().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getSimilarArtists(String artistName, int limit, int page,
                                  final Callback<List<LastfmArtist>> callback) {
        lastfmService.getSimilarArtists(
                artistName,
                limit,
                page,
                new Callback<LastfmSimilarArtistsRoot>() {
                    @Override
                    public void success(LastfmSimilarArtistsRoot root, Response response) {
                        callback.success(root.getArtists().getArtists(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getArtistInfo(String artist, String username,
                              final Callback<LastfmArtist> callback) {
        String lang = Locale.getDefault().getLanguage();
        lastfmService.getArtistInfo(artist, username, lang, new Callback<LastfmArtistRoot>() {
            @Override
            public void success(LastfmArtistRoot root, Response response) {
                callback.success(root.getArtist(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getHypedArtists(int limit, int page, final Callback<List<LastfmArtist>> callback) {
        lastfmService.getChartHypedArtists(limit, page, new Callback<LastfmArtistsRoot>() {
            @Override
            public void success(LastfmArtistsRoot root, Response response) {
                callback.success(root.getArtists().getArtists(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getTopArtists(int limit, int page, final Callback<List<LastfmArtist>> callback) {
        lastfmService.getChartTopArtists(limit, page, new Callback<LastfmArtistsRoot>() {
            @Override
            public void success(LastfmArtistsRoot root, Response response) {
                callback.success(root.getArtists().getArtists(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getLovedTracksChart(int limit, int page,
                                    final Callback<List<LastfmTrack>> callback) {
        lastfmService.getChartLovedTracks(limit, page, new Callback<LastfmTracksRoot>() {
            @Override
            public void success(LastfmTracksRoot root, Response response) {
                callback.success(root.getTracks().getTracks(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getTopTracksChart(int limit, int page, final Callback<List<LastfmTrack>> callback) {
        lastfmService.getChartTopTracks(limit, page, new Callback<LastfmTracksRoot>() {
            @Override
            public void success(LastfmTracksRoot root, Response response) {
                callback.success(root.getTracks().getTracks(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getHypedTracks(int limit, int page, final Callback<List<LastfmTrack>> callback) {
        lastfmService.getChartHypedTracks(limit, page, new Callback<LastfmTracksRoot>() {
            @Override
            public void success(LastfmTracksRoot root, Response response) {
                callback.success(root.getTracks().getTracks(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getTagTopTracks(String tag, int limit, int page,
                                final Callback<List<LastfmTrack>> callback) {
        lastfmService.getTagTopTracks(tag, limit, page,
                new Callback<LastfmTopTracksRoot>() {
                    @Override
                    public void success(LastfmTopTracksRoot root, Response response) {
                        callback.success(root.getTracks().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
    }

    public void getUserTopArtists(String userName, String period, int limit, int page,
                                  final Callback<List<LastfmArtist>> callback) {
        lastfmService.getUserTopArtists(userName,
                period,
                limit,
                page,
                new Callback<LastfmTopArtistsRoot>() {
                    @Override
                    public void success(LastfmTopArtistsRoot root, Response response) {
                        callback.success(root.getArtists().getArtists(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
    }

    public void getUserRecentTracks(String userName, int limit, int page,
                                    final Callback<List<LastfmTrack>> callback) {
        lastfmService.getRecentTracks(
                userName,
                limit,
                page,
                new Callback<LastfmRecentTracksRoot>() {
                    @Override
                    public void success(LastfmRecentTracksRoot root, Response response) {
                        callback.success(root.getTracks().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getUserTopTracks(String userName, String period, int limit, int page,
                                 final Callback<List<LastfmTrack>> callback) {

        lastfmService.getUserTopTracks(
                userName,
                period,
                limit,
                page,
                new Callback<LastfmTopTracksRoot>() {
                    @Override
                    public void success(LastfmTopTracksRoot root, Response response) {
                        callback.success(root.getTracks().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }


    public void getLovedTracks(String userName, int limit, int page,
                               final Callback<List<LastfmTrack>> callback) {
        lastfmService.getLovedTracks(
                userName,
                limit,
                page,
                new Callback<LastfmLovedTracksRoot>() {
                    @Override
                    public void success(LastfmLovedTracksRoot root, Response response) {
                        callback.success(root.getTracks().getTracks(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getMobileSession(String username, String password, final Callback<LastfmSession> callback) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("method", "auth.getMobileSession");
        ServiceHelper.getLastfmAuthService().getMobileSession(
                username,
                password,
                generateApiSig(params),
                new Callback<LastfmSessionRoot>() {
                    @Override
                    public void success(LastfmSessionRoot root, Response response) {
                        callback.success(root.getSession(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void searchArtist(String query, int limit, int page,
                             final Callback<List<LastfmArtist>> callback) {
        lastfmService.searchArtist(query, limit, page,
                new Callback<LastfmArtistSearchResultsRoot>() {
                    @Override
                    public void success(LastfmArtistSearchResultsRoot root, Response response) {
                        callback.success(root.getResults().getArtists().getArtists(), response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
    }

    public void searchTag(String query, int limit, int page,
                          final Callback<List<LastfmTag>> callback) {
        lastfmService.searchTag(query, limit, page, new Callback<LastfmTagSearchResultsRoot>() {
            @Override
            public void success(LastfmTagSearchResultsRoot root, Response response) {
                callback.success(root.getResults().getTags().getTags(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void searchAlbum(String query, int limit, int page,
                            final Callback<List<LastfmAlbum>> callback) {
        lastfmService.searchAlbum(query, limit, page, new Callback<LastfmAlbumSearchResultsRoot>() {
            @Override
            public void success(LastfmAlbumSearchResultsRoot root, Response response) {
                callback.success(root.getResults().getAlbums().getAlbums(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getLastfmFriends(String username, int limit, int page,
                                 final Callback<List<LastfmUser>> callback) {
        lastfmService.getFriends(username, limit, page, new Callback<LastfmFriendsRoot>() {
            @Override
            public void success(LastfmFriendsRoot lastfmFriendsRoot, Response response) {
                callback.success(lastfmFriendsRoot.getUsers().getUsers(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getNeighbours(String username, int limit,
                              final Callback<List<LastfmUser>> callback) {
        lastfmService.getNeighbours(username, limit, new Callback<LastfmNeighboursRoot>() {
            @Override
            public void success(LastfmNeighboursRoot root, Response response) {
                callback.success(root.getUsers().getUsers(), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void love(Track track, final Callback<Object> callback) {
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
                generateApiSig(params),
                sessionKey,
                callback);
    }

    public void unlove(Track track, final Callback<Object> callback) {
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
                generateApiSig(params),
                sessionKey,
                callback);
    }

    public void scrobble(String artist, String title, String album, String timestamp, final Callback<Object> callback) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();
        Map<String, String> params = new TreeMap<>();
        params.put("artist", artist);
        params.put("track", title);
        params.put("album", album);
        params.put("sk", sessionKey);
        params.put("method", "track.unlove");
        lastfmService.scrobble(artist, title, album, timestamp, generateApiSig(params), sessionKey, callback);
    }

    public void nowplaying(Track track, final Callback<Object> callback) {
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
            lastfmService.nowplaying(artist, title, album, generateApiSig(params), sessionKey, callback);
        } else {
            lastfmService.nowplaying(artist, title, generateApiSig(params), sessionKey, callback);
        }
    }
}
