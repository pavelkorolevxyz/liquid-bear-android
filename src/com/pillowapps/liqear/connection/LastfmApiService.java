package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.models.lastfm.LastfmArtist;
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
import com.pillowapps.liqear.models.lastfm.roots.LastfmSimilarArtistsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTagSearchResultsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTopAlbumsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTopArtistsRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.models.lastfm.roots.LastfmTracksRoot;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface LastfmApiService {

    @GET("/?method=user.getNeighbours")
    public void getNeighbours(@Query("user") String user,
                              @Query("limit") int limit,
                              Callback<LastfmNeighboursRoot> callback);

    @GET("/?method=user.getRecommendedArtists")
    public void getRecommendedArtists(@Query("limit") int limit,
                                      @Query("page") int page,
                                      @Query("api_sig") String apiSig,
                                      @Query("sk") String sessionKey,
                                      Callback<List<LastfmArtist>> callback);

    @GET("/?method=user.getTopArtists")
    public void getUserTopArtists(@Query("user") String user,
                                  @Query("period") String period,
                                  @Query("limit") int limit,
                                  @Query("page") int page,
                                  Callback<LastfmTopArtistsRoot> callback);

    @GET("/?method=artist.getSimilar")
    public void getSimilarArtists(@Query("artist") String artist,
                                  @Query("limit") int limit,
                                  @Query("page") int page,
                                  Callback<LastfmSimilarArtistsRoot> callback);

    @GET("/?method=artist.search")
    public void searchArtist(@Query("artist") String artist,
                             @Query("limit") int limit,
                             @Query("page") int page,
                             Callback<LastfmArtistSearchResultsRoot> callback);

    @GET("/?method=tag.search")
    public void searchTag(@Query("tag") String tag,
                          @Query("limit") int limit,
                          @Query("page") int page,
                          Callback<LastfmTagSearchResultsRoot> callback);

    @GET("/?method=album.search")
    public void searchAlbum(@Query("album") String album,
                            @Query("limit") int limit,
                            @Query("page") int page,
                            Callback<LastfmAlbumSearchResultsRoot> callback);

    @GET("/?method=chart.getTopArtists")
    public void getChartTopArtists(@Query("limit") int limit,
                                   @Query("page") int page,
                                   Callback<LastfmArtistsRoot> callback);

    @GET("/?method=chart.getHypedArtists")
    public void getChartHypedArtists(@Query("limit") int limit,
                                     @Query("page") int page,
                                     Callback<LastfmArtistsRoot> callback);

    @GET("/?method=user.getFriends")
    public void getFriends(@Query("user") String user,
                           @Query("limit") int limit,
                           @Query("page") int page,
                           Callback<LastfmFriendsRoot> callback);

    @GET("/?method=tag.getTopTracks")
    public void getTagTopTracks(@Query("tag") String tag,
                                @Query("limit") int limit,
                                @Query("page") int page,
                                Callback<LastfmTopTracksRoot> callback);

    @GET("/?method=artist.getTopTracks")
    public void getArtistTopTracks(@Query("artist") String artist,
                                   @Query("limit") int limit,
                                   @Query("page") int page,
                                   Callback<LastfmTopTracksRoot> callback);

    @GET("/?method=library.getTopTracks")
    public void getLibraryTracks(@Query("user") String user,
                                 @Query("limit") int limit,
                                 @Query("page") int page,
                                 Callback<List<LastfmTrack>> callback);

    @GET("/?method=library.getTracks")
    public void getPersonalArtistTopTracks(@Query("artist") String artist,
                                           @Query("user") String user,
                                           @Query("limit") int limit,
                                           @Query("page") int page,
                                           Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getTopTracks")
    public void getChartTopTracks(@Query("limit") int limit,
                                  @Query("page") int page,
                                  Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getLovedTracks")
    public void getChartLovedTracks(@Query("limit") int limit,
                                    @Query("page") int page,
                                    Callback<LastfmTracksRoot> callback);

    @GET("/?method=chart.getTopTags")
    public void getChartTopTags(@Query("limit") int limit,
                                @Query("page") int page,
                                Callback<List<LastfmTag>> callback);

    @GET("/?method=chart.getHypedTracks")
    public void getChartHypedTracks(@Query("limit") int limit,
                                    @Query("page") int page,
                                    Callback<LastfmTracksRoot> callback);

    @GET("/?method=user.getRecentTracks")
    public void getRecentTracks(@Query("user") String user,
                                @Query("limit") int limit,
                                @Query("page") int page,
                                Callback<LastfmRecentTracksRoot> callback);

    @GET("/?method=album.getInfo")
    public void getAlbumInfo(@Query("artist") String artist,
                             @Query("album") String album,
                             Callback<LastfmAlbumRoot> callback);

    @GET("/?method=artist.getInfo")
    public void getArtistInfo(@Query("artist") String artist,
                              @Query("user") String user,
                              Callback<LastfmArtistRoot> callback);

    @GET("/?method=user.getInfo")
    public void getUserInfo(@Query("user") String user,
                            Callback<LastfmUser> callback);

    @GET("/?method=track.getInfo")
    public void getTrackInfo(@Query("artist") String artist,
                             @Query("track") String track,
                             Callback<LastfmTrack> callback);

    @GET("/?method=tag.getTopTags")
    public void getTopTags(Callback<LastfmTag> callback);

    @POST("?method=track.love")
    public void love(@Query("artist") String artist,
                     @Query("track") String track,
                     @Query("api_sig") String apiSig,
                     @Query("sk ") String sessionKey,
                     Callback<Object> callback);

    @POST("?method=track.unlove")
    public void unlove(@Query("artist") String artist,
                       @Query("track") String track,
                       @Query("api_sig") String apiSig,
                       @Query("sk ") String sessionKey,
                       Callback<Object> callback);

    @POST("?method=track.updateNowPlaying")
    public void nowplaying(@Query("artist") String artist,
                           @Query("track") String track,
                           @Query("api_sig") String apiSig,
                           @Query("sk ") String sessionKey,
                           Callback<Object> callback);

    @POST("?method=track.updateNowPlaying")
    public void nowplaying(@Query("artist") String artist,
                           @Query("track") String track,
                           @Query("album") String album,
                           @Query("api_sig") String apiSig,
                           @Query("sk ") String sessionKey,
                           Callback<Object> callback);

    @POST("?method=track.scrobble")
    public void scrobble(@Query("artist") String artist,
                         @Query("track") String track,
                         @Query("album") String album,
                         @Query("timestamp") String timestamp,
                         @Query("api_sig") String apiSig,
                         @Query("sk ") String sessionKey,
                         Callback<Object> callback);

    @GET("/?method=artist.getTopAlbums")
    public void getArtistTopAlbums(@Query("artist") String artist,
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
}
