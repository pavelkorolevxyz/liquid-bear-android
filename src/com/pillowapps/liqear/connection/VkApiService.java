package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.models.lastfm.LastfmAlbum;
import com.pillowapps.liqear.models.User;
import com.pillowapps.liqear.models.VkAlbum;
import com.pillowapps.liqear.models.VkGroup;
import com.pillowapps.liqear.models.VkTrack;
import com.pillowapps.liqear.models.VkWallMessage;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface VkApiService {

    @GET("/wall.get")
    public void getWallMessages(@Query("owner_id") String ownerId,
                                @Query("offset") int offset,
                                @Query("count") int count,
                                Callback<VkWallMessage> callback);

    @GET("/fave.getPosts")
    public void getFavoriteWallMessages(@Query("offset") int offset,
                                        @Query("count") int count,
                                        Callback<VkWallMessage> callback);

    @GET("/newsfeed.get")
    public void getNewsfeedWallMessages(@Query("offset") int offset,
                                        @Query("count") int count,
                                        Callback<VkWallMessage> callback);

    @POST("/wall.post")
    public void postWallMessages(@Query("message") String message,
                                 @Query("attachment") String attachment,
                                 Callback<Object> callback);

    @GET("/audio.get")
    public void getAudio(@Query("owner_id") String ownerId,
                         Callback<List<VkTrack>> callback);

    @GET("/audio.get")
    public void getAudio(@Query("owner_id") String ownerId,
                         @Query("album_id") long albumId,
                         Callback<List<VkTrack>> callback);

    @GET("/audio.search")
    public void searchAudio(@Query("q") String q,
                            @Query("offset") int offset,
                            @Query("count") int count,
                            Callback<List<VkTrack>> callback);

    @GET("/users.get")
    public void getUsers(@Query("user_ids") String uid,
                         @Query("fields") String fields,
                         Callback<List<User>> callback);

    @POST("/status.set")
    public void setAudioStatus(@Query("audio") String audio,
                               Callback<Object> callback);

    @POST("/execute.saps")
    public void setAudioStatusWithSearch(@Query("q") String searchQuery,
                                         Callback<Object> callback);

    @POST("/execute.ta")
    public void addAudioWithSearch(@Query("q") String searchQuery,
                                   Callback<Object> callback);

    @GET("/groups.get")
    public void getGroups(@Query("fields") String fields,
                          @Query("extended") int extended,
                          @Query("offset") int offset,
                          @Query("count") int count,
                          Callback<List<VkGroup>> callback);

    @GET("/audio.getAlbums")
    public void getAlbums(@Query("owner_id") String ownerId,
                          @Query("offset") int offset,
                          @Query("count") int count,
                          Callback<List<VkAlbum>> callback);

    @GET("/audio.getRecommendations")
    public void getRecommendations(@Query("offset") int offset,
                                   @Query("count") int count,
                                   Callback<List<VkTrack>> callback);

    @GET("/friends.get")
    public void getFriends(@Query("user_id") String userId,
                           @Query("fields") String fields,
                           @Query("offset") int offset,
                           @Query("count") int count,
                           Callback<List<LastfmAlbum>> callback);

    @GET("/execute.u")
    public void getTrackUrl(@Query("q") String trackNotation,
                            @Query("n") String offset,
                            Callback<String> callback);

    @GET("/execute.getLyrics")
    public void getLyrics(@Query("q") String trackNotation,
                          @Query("aim") String offset,
                          Callback<String> callback);

    @POST("/photos.saveWallPhoto")
    public void saveWallPhoto(@Query("server") String server,
                              @Query("photo") String photo,
                              @Query("hash") String hash,
                              Callback<Object> callback);

    @GET("/photos.getWallUploadServer")
    public void getWallUploadServer(Callback<Object> callback);
}
