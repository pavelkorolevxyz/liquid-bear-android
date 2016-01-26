package com.pillowapps.liqear.network.service;

import com.pillowapps.liqear.callbacks.retrofit.LastfmCallback;
import com.pillowapps.liqear.entities.lastfm.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmTag;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmAlbumSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmFriendsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmLovedTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmNeighboursRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmRecentTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmRecommendationsArtistRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmSimilarArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTagSearchResultsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopAlbumsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTrackRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmUserRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmWeeklyTrackChartRoot;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface LastfmApiService {

    @GET("/?method=user.getNeighbours")
    void getNeighbours(@Query("user") String user,
                       @Query("limit") int limit,
                       Callback<LastfmNeighboursRoot> callback);

    @GET("/?method=user.getRecommendedArtists")
    void getRecommendedArtists(@Query("limit") int limit,
                               @Query("page") int page,
                               @Query("api_sig") String apiSig,
                               @Query("sk") String sessionKey,
                               LastfmCallback<LastfmRecommendationsArtistRoot> callback);

    @GET("/?method=user.getTopArtists")
    void getUserTopArtists(@Query("user") String user,
                           @Query("period") String period,
                           @Query("limit") int limit,
                           @Query("page") int page,
                           Callback<LastfmTopArtistsRoot> callback);

    @GET("/?method=artist.getSimilar")
    void getSimilarArtists(@Query("artist") String artist,
                           @Query("limit") int limit,
                           @Query("page") int page,
                           Callback<LastfmSimilarArtistsRoot> callback);

    @GET("/?method=artist.search")
    void searchArtist(@Query("artist") String artist,
                      @Query("limit") int limit,
                      @Query("page") int page,
                      Callback<LastfmArtistSearchResultsRoot> callback);

    @GET("/?method=tag.search")
    void searchTag(@Query("tag") String tag,
                   @Query("limit") int limit,
                   @Query("page") int page,
                   Callback<LastfmTagSearchResultsRoot> callback);

    @GET("/?method=album.search")
    void searchAlbum(@Query("album") String album,
                     @Query("limit") int limit,
                     @Query("page") int page,
                     Callback<LastfmAlbumSearchResultsRoot> callback);

    @GET("/?method=chart.getTopArtists")
    void getChartTopArtists(@Query("limit") int limit,
                            @Query("page") int page,
                            Callback<LastfmArtistsRoot> callback);

    @GET("/?method=chart.getHypedArtists")
    void getChartHypedArtists(@Query("limit") int limit,
                              @Query("page") int page,
                              Callback<LastfmArtistsRoot> callback);

    @GET("/?method=user.getFriends")
    void getFriends(@Query("user") String user,
                    @Query("limit") int limit,
                    @Query("page") int page,
                    Callback<LastfmFriendsRoot> callback);

    @GET("/?method=tag.getTopTracks")
    void getTagTopTracks(@Query("tag") String tag,
                         @Query("limit") int limit,
                         @Query("page") int page,
                         Callback<LastfmTopTracksRoot> callback);

    @GET("/?method=artist.getTopTracks")
    void getArtistTopTracks(@Query("artist") String artist,
                            @Query("limit") int limit,
                            @Query("page") int page,
                            Callback<LastfmTopTracksRoot> callback);

    @GET("/?method=library.getTopTracks")
    void getLibraryTracks(@Query("user") String user,
                          @Query("limit") int limit,
                          @Query("page") int page,
                          Callback<List<LastfmTrack>> callback);

    @GET("/?method=library.getTracks")
    void getPersonalArtistTopTracks(@Query("artist") String artist,
                                    @Query("user") String user,
                                    @Query("limit") int limit,
                                    @Query("page") int page,
                                    Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getTopTracks")
    void getChartTopTracks(@Query("limit") int limit,
                           @Query("page") int page,
                           Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getLovedTracks")
    void getChartLovedTracks(@Query("limit") int limit,
                             @Query("page") int page,
                             Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getTopTags")
    void getChartTopTags(@Query("limit") int limit,
                         @Query("page") int page,
                         Callback<List<LastfmTag>> callback);

    @GET("/?method=chart.getHypedTracks")
    void getChartHypedTracks(@Query("limit") int limit,
                             @Query("page") int page,
                             Callback<LastfmTracksRoot> callback);

    @GET("/?method=user.getRecentTracks")
    void getRecentTracks(@Query("user") String user,
                         @Query("limit") int limit,
                         @Query("page") int page,
                         Callback<LastfmRecentTracksRoot> callback);

    @GET("/?method=album.getInfo")
    void getAlbumInfo(@Query("artist") String artist,
                      @Query("album") String album,
                      Callback<LastfmAlbumRoot> callback);

    @GET("/?method=artist.getInfo")
    void getArtistInfo(@Query("artist") String artist,
                       @Query("user") String user,
                       @Query("lang") String language,
                       Callback<LastfmArtistRoot> callback);

    @GET("/?method=user.getInfo")
    void getUserInfo(@Query("user") String user,
                     LastfmCallback<LastfmUserRoot> callback);

    @GET("/?method=track.getInfo")
    void getTrackInfo(@Query("artist") String artist,
                      @Query("track") String track,
                      @Query("username") String username,
                      LastfmCallback<LastfmTrackRoot> callback);

    @GET("/?method=tag.getTopTags")
    void getTopTags(Callback<LastfmTag> callback);

    @POST("/?method=track.love")
    void love(@Query("artist") String artist,
              @Query("track") String track,
              @Query("api_sig") String apiSig,
              @Query("sk") String sessionKey,
              LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.unlove")
    void unlove(@Query("artist") String artist,
                @Query("track") String track,
                @Query("api_sig") String apiSig,
                @Query("sk") String sessionKey,
                LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.updateNowPlaying")
    void nowplaying(@Query("artist") String artist,
                    @Query("track") String track,
                    @Query("api_sig") String apiSig,
                    @Query("sk") String sessionKey,
                    LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.updateNowPlaying")
    void nowplaying(@Query("artist") String artist,
                    @Query("track") String track,
                    @Query("album") String album,
                    @Query("api_sig") String apiSig,
                    @Query("sk") String sessionKey,
                    LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.scrobble")
    void scrobble(@Query("artist") String artist,
                  @Query("track") String track,
                  @Query("album") String album,
                  @Query("timestamp") String timestamp,
                  @Query("api_sig") String apiSig,
                  @Query("sk") String sessionKey,
                  LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.scrobble")
    void scrobble(@Query("artist") String artist,
                  @Query("track") String track,
                  @Query("timestamp") String timestamp,
                  @Query("api_sig") String apiSig,
                  @Query("sk") String sessionKey,
                  LastfmCallback<LastfmResponse> callback);

    @POST("/?method=track.scrobble")
    Observable<LastfmResponse> scrobble(@Query("artist") String artist,
                                        @Query("track") String track,
                                        @Query("album") String album,
                                        @Query("timestamp") String timestamp,
                                        @Query("api_sig") String apiSig,
                                        @Query("sk") String sessionKey);

    @POST("/?method=track.scrobble")
    Observable<LastfmResponse> scrobble(@Query("artist") String artist,
                                        @Query("track") String track,
                                        @Query("timestamp") String timestamp,
                                        @Query("api_sig") String apiSig,
                                        @Query("sk") String sessionKey);

    @GET("/?method=artist.getTopAlbums")
    void getArtistTopAlbums(@Query("artist") String artist,
                            Callback<LastfmTopAlbumsRoot> callback);

    @GET("/?method=user.getTopTracks")
    void getUserTopTracks(@Query("user") String user,
                          @Query("period") String period,
                          @Query("limit") int limit,
                          @Query("page") int page,
                          Callback<LastfmTopTracksRoot> callback);

    @GET("/?method=user.getLovedTracks")
    void getLovedTracks(@Query("user") String user,
                        @Query("limit") int limit,
                        @Query("page") int page,
                        Callback<LastfmLovedTracksRoot> callback);

    @GET("/?method=user.getWeeklyTrackChart")
    Observable<LastfmWeeklyTrackChartRoot> getWeeklyTracksChart(@Query("user") String user);

    @GET("/?method=user.getTopTracks")
    Observable<LastfmTopTracksRoot> getUserTopTracks(@Query("user") String user,
                                                     @Query("period") String period,
                                                     @Query("limit") int limit,
                                                     @Query("page") int page);

    @GET("/?method=user.getLovedTracks")
    Observable<LastfmLovedTracksRoot> getLovedTracks(@Query("user") String user,
                                                     @Query("limit") int limit,
                                                     @Query("page") int page);

    @GET("/?method=album.getInfo")
    Observable<LastfmAlbumRoot> getAlbumInfo(@Query("artist") String artist,
                                             @Query("album") String album);

    @GET("/?method=artist.getTopTracks")
    Observable<LastfmTopTracksRoot> getArtistTopTracks(@Query("artist") String artist,
                                                       @Query("limit") int limit,
                                                       @Query("page") int page);
}
